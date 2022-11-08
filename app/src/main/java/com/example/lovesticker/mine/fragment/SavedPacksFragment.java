package com.example.lovesticker.mine.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentSavedPacksBinding;
import com.example.lovesticker.databinding.FragmentStickerBinding;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.mine.activity.WrapContentLinearLayoutManager;
import com.example.lovesticker.mine.adapter.SavedPackAdapter;
import com.example.lovesticker.mine.viewmodel.SavePacksViewModel;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.event.UpdatePacksEvent;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.score.RateController;
import com.example.lovesticker.util.score.RateDialog;
import com.example.lovesticker.util.stickers.AddStickerPackActivity;
import com.example.lovesticker.util.stickers.StickersCallBack;
import com.example.lovesticker.util.stickers.StickersManager;
import com.example.lovesticker.util.stickers.WhitelistCheck;
import com.example.lovesticker.util.stickers.model.Sticker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class SavedPacksFragment extends BaseFragment<SavePacksViewModel, FragmentSavedPacksBinding> {
    private SavedPackAdapter savedPackAdapter;
    private List<Sticker> sticker = new ArrayList<>();
    private boolean stickerPackWhitelistedInWhatsAppConsumer;
    private boolean stickerPackWhitelistedInWhatsAppSmb;
    private FragmentSavedPacksBinding viewBinding;
    private static int selIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        viewBinding = FragmentSavedPacksBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewBinding.savedPackRecycler.setLayoutManager(layoutManager);
        viewBinding.savedPackRecycler.setHasFixedSize(true);
        savedPackAdapter = new SavedPackAdapter(viewModel.saveData,getContext(), onAddButtonClickedListener);
        viewBinding.savedPackRecycler.setAdapter(savedPackAdapter);

        viewModel.getGsonData();
    }

    @Override
    protected void initClickListener() {
        viewBinding.savePacksSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getGsonData();
//                savedPackAdapter.notifyDataSetChanged();
                viewBinding.savePacksSwipeLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdatePacksEvent event) {
        // Do something
        viewModel.getGsonData();
    }


    @Override
    protected void dataObserver() {
        viewModel.savePackLiveData.observe(getViewLifecycleOwner(), new Observer<List<StickerPacks>>() {
            @Override
            public void onChanged(List<StickerPacks> stickerPacks) {
//                Log.e("###", "stickerPacks Size "+  stickerPacks.size());
                savedPackAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // 接口回调
    private final SavedPackAdapter.OnAddButtonClickedListener onAddButtonClickedListener =
            (stickerPack, index) -> {
                selIndex = index;

                if (stickerPack.getStickersList().size() > 0){
                    sticker.clear();
                    ArrayList<String> emoji = new ArrayList<>();
                    emoji.add("");
                    for (int i = 0; i < stickerPack.getStickersList().size(); i++) {
                        String name = stickerPack.getStickersList().get(i).getImage();
                        sticker.add(new Sticker(name.substring(name.lastIndexOf("/")+1), emoji));
                    }
                    addStickerPackToWhatsApp(stickerPack.getIdentifier(), stickerPack.getTitle(), stickerPack);
                }
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

//                if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
//                    launchIntentToAddPackToChooser(stickerPacks.getIdentifier(), stickerPacks.getTitle());
//                } else
                if (!stickerPackWhitelistedInWhatsAppConsumer) {
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

            if (null != viewModel.saveData  && selIndex > -1 && selIndex < viewModel.saveData.size()) {
                boolean isWhitelisted = WhitelistCheck.isWhitelisted(requireContext(), ((SavePacksViewModel)viewModel).saveData.get(selIndex).getIdentifier());
                if (isWhitelisted && null != savedPackAdapter){
                    savedPackAdapter.notifyItemRangeChanged(selIndex, 1);
                }
            }

//            RateController.getInstance().tryRateFinish(getContext(), new RateDialog.RatingClickListener() {
//
//                @Override
//                public void onClickFiveStart() {
//
//                }
//
//                @Override
//                public void onClick1To4Start() {
//
//                }
//
//                @Override
//                public void onClickReject() {
//
//                }
//
//                @Override
//                public void onClickCancel() {
//
//                }
//
//                @Override
//                public void onFinishScore() {
//
//                }
//            }); //评分

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


}