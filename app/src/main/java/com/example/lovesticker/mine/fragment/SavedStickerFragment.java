package com.example.lovesticker.mine.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentSavedStickerBinding;
import com.example.lovesticker.details.activity.StickersDetailsActivity;
import com.example.lovesticker.mine.adapter.SaveStickerAdapter;
import com.example.lovesticker.mine.viewmodel.SavedStickerViewModel;
import com.example.lovesticker.util.event.UpdateStickerEvent;
import com.example.lovesticker.util.room.SaveStickerData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class SavedStickerFragment extends BaseFragment<SavedStickerViewModel, FragmentSavedStickerBinding> implements OnClickCallBack {
    private SaveStickerAdapter adapter;

    public SavedStickerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateStickerEvent event) {
        // Do something
        viewModel.getImageData();
    }

    @Override
    protected void initView() {
        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        viewBinding.saveStickerRecycler.setLayoutManager(manager);
        viewBinding.saveStickerRecycler.setHasFixedSize(true);
        adapter = new SaveStickerAdapter(((SavedStickerViewModel)viewModel).saveImgData,getContext());
        adapter.setOnClickItem(SavedStickerFragment.this);
        viewBinding.saveStickerRecycler.setAdapter(adapter);

        viewModel.getImageData();
    }

    @Override
    protected void initClickListener() {
        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getImageData();
                adapter.notifyDataSetChanged();
                viewBinding.swipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void dataObserver() {
        viewModel.savedStickerLiveData.observe(getViewLifecycleOwner(), new Observer<List<SaveStickerData>>() {
            @Override
            public void onChanged(List<SaveStickerData> stringList) {
                //Adapter
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClickItem(int index) {
        //跳转到详情页
        Intent intent = new Intent(getActivity(), StickersDetailsActivity.class);
        intent.putExtra("image",((SavedStickerViewModel)viewModel).saveImgData.get(index).getSavePostcardsImg());
        intent.putExtra("id", ((SavedStickerViewModel)viewModel).saveImgData.get(index).getSavePostcardId());
        getActivity().startActivity(intent);
    }

    @Override
    public void onDelItem(int index) {
        ((SavedStickerViewModel)viewModel).saveImgData.remove(index);

        viewBinding.saveStickerRecycler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRemoved(index);
            }

        });
    }
}