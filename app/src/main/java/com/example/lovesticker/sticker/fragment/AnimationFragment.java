package com.example.lovesticker.sticker.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.FragmentAnimationBinding;
import com.example.lovesticker.sticker.adapter.AnimationAdapter;
import com.example.lovesticker.sticker.model.AllAnimatedBean;
import com.example.lovesticker.sticker.viewmodel.AnimationViewModel;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnimationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimationFragment extends BaseFragment<AnimationViewModel, FragmentAnimationBinding> {
    AnimationAdapter animationAdapter;


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

        viewModel.requestInitialAllAnimatedData();
        initRefresh();

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
                    GridLayoutManager manager = new GridLayoutManager(getContext(),2);
                    viewBinding.animationRecycler.setLayoutManager(manager);
                    animationAdapter = new AnimationAdapter(postcards,getContext());
                    viewBinding.animationRecycler.setAdapter(animationAdapter);


                }
            }
        });


    }

    private void initRefresh() {

        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.requestSurplusAllAnimatedData();
                animationAdapter.notifyDataSetChanged();
                viewBinding.swipeLayout.setRefreshing(false);

            }
        });
    }




}