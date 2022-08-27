package com.example.lovesticker.sticker.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.databinding.FragmentLoveBinding;
import com.example.lovesticker.sticker.adapter.AnimationAdapter;
import com.example.lovesticker.sticker.adapter.LoveAdapter;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.sticker.viewmodel.LoveViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.view.swipeRefresh.PullLoadMoreRecyclerView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;


public class LoveFragment extends Fragment {
    private FragmentLoveBinding viewBinding;
    private String mLink;
    private LoveAdapter loveAdapter;
    private LoveViewModel viewModel;

    private List<SingleAnimatedCategoriesBean.Postcards> mPostcards;
    private GridLayoutManager manager;


    public LoveFragment() {
    }

    public static LoveFragment newInstance(String link) {
        LoveFragment fragment = new LoveFragment();
        Bundle args = new Bundle();
        args.putString("link",link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewBinding = FragmentLoveBinding.inflate(inflater,container,false);

        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this,new ViewModelProvider.NewInstanceFactory()).get(LoveViewModel.class);

        initView();
        initClickListener();
        dataObserver();
    }

    protected void initView() {
        if (getArguments() != null){
            MaxADManager.loadInterstitialDetailAd((AppCompatActivity) getActivity());
            LSMKVUtil.put(" SingleAnimatedInterstitialAd", true);
            mLink = getArguments().getString("link");

            if (mLink != null) {
//                Log.e("###", "mLink: " + getArguments().getString("link").substring(1));
                viewBinding.loadingData.setVisibility(View.VISIBLE);

                //Adapter
                manager = new GridLayoutManager(getContext(), 2);
                viewBinding.loveRecycler.setLayoutManager(manager);
                loveAdapter = new LoveAdapter(viewModel.postcardsList, getContext(), getActivity(), onPositionClickedListener);
                viewBinding.loveRecycler.setAdapter(loveAdapter);


                viewModel.requestInitialSingleAnimatedData(mLink.substring(1));
            }
        }
    }


    protected void initClickListener() {

        viewBinding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {  //下拉刷新监听
                viewModel.requestInitialSingleAnimatedData(mLink.substring(1));
                viewBinding.swipeLayout.finishRefresh();
            }
        });

        viewBinding.swipeLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {  //上拉加载监听
                if (!viewModel.requestSurplusSingleAnimatedData(mLink.substring(1))) {
                    viewBinding.swipeLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });

    }


    protected void dataObserver() {
        viewModel.getSingleAnimatedLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                viewBinding.loadingData.setVisibility(View.GONE);

                if (size > -1) { // -1时，网络加载数据失败
                    int index = viewModel.postcardsList.size() - size;

                    if (index == 0) {
                        loveAdapter.notifyDataSetChanged();
                    } else {
                        loveAdapter.notifyItemRangeChanged(index - 1, size);
                    }
                }

                viewBinding.swipeLayout.finishLoadMore();
            }
        });
    }

    private final AnimationAdapter.OnPositionClickedListener onPositionClickedListener = position -> {
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (LSMKVUtil.getBoolean("loadad", true)) {
                    if (position == 6) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }

            }
        });
    };


    @Override
    public void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("SingleAnimatedBackAd", false) &&
                LSMKVUtil.getBoolean("loadad", true)) {
            MaxADManager.tryShowInterstitialBackAd((AppCompatActivity) getActivity());
            LSMKVUtil.put(" SingleAnimatedBackAd", false);
        }
    }


}