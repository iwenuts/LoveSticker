package com.example.lovesticker.sticker.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.lovesticker.util.inteface.ImageListener;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.List;


public class LoveFragment extends Fragment {
    private FragmentLoveBinding viewBinding;
    private String mLink;
    private LoveAdapter loveAdapter;
    private LoveViewModel viewModel;
    private static ImageListener imageListener;
    private List<SingleAnimatedCategoriesBean.Postcards> mPostcards;
    private GridLayoutManager manager;

    public LoveFragment() {
        // Required empty public constructor
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


    public void setGetListener(ImageListener imageListener2){
        imageListener = imageListener2;
    }


    protected void initView() {
        if (getArguments() != null){
            MaxADManager.loadInterstitialDetailAd();
            LSMKVUtil.put(" SingleAnimatedInterstitialAd",true);

            mLink = getArguments().getString("link");
            Log.e("###", "mLink: " + getArguments().getString("link").substring(1));

            viewBinding.loadingData.setVisibility(View.VISIBLE);
            viewModel.requestInitialSingleAnimatedData(mLink.substring(1));
            initRefresh();
        }
    }


    protected void initClickListener() {

    }


    protected void dataObserver() {
        viewModel.getSingleAnimatedLiveData().observe(getViewLifecycleOwner(), new Observer<List<SingleAnimatedCategoriesBean.Postcards>>() {
            @Override
            public void onChanged(List<SingleAnimatedCategoriesBean.Postcards> postcards) {

                if (postcards != null) {
                    viewBinding.loadingData.setVisibility(View.GONE);
                    //Adapter
                    manager = new GridLayoutManager(getContext(), 2);
                    viewBinding.loveRecycler.setLayoutManager(manager);
                    loveAdapter = new LoveAdapter(postcards, getContext(), getActivity(),onPositionClickedListener);
                    viewBinding.loveRecycler.setAdapter(loveAdapter);
//                    imageListener.onImageClick(postcards);

                    if (imageListener != null){
                        imageListener.onImage(postcards);
                    }
                }
            }
        });
    }

    private final AnimationAdapter.OnPositionClickedListener onPositionClickedListener = position -> {
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 6){
                    return 2;
                }else {
                    return 1;
                }

            }
        });
    };


    @Override
    public void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("SingleAnimatedBackAd",false)){
            MaxADManager.tryShowInterstitialBackAd();
            LSMKVUtil.put(" SingleAnimatedBackAd",false);
        }

    }

    private void initRefresh() {
        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.requestSurplusSingleAnimatedData(mLink.substring(1));
                loveAdapter.notifyDataSetChanged();
                viewBinding.swipeLayout.setRefreshing(false);

            }
        });

    }


}