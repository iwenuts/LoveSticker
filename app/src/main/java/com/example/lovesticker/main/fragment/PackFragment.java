package com.example.lovesticker.main.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentPackBinding;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackFragment extends BaseFragment<PackViewModel, FragmentPackBinding>{
    private PackAdapter packAdapter;


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
        viewBinding.loadingData.setVisibility(View.VISIBLE);

        viewModel.requestInitialPackData();
//        viewModel.requestSurplusPackData();
        initRefresh();

    }

    @Override
    protected void initClickListener() {

    }

    @Override
    protected void dataObserver() {

        viewModel.getStickerPacksBean().observe(getViewLifecycleOwner(), new Observer<List<StickerPacks>>() {
            @Override
            public void onChanged(List<StickerPacks> stickerPacks) {
                Log.e("###", "setAllStickerPacks:" + stickerPacks.size());
                viewBinding.loadingData.setVisibility(View.GONE);
                //Adapter
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                viewBinding.packRecycler.setLayoutManager(layoutManager);
                packAdapter = new PackAdapter(stickerPacks,viewModel,getContext(),getActivity());
                viewBinding.packRecycler.setAdapter(packAdapter);

            }
        });

    }

    private void initRefresh() {

        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.requestSurplusPackData();
                packAdapter.notifyDataSetChanged();
                viewBinding.swipeLayout.setRefreshing(false);
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (LSMKVUtil.getBoolean("PackDetailsBackAd",false)){
            MaxADManager.tryShowInterstitialBackAd();
            LSMKVUtil.put("PackDetailsBackAd",false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}