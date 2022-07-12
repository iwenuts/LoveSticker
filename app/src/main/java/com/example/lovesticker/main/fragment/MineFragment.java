package com.example.lovesticker.main.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.FragmentMineBinding;
import com.example.lovesticker.main.viewmodel.MineViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment<MineViewModel, FragmentMineBinding> {

    public MineFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
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