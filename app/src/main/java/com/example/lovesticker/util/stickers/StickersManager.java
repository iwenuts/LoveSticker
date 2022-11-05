package com.example.lovesticker.util.stickers;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPStaticUtils;
import com.example.lovesticker.base.LoveStickerApp;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.hawk.Hawk;
import com.alibaba.fastjson.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class StickersManager {
    private volatile static StickersManager mInstance;
    private StickersManager () {
        path = LoveStickerApp.getAppContext().getFilesDir() +"/"+ "stickers_asset" + "/";
    }
    public static StickersManager getInstance() {
        if (mInstance == null) {
        synchronized (StickersManager.class) {
                if (mInstance == null) {
                    mInstance = new StickersManager();
                }
            }
        }
        return mInstance;
    }


    private static String TAG = "StickersManager";
    private static String path;// = LoveStickerApp.getAppContext().getFilesDir() + "stickers_asset" + "/";
    private StickersCallBack mCallBack;
    private int completedNum = 0;
    private int errorNum = 0;
    private int allNum = 0;
    private StickerPacks mStickerPacks;
    final List<BaseDownloadTask> tasks = new ArrayList<>();

    private FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e(TAG, "pending....");
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            Log.e(TAG, "connected....");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e(TAG, "progress....");
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Log.e(TAG, "blockComplete...."+  task.getPath());
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            Log.e(TAG, "retry....");
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.e(TAG, "completed...."+  task.getUrl());
            completedNum ++;
            if (null != mCallBack){
                mCallBack.completed(completedNum, errorNum, allNum);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e(TAG, "paused....");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            errorNum ++;
            if (null != mCallBack ){
                mCallBack.completed(completedNum, errorNum, allNum);
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.e(TAG, "warn....");
        }
    };

    /**
     * 下载数据
     * @param stickerPacks
     * @param callBack
     */
    public static void downloadStickers(StickerPacks stickerPacks, StickersCallBack callBack) {
        getInstance().download(stickerPacks, callBack);
    }

    private void download(StickerPacks stickerPacks, StickersCallBack callBack){
        mCallBack = callBack;
        mStickerPacks = stickerPacks;

        String trayImage = LSConstant.image_uri + stickerPacks.getTrayImageFile();
        List<String> stickersImg = new ArrayList();

        stickersImg.add(trayImage);

        for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
            stickersImg.add(LSConstant.image_uri + stickerPacks.getStickersList().get(i).getImage());
        }

        Log.e(TAG, "download...."+ stickersImg.size());
        getInstance().allNum = stickersImg.size();
        getInstance().completedNum = 0;
        getInstance().errorNum = 0;

        File myTrayr = new File(path + stickerPacks.getIdentifier());
        if (!myTrayr.exists()) {
            myTrayr.mkdirs();
        }

        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(getInstance().downloadListener);


        for (int i = 0; i < stickersImg.size(); i++) {
            String filename = stickersImg.get(i).substring(stickersImg.get(i).lastIndexOf('/')+1);
            String filepath = path+stickerPacks.getIdentifier()+"/"+filename;
            tasks.add(FileDownloader.getImpl().create(stickersImg.get(i)).setPath(filepath).setTag(i + 1));
        }

        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        queueSet.disableCallbackProgressTimes();

        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(3);

        //queueSet.downloadTogether(tasks);
        queueSet.downloadSequentially(tasks);

        // 最后你需要主动调用start方法来启动该Queue
        queueSet.start();
    }

    //
    public static void stopDownload(){
        getInstance().stopAll();
    }

    private void stopAll()
    {
        for (int i=0; i<tasks.size(); ++i){
            tasks.get(i).pause();
        }

        tasks.clear();
    }
    /**
     * 设置sticker_packs数据
     * @return
     */
    public static boolean putStickers(){
        return getInstance().put_stickers();
    }

    private boolean put_stickers(){
        StickerPacks stickerPacks = mStickerPacks;

        if (null == stickerPacks || null == stickerPacks.getStickersList() || stickerPacks.getStickersList().size() == 0){
            return false;
        }

        String trayImage = stickerPacks.getTrayImageFile().substring(stickerPacks.getTrayImageFile().lastIndexOf("/")+1);

         List<Sticker> sticker = new ArrayList<>();
        if (stickerPacks.getStickersList().size() != 0){
            sticker.clear();
            ArrayList<String> emoji = new ArrayList<>();
            emoji.add("");
            for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
                String name = stickerPacks.getStickersList().get(i).getImage();
                sticker.add(new Sticker(name.substring(name.lastIndexOf("/")+1), emoji));
            }
        }

        StickerPack stickerPack;
        stickerPack = new StickerPack(stickerPacks.getIdentifier(), stickerPacks.getTitle(),
                stickerPacks.getPublisher(), trayImage, "",
                stickerPacks.getPublisherWebsite(), stickerPacks.getPrivacyPolicyWebsite(),
                stickerPacks.getLicenseAgreementWebsite(), "1", false,
                false, sticker);

        List<StickerPack> packs = new ArrayList<>();
        packs.add(stickerPack);

        String resJson = JSONObject.toJSONString(packs);
        SPStaticUtils.put("sticker_packs", resJson);
        return true;
    }

    /**
     * 清空sticker_packs数据
     */
    public static void cleanStickers(){
//        Hawk.delete("sticker_packs");
        getInstance().clean();
    }

    private void clean(){
        if (mStickerPacks != null){
            mStickerPacks = null;
        }
    }

    /**
     * 获取sticker_packs数据
     * @return
     */
    public static List<StickerPack> getStickers(){
        String json = SPStaticUtils.getString("sticker_packs","");
        if (TextUtils.isEmpty(json)){
            return new ArrayList<StickerPack>();
        }

        List<StickerPack> list = JSON.parseArray(json,StickerPack.class);

        return  list;
    }



    private void fileStorage(File file, String data) {
        if (file.exists() && getFileSize(file) > 0) {
            return;
        }
        byte[] b = new byte[1024];
        try {
            URL url = new URL(data);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            DataInputStream di = new DataInputStream(urlConnection.getInputStream());
            // output
            FileOutputStream fo = new FileOutputStream(file);
            // copy the actual file
            // (it would better to use a buffer bigger than this)
            while (-1 != di.read(b, 0, 1))
                fo.write(b, 0, 1);
            di.close();
            fo.close();
            Log.e("###", "Size: " + getFileSize(file) + " file: " + file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("###", "e: " + e.getMessage());
            System.exit(1);
        }
    }

    public static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
                Log.d("###", "获取文件大小不存在!");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return size;
    }
}
