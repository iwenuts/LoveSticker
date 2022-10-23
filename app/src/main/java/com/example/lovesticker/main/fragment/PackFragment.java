package com.example.lovesticker.main.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentMineBinding;
import com.example.lovesticker.databinding.FragmentPackBinding;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.view.swipeRefresh.PullLoadMoreRecyclerView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


import java.util.ArrayList;
import java.util.List;


public class PackFragment extends BaseFragment<PackViewModel, FragmentPackBinding> {
    private PackAdapter packAdapter;
    private FragmentPackBinding viewBinding;

    public PackFragment() { }


    public static PackFragment newInstance() {
        PackFragment fragment = new PackFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentPackBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initView() {

        MaxADManager.loadInterstitialDetailAd((AppCompatActivity) getActivity());
        LSMKVUtil.put("PackInterstitialAd",true);

        viewBinding.loadingData.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewBinding.packRecycler.setLayoutManager(layoutManager);
        packAdapter = new PackAdapter(viewModel.stickerPacksList,viewModel,getContext(),getActivity());
        viewBinding.packRecycler.setAdapter(packAdapter);

        viewModel.requestInitialPackData();
    }

    @Override
    protected void initClickListener() {
        viewBinding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {  //下拉刷新监听
                viewModel.requestInitialPackData();
                viewBinding.swipeLayout.finishRefresh();
            }
        });

        viewBinding.swipeLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {  //上拉加载监听
                if (!viewModel.requestSurplusPackData()) {
                    viewBinding.swipeLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });
    }

    @Override
    protected void dataObserver() {
        viewModel.getStickerPacksBean().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                viewBinding.loadingData.setVisibility(View.GONE);

                if (size > -1) { // -1时，网络加载数据失败
                    int index = viewModel.stickerPacksList.size() - size;

                    if (index == 0) {
                        packAdapter.notifyDataSetChanged();
                    } else {
                        packAdapter.notifyItemRangeChanged(index - 1, size);
                    }
                }

                viewBinding.swipeLayout.finishLoadMore();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("PackDetailsBackAd",false) &&
                LSMKVUtil.getBoolean("loadad",true)){
            MaxADManager.tryShowInterstitialBackAd((AppCompatActivity) getActivity());
            LSMKVUtil.put("PackDetailsBackAd",false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}