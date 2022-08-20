package com.example.lovesticker.mine.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentSavedPacksBinding;
import com.example.lovesticker.details.activity.PackDetailsActivity;
import com.example.lovesticker.details.activity.PackImageDetailsActivity;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.mine.adapter.SavedPackAdapter;
import com.example.lovesticker.mine.viewmodel.SavePacksViewModel;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.room.InvokesData;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SavedPacksFragment extends BaseFragment<SavePacksViewModel, FragmentSavedPacksBinding> {
    private SavedPackAdapter savedPackAdapter;
    private StickerPack stickerPack;
    private List<Sticker> sticker = new ArrayList<>();

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;
    private File file;
    private File fileTray;
    private boolean stickerPackWhitelistedInWhatsAppConsumer;
    private boolean stickerPackWhitelistedInWhatsAppSmb;

    @Override
    protected void initView() {

        viewModel.getGsonData(getContext());

    }

    @Override
    protected void initClickListener() {
        viewBinding.savePacksSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getGsonData(getContext());
//                savedPackAdapter.notifyDataSetChanged();
                viewBinding.savePacksSwipeLayout.setRefreshing(false);
//                int temp = savedPackAdapter.getItemCount();
//                Log.e("###", "temp: " + temp);
//                Log.e("###", "mStickerPacks.size(): " + mStickerPacks.size());
//                if (temp!=mStickerPacks.size()){
//                    savedPackAdapter.notifyDataSetChanged();
//                }
//                viewBinding.savePacksSwipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void dataObserver() {
        viewModel.getSavePackLiveData().observe(getViewLifecycleOwner(), new Observer<List<StickerPacks>>() {
            @Override
            public void onChanged(List<StickerPacks> stickerPacks) {
//                Log.e("###", "stickerPacks Size "+  stickerPacks.size());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                viewBinding.savedPackRecycler.setLayoutManager(layoutManager);
                savedPackAdapter = new SavedPackAdapter(stickerPacks,getContext(), onAddButtonClickedListener);
                viewBinding.savedPackRecycler.setAdapter(savedPackAdapter);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedPackAdapter != null){
            savedPackAdapter.notifyDataSetChanged();
        }
    }

    // 接口回调
    private final SavedPackAdapter.OnAddButtonClickedListener onAddButtonClickedListener =
            stickerPack -> {
                if (stickerPack.getStickersList().size() != 0){
                    sticker.clear();
                    ArrayList<String> emoji = new ArrayList<>();
                    emoji.add("");
                    for (int i = 0; i < stickerPack.getStickersList().size(); i++) {
                        String name = stickerPack.getStickersList().get(i).getImage();
                        sticker.add(new Sticker(name.substring(name.lastIndexOf("/")+1), emoji));
                    }
                }
                addStickerPackToWhatsApp(stickerPack.getIdentifier(), stickerPack.getTitle(), stickerPack);
            };

    protected void addStickerPackToWhatsApp(String identifier, String stickerPackName, StickerPacks stickerPacks) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(requireActivity().getPackageManager()) && !WhitelistCheck.isWhatsAppSmbAppInstalled(requireActivity().getPackageManager())) {
                Toast.makeText(getActivity(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_SHORT).show();
                return;
            }
            stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(requireContext(), identifier);
            stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(requireContext(), identifier);

//            if (getFileSize(file) == 0 && getFileSize(fileTray) == 0){
//                AddSendStatus();
//            }

            DownloadImages(stickerPacks);

        } catch (Exception e) { //恢复原来状态，不再显示阴影
            WindowManager.LayoutParams attributes1 = requireActivity().getWindow().getAttributes();
            attributes1.alpha = 1;
            requireActivity().getWindow().setAttributes(attributes1);

            Log.e("###", "error adding sticker pack to WhatsApp: " + e.getMessage());
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    protected long getFileSize(File file) {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void AddSendStatus() {
        //弹窗出现外部为阴影
        WindowManager.LayoutParams attributes = requireActivity().getWindow().getAttributes();
        attributes.alpha = 0.5f;
        requireActivity().getWindow().setAttributes(attributes);

        //PopupWindow
        addSendPopupWindow = new PopupWindow();
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuview = inflater.inflate(R.layout.item_send_status, null);
        popupWindowImg = menuview.findViewById(R.id.send_logo);
        popupWindowHeadline = menuview.findViewById(R.id.status_text);
        popupWindowSubtitle = menuview.findViewById(R.id.subtitle);

        popupWindowImg.setImageResource(R.drawable.preparing);
        popupWindowHeadline.setText("Preparing Pack");
        popupWindowSubtitle.setText("Pack is ready soon…");

        addSendPopupWindow.setContentView(menuview);
        addSendPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        addSendPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        addSendPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindows_bg));

        //设置弹窗位置
        addSendPopupWindow.showAtLocation(requireActivity().findViewById(R.id.ll_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //弹窗取消监听 取消之后恢复阴影
        addSendPopupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams attributes1 = requireActivity().getWindow().getAttributes();
            attributes1.alpha = 1;
            requireActivity().getWindow().setAttributes(attributes1);
        });

    }

    private void DownloadImages(StickerPacks stickerPacks) {
        List<String> stickersImg = new ArrayList();
//        popupWindowImg.setImageResource(R.drawable.poppup_window_rotating_wheel);
//        popupWindowHeadline.setText("Connecting WhatsApp");
//        popupWindowSubtitle.setText("The pack in preparation…");
        for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
            stickersImg.add(LSConstant.image_uri + stickerPacks.getStickersList().get(i).getImage());
        }

        new Thread(() -> {
            try {
                String[] trayImageName = stickerPacks.getTrayImageFile().split("/");
                String trayImage = LSConstant.image_uri + stickerPacks.getTrayImageFile();
                Log.e("###", "Save getFilesDir: " + requireActivity().getFilesDir());
                File myTrayr = new File(requireActivity().getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
                if (!myTrayr.exists()) {
                    myTrayr.mkdirs();
                }

                fileTray = new File(myTrayr, trayImageName[2]);
                fileStorage(fileTray, trayImage);

                for (int i = 0; i < stickersImg.size(); i++) {
                    String imageName = stickerPacks.getStickersList().get(i).getImage();
                    String[] srs = imageName.split("/");

                    file = new File(myTrayr, srs[2]);
                    fileStorage(file, stickersImg.get(i));
                }
            } catch (Exception e) {
                e.getMessage();
            }
            Message msg = new Message();
            msg.what = 0;
            msg.obj = stickerPacks;
            handler.sendMessage(msg);
        }).start();
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


    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {
            StickerPacks stickerPacks = (StickerPacks) msg.obj;

            String[] trayImage = stickerPacks.getTrayImageFile().split("/");
            Log.e("###", "trayImage :" + trayImage[2]);
            String pngImage = trayImage[2].replace(".webp", ".png");

            stickerPack = new StickerPack(stickerPacks.getIdentifier(), stickerPacks.getTitle(),
                    stickerPacks.getPublisher(), trayImage[2], "",
                    stickerPacks.getPublisherWebsite(), stickerPacks.getPrivacyPolicyWebsite(),
                    stickerPacks.getLicenseAgreementWebsite(), "1", false,
                    false, sticker);

            List<StickerPack> packs = new ArrayList<>();
            packs.add(stickerPack);
            Hawk.put("sticker_packs", packs);

//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                popupWindowImg.setImageResource(R.drawable.connection);
//                popupWindowHeadline.setText("Connection Succeeded");
//                popupWindowSubtitle.setText("Almost completed…");
//            }, 2000);
//
//            if (stickerPackWhitelistedInWhatsAppConsumer){
//                addSendPopupWindow.dismiss();
//            }

            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToChooser(stickerPacks.getIdentifier(), stickerPacks.getTitle());

            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToSpecificPackage(stickerPacks.getIdentifier(), stickerPacks.getTitle(), WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
            } else {
                Toast.makeText(requireActivity(), R.string.not_whitelisted, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    });


    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            popupWindowImg.setImageResource(R.drawable.finish_add);
//            popupWindowHeadline.setText("Add to WhatsApp");
//            popupWindowSubtitle.setText("Done！");
//        }, 1000);
//        addSendPopupWindow.dismiss();

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);
        try {
            startActivityForResult(intent, LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    //Handle cases either of WhatsApp are set as default app to handle this intent. We still want users to see both options.
    private void launchIntentToAddPackToChooser(String identifier, String stickerPackName) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            popupWindowImg.setImageResource(R.drawable.finish_add);
            popupWindowHeadline.setText("Add to WhatsApp");
            popupWindowSubtitle.setText("Done！");
        }, 1000);
        addSendPopupWindow.dismiss();

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireActivity(), R.string.not_add_package_selector, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK"); //跳转whatsapp
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LSConstant.ADD_PACK) {
            RateController.getInstance().tryRateFinish(getContext(), new RateDialog.RatingClickListener() {

                @Override
                public void onClickFiveStart() {

                }

                @Override
                public void onClick1To4Start() {

                }

                @Override
                public void onClickReject() {

                }

                @Override
                public void onClickCancel() {

                }
            }); //评分

            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    RateController.getInstance().tryRateFinish(getContext(), new RateDialog.RatingClickListener() {

                        @Override
                        public void onClickFiveStart() {

                        }

                        @Override
                        public void onClick1To4Start() {

                        }

                        @Override
                        public void onClickReject() {

                        }

                        @Override
                        public void onClickCancel() {

                        }
                    }); //评分

                    final String validationError = data.getStringExtra("validation_error");
                    if (validationError != null) {
                        if (BuildConfig.DEBUG) {
                            //validation error should be shown to developer only, not users.
                            BaseActivity.MessageDialogFragment.newInstance(R.string.title_validation_error, validationError).show(getActivity().getSupportFragmentManager(), "validation error");
                        }
                        Log.e("###", "Validation failed:" + validationError);
                    }
                } else {
                    new AddStickerPackActivity.StickerPackNotAddedMessageFragment().show(getActivity().getSupportFragmentManager(), "sticker_pack_not_added");
                }
            }
        }
    }


}