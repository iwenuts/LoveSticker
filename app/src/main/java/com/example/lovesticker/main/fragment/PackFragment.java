package com.example.lovesticker.main.fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentPackBinding;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.view.swipeRefresh.PullLoadMoreRecyclerView;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackFragment extends BaseFragment<PackViewModel, FragmentPackBinding> {
    private PackAdapter packAdapter;
    private RecyclerView recyclerView;

    public PackFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PackFragment newInstance() {
        PackFragment fragment = new PackFragment();
        return fragment;
    }


    @Override
    protected void initView() {

        MaxADManager.loadInterstitialDetailAd((AppCompatActivity) getActivity());
        LSMKVUtil.put("PackInterstitialAd",true);

        viewBinding.loadingData.setVisibility(View.VISIBLE);

        recyclerView = viewBinding.swipeLayout.getRecyclerView();
        recyclerView.setVerticalScrollBarEnabled(true);
        viewBinding.swipeLayout.setRefreshing(false);
        viewBinding.swipeLayout.setFooterViewText("loading");
        viewBinding.swipeLayout.setLinearLayout();
        packAdapter = new PackAdapter(((PackViewModel)viewModel).stickerPacksList,viewModel,getContext(),getActivity());
        viewBinding.swipeLayout.setAdapter(packAdapter);

        viewBinding.swipeLayout.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {  //下拉刷新监听
                viewModel.requestInitialPackData();
            }

            @Override
            public void onLoadMore() {  //上拉加载监听
                if (!viewModel.requestSurplusPackData()) {
                    viewBinding.swipeLayout.setPullLoadMoreCompleted();
                }
            }
        });

        viewModel.requestInitialPackData();
    }

    @Override
    protected void initClickListener() {

    }

    @Override
    protected void dataObserver() {
        viewModel.getStickerPacksBean().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer size) {
                viewBinding.loadingData.setVisibility(View.GONE);

                if (size > -1) { // -1时，网络加载数据失败
                    int index = ((PackViewModel) viewModel).stickerPacksList.size() - size;

                    if (index == 0) {
                        packAdapter.notifyDataSetChanged();
                    } else {
                        packAdapter.notifyItemRangeChanged(index - 1, size);
                    }
                }

                viewBinding.swipeLayout.setPullLoadMoreCompleted();
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