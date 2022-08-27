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
import com.example.lovesticker.util.stickers.StickersCallBack;
import com.example.lovesticker.util.stickers.StickersManager;
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
    private List<Sticker> sticker = new ArrayList<>();

    private PopupWindow addSendPopupWindow;
    private ImageView popupWindowImg;
    private TextView popupWindowHeadline;
    private TextView popupWindowSubtitle;
    private boolean stickerPackWhitelistedInWhatsAppConsumer;
    private boolean stickerPackWhitelistedInWhatsAppSmb;

    private int selIndex = -1;
    private List<StickerPacks> mStickerPacks;

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
            }
        });
    }

    @Override
    protected void dataObserver() {
        viewModel.getSavePackLiveData().observe(getViewLifecycleOwner(), new Observer<List<StickerPacks>>() {
            @Override
            public void onChanged(List<StickerPacks> stickerPacks) {
//                Log.e("###", "stickerPacks Size "+  stickerPacks.size());
                mStickerPacks = stickerPacks;

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                viewBinding.savedPackRecycler.setLayoutManager(layoutManager);
                viewBinding.savedPackRecycler.setHasFixedSize(true);
                savedPackAdapter = new SavedPackAdapter(stickerPacks,getContext(), onAddButtonClickedListener);
                viewBinding.savedPackRecycler.setAdapter(savedPackAdapter);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (savedPackAdapter != null){
//            savedPackAdapter.notifyDataSetChanged();
//        }
    }

    // 接口回调
    private final SavedPackAdapter.OnAddButtonClickedListener onAddButtonClickedListener =
            (stickerPack, index) -> {
                selIndex = index;

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

            StickersManager.downloadStickers(stickerPacks, new StickersCallBack() {
                @Override
                public void completed(int complete, int failed, int all) {
                    Log.e("StickersManager", "downloadStickers 完成:" + complete + " 失败:" + failed + " 共:" + all);
                    if (failed > 0){ //如果有下载失败弹出提示

                        return;
                    }

                    if (complete == all) {
                        //下载成功数据缓存
                        StickersManager.putStickers();
                    }

                        Message msg = new Message();
                        msg.what = 0;
                        msg.arg1 = complete;
                        msg.arg2 = all;
                        msg.obj = stickerPacks;
                        handler.sendMessage(msg);
                }
            });
            //DownloadImages(stickerPacks);

        } catch (Exception e) { //恢复原来状态，不再显示阴影
            WindowManager.LayoutParams attributes1 = requireActivity().getWindow().getAttributes();
            attributes1.alpha = 1;
            requireActivity().getWindow().setAttributes(attributes1);

            Log.e("###", "error adding sticker pack to WhatsApp: " + e.getMessage());
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private final Handler handler = new Handler(msg -> {
        //回到主线程（UI线程），处理UI
        if (msg.what == 0) {

            if (msg.arg1 == msg.arg2) {//如果下载完成
                StickerPacks stickerPacks = (StickerPacks) msg.obj;

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
        }
        return false;
    });


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

            StickersManager.cleanStickers();

            if (null != mStickerPacks  && selIndex>-1 && selIndex<mStickerPacks.size()) {
                boolean isWhitelisted = WhitelistCheck.isWhitelisted(requireContext(), mStickerPacks.get(selIndex).getIdentifier());
                if (isWhitelisted && null != savedPackAdapter){
                    savedPackAdapter.notifyItemRangeChanged(selIndex, 1);

                }
            }

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