package com.example.lovesticker.sticker.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentAnimationBinding;
import com.example.lovesticker.databinding.FragmentSavedStickerBinding;
import com.example.lovesticker.sticker.adapter.AnimationAdapter;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.sticker.viewmodel.AnimationViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.view.swipeRefresh.PullLoadMoreRecyclerView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;

public class AnimationFragment extends BaseFragment<AnimationViewModel, FragmentAnimationBinding> {
    private AnimationAdapter animationAdapter;
    private GridLayoutManager manager;
    private FragmentAnimationBinding viewBinding;


    public AnimationFragment() {
    }

    public static AnimationFragment newInstance() {
        AnimationFragment fragment = new AnimationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentAnimationBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    protected void initView() {
        viewBinding.loadingData.setVisibility(View.VISIBLE);

        MaxADManager.loadInterstitialDetailAd((AppCompatActivity) getActivity());
        LSMKVUtil.put("AnimationInterstitialAd",true);


       //Adapter
        manager = new GridLayoutManager(getContext(),2);
        viewBinding.animationRecycler.setLayoutManager(manager);
        animationAdapter = new AnimationAdapter(viewModel.postcardsList,getContext(),getActivity(),onPositionClickedListener);
        viewBinding.animationRecycler.setAdapter(animationAdapter);


        viewModel.requestInitialAllAnimatedData();

    }


    @Override
    protected void initClickListener() {

        viewBinding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {  //下拉刷新监听
                viewModel.requestInitialAllAnimatedData();
                viewBinding.swipeLayout.finishRefresh();
            }
        });

        viewBinding.swipeLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {  //上拉加载监听
                if (!viewModel.requestSurplusAllAnimatedData()) {
                    viewBinding.swipeLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

    }



    @Override
    protected void dataObserver() {
        viewModel.getAllAnimatedLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                try {
                    viewBinding.loadingData.setVisibility(View.GONE);
                    if (size > -1) { // -1时，网络加载数据失败
                        int index = viewModel.postcardsList.size() - size;
                        if (index == 0) {
                            animationAdapter.notifyDataSetChanged();
                        } else {
                            if (index - 1 < viewModel.postcardsList.size()){
                                animationAdapter.notifyItemRangeChanged(index - 1, size);
                            }
                        }
                    }

                    viewBinding.swipeLayout.finishLoadMore(1000);
                }catch (Exception e){
                    e.getMessage();
                }

            }
        });
    }

    private final AnimationAdapter.OnPositionClickedListener onPositionClickedListener = position -> {
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (LSMKVUtil.getBoolean("loadad",true)){
                    if (position == 6){
                        return 2;
                    }else {
                        return 1;
                    }
                }else {
                    return 1;
                }
            }
        });
    };

    @Override
    public void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("AnimationDetailsBackAd",false) &&
                LSMKVUtil.getBoolean("loadad",true)){
            MaxADManager.tryShowInterstitialBackAd((AppCompatActivity) getActivity());
            LSMKVUtil.put("AnimationDetailsBackAd",false);
        }

    }

}