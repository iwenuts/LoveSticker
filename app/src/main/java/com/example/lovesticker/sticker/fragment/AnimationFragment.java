package com.example.lovesticker.sticker.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentAnimationBinding;
import com.example.lovesticker.sticker.adapter.AnimationAdapter;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.sticker.viewmodel.AnimationViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.example.lovesticker.util.view.swipeRefresh.PullLoadMoreRecyclerView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnimationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimationFragment extends BaseFragment<AnimationViewModel, FragmentAnimationBinding> {
    private AnimationAdapter animationAdapter;
    private GridLayoutManager manager;
    private RecyclerView recyclerView;


    public AnimationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AnimationFragment newInstance() {
        AnimationFragment fragment = new AnimationFragment();
        return fragment;
    }


    @Override
    protected void initView() {
        viewBinding.loadingData.setVisibility(View.VISIBLE);

        MaxADManager.loadInterstitialDetailAd((AppCompatActivity) getActivity());
        LSMKVUtil.put("AnimationInterstitialAd",true);
        viewModel.requestInitialAllAnimatedData();
//        initRefresh();

    }


    @Override
    protected void initClickListener() {

    }

    @Override
    protected void dataObserver() {
        viewModel.getAllAnimatedLiveData().observe(getViewLifecycleOwner(), new Observer<List<AllAnimatedBean.Postcards>>() {
            @Override
            public void onChanged(List<AllAnimatedBean.Postcards> postcards) {
                if (postcards != null){
                    viewBinding.loadingData.setVisibility(View.GONE);

                    //Adapter
//                    recyclerView = viewBinding.swipeLayout.getRecyclerView();
//                    recyclerView.setVerticalScrollBarEnabled(false);
//                    viewBinding.swipeLayout.setRefreshing(true);
//                    viewBinding.swipeLayout.setFooterViewText("loading");
                    manager = viewBinding.swipeLayout.setGridLayout(2);
                    animationAdapter = new AnimationAdapter(postcards,getContext(),getActivity(),onPositionClickedListener);
                    viewBinding.swipeLayout.setAdapter(animationAdapter);

                    viewBinding.swipeLayout.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                        @Override
                        public void onRefresh() {
                            if (animationAdapter!= null){
                                animationAdapter.notifyDataSetChanged();
                                viewBinding.swipeLayout.setPullLoadMoreCompleted();
                            }
                        }

                        @Override
                        public void onLoadMore() {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                viewModel.requestSurplusAllAnimatedData();
                                if (animationAdapter!= null){
                                    animationAdapter.notifyDataSetChanged();
                                    viewBinding.swipeLayout.setPullLoadMoreCompleted();
                                }

                            }, 1000);
                        }
                    });

//                    //Adapter
//                    manager = new GridLayoutManager(getContext(),2);
//                    viewBinding.animationRecycler.setLayoutManager(manager);
//                    animationAdapter = new AnimationAdapter(postcards,getContext(),getActivity(),onPositionClickedListener);
//
//                    viewBinding.animationRecycler.setAdapter(animationAdapter);


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

//    private void initRefresh() {
//
//        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                viewModel.requestSurplusAllAnimatedData();
//                animationAdapter.notifyDataSetChanged();
//                viewBinding.swipeLayout.setRefreshing(false);
//
//            }
//        });
//    }

//    @Override
//    public void onRefresh() {   //下拉刷新监听
//        if (animationAdapter!= null){
//            animationAdapter.notifyDataSetChanged();
//            viewBinding.swipeLayout.setPullLoadMoreCompleted();
//        }
//
//    }
//
//    @Override
//    public void onLoadMore() {  //上拉加载监听
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//
//            viewModel.requestSurplusAllAnimatedData();
//            if (animationAdapter!= null){
//                animationAdapter.notifyDataSetChanged();
//                viewBinding.swipeLayout.setPullLoadMoreCompleted();
//            }
//
//        }, 1000);
//
//    }

}