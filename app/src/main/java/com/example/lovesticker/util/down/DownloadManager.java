package com.example.lovesticker.util.down;

import com.liulishuo.okdownload.DownloadTask;

public class DownloadManager {

    public static DownloadTask singleTask;

//    public static void startDownloadTemplate(TemplateHomeBean homeBean, OnDownloadListener downloadListener) {
//        String url = BaseRepository.BASE_URL + homeBean.getResource();
//        if(singleTask != null) singleTask.cancel();
//
//        singleTask = new DownloadTask.Builder(url, new File(PixoCutApp.resourcePath))
//                .setFilename("model" + homeBean.getTemplateName() + "-" + homeBean.getVersion() + ".zip")
//                .setPassIfAlreadyCompleted(false)
//                .build();
//        singleTask.enqueue(new DownloadListener3() {
//            @Override
//            protected void started(@NonNull DownloadTask task) {
//                MLog.logI("download start :" + task.getUrl());
//                downloadStart(downloadListener);
//            }
//            @Override
//            protected void completed(@NonNull DownloadTask task) {
//                if(task.getFile() != null){
//                    MLog.logI("download success :" + task.getFilename());
//                    downloadSuccess(downloadListener,task.getFile().getPath());
//                }else{
//                    MLog.logI("download failed case null");
//                    downloadFail(downloadListener);
//                }
//            }
//            @Override
//            protected void canceled(@NonNull DownloadTask task) {
//                MLog.logI("download canceled");
//                downloadCancel(downloadListener);
//            }
//            @Override
//            protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
//                if(task.getFile() != null && FileUtils.isFileExists(task.getFile())) {
//                    MLog.logI("download error with file:" + e.getMessage());
//                    downloadSuccess(downloadListener,task.getFile().getPath());
//                }else{
//                    MLog.logI("download error :" + e.getMessage());
//                    downloadFail(downloadListener);
//                }
//
//            }
//            @Override
//            protected void warn(@NonNull DownloadTask task) {
//
//            }
//
//            @Override
//            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
//
//            }
//            @Override
//            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
//
//            }
//            @Override
//            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
//
//            }
//        });
//    }

    public static void cancelCurrentTask() {
        if(singleTask != null) singleTask.cancel();
    }

    private static void downloadStart(OnDownloadListener downloadListener) {
        if(downloadListener != null) downloadListener.onDownloadStart();
    }
    private static void downloadSuccess(OnDownloadListener downloadListener, String path) {
        if(downloadListener != null) downloadListener.onDownloadSuccess(path);
    }
    private static void downloadFail(OnDownloadListener downloadListener) {
        if(downloadListener != null) downloadListener.onDownloadFail();
    }
    private static void downloadCancel(OnDownloadListener downloadListener) {
        if(downloadListener != null) downloadListener.onDownloadCancel();
    }
    public interface OnDownloadListener {
        default void onDownloadStart() {

        }
        void onDownloadSuccess(String path);
        void onDownloadFail();
        void onDownloadCancel();
    }
}
