package com.example.lovesticker.main.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.animation.keyframe.PathKeyframe;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentPackBinding;
import com.example.lovesticker.main.adapter.PackAdapter;
import com.example.lovesticker.main.model.StickerPacks;
import com.example.lovesticker.main.viewmodel.PackViewModel;
import com.example.lovesticker.util.inteface.PacksListener;

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

        viewModel.requestInitialPackData();
//        viewModel.requestSurplusPackData();

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

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                viewBinding.packRecycler.setLayoutManager(layoutManager);
                packAdapter = new PackAdapter(stickerPacks,viewModel,getContext());
                viewBinding.packRecycler.setAdapter(packAdapter);


            }
        });

    }

}