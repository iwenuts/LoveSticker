package com.example.lovesticker.main.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentStickerBinding;
import com.example.lovesticker.main.viewmodel.StickerViewModel;


public class StickerFragment extends BaseFragment<StickerViewModel, FragmentStickerBinding> {


    public StickerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StickerFragment newInstance() {
        StickerFragment fragment = new StickerFragment();

        return fragment;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initClickListener() {

    }

    @Override
    protected void dataObserver() {

    }


}