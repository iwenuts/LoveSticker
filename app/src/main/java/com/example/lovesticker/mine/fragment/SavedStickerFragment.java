package com.example.lovesticker.mine.fragment;

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
import com.example.lovesticker.databinding.FragmentSavedStickerBinding;
import com.example.lovesticker.mine.adapter.SaveStickerAdapter;
import com.example.lovesticker.mine.viewmodel.SavedStickerViewModel;

import java.util.List;


public class SavedStickerFragment extends BaseFragment<SavedStickerViewModel, FragmentSavedStickerBinding> {
    private SaveStickerAdapter adapter;

    public SavedStickerFragment() {
        // Required empty public constructor
    }


    @Override
    protected void initView() {
        viewModel.getImageData(getContext());

    }

    @Override
    protected void initClickListener() {
        viewBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getImageData(getContext());
                adapter.notifyDataSetChanged();
                viewBinding.swipeLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void dataObserver() {
        viewModel.getSavedStickerLiveData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> stringList) {
                //Adapter
                GridLayoutManager manager = new GridLayoutManager(getContext(),2);
                viewBinding.saveStickerRecycler.setLayoutManager(manager);
                adapter = new SaveStickerAdapter(stringList,getContext());
                viewBinding.saveStickerRecycler.setAdapter(adapter);




            }
        });


    }
}