package com.example.lovesticker.mine.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.mine.adapter.SavedPackAdapter;
import com.example.lovesticker.mine.viewmodel.SavePacksViewModel;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;
import com.example.lovesticker.util.stickers.model.StickerPack;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.FileInputStream;
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

    private final SavedPackAdapter.OnAddButtonClickedListener onAddButtonClickedListener =
            stickerPack -> addStickerPackToWhatsApp(stickerPack.getIdentifier(),stickerPack.getTitle(),stickerPack);  // 接口回调

    protected void addStickerPackToWhatsApp(String identifier, String stickerPackName,StickerPacks stickerPacks) {
        try {
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(getContext().getPackageManager()) && !WhitelistCheck.isWhatsAppSmbAppInstalled(getContext().getPackageManager())) {
                Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
                return;
            }
            final boolean stickerPackWhitelistedInWhatsAppConsumer = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(getContext(), identifier);
            final boolean stickerPackWhitelistedInWhatsAppSmb = WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(getContext(), identifier);


            if (getFileSize(file) == 0 && getFileSize(fileTray) == 0){
                AddSendStatus();
            }

            DownloadImages();




            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                //ask users which app to add the pack to.

                for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
                    sticker.add(new Sticker(stickerPacks.getStickersList().get(i).getImage(), new ArrayList<>()));
                }

                stickerPack = new StickerPack(stickerPacks.getIdentifier(),stickerPacks.getTitle(),stickerPacks.getTrayImageFile(),
                        stickerPacks.getTrayImageFile(),"",stickerPacks.getPublisherWebsite(),stickerPacks.getPrivacyPolicyWebsite(),
                        stickerPacks.getLicenseAgreementWebsite(),"",false,false,sticker);

                Log.e("###", "PackImageDetailsStickerPack: " + stickerPack);

                stickerPack.setStickers(sticker);

                Hawk.put("stickerPack",stickerPack);


                launchIntentToAddPackToChooser(identifier, stickerPackName);
            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME);
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                launchIntentToAddPackToSpecificPackage(identifier, stickerPackName, WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME);
            } else {
                Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("###", "error adding sticker pack to WhatsApp", e);
            Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }

    }

    private void launchIntentToAddPackToSpecificPackage(String identifier, String stickerPackName, String whatsappPackageName) {
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
        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.add_to_whatsapp)), LSConstant.ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.add_pack_fail_prompt_update_whatsapp, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(LSConstant.EXTRA_STICKER_PACK_NAME, stickerPackName);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LSConstant.ADD_PACK) {
            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
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
        addSendPopupWindow.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
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

    private void DownloadImages() {
        List<String> stickersImg = new ArrayList();
        popupWindowImg.setImageResource(R.drawable.poppup_window_rotating_wheel);
        popupWindowHeadline.setText("Connecting WhatsApp");
        popupWindowSubtitle.setText("The pack in preparation…");
        for (int i = 0; i < stickerPacks.getStickersList().size(); i++) {
            stickersImg.add(LSConstant.image_uri + stickerPacks.getStickersList().get(i).getImage());
        }

        new Thread(() -> {
            try {
                String[] trayImageName = stickerPacks.getTrayImageFile().split("/");
                String trayImage = LSConstant.image_uri + stickerPacks.getTrayImageFile();

                File myTrayr = new File(getFilesDir() + "/" + "stickers_asset" + "/" + stickerPacks.getIdentifier());
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
            handler.sendMessage(msg);
        }).start();
    }







}