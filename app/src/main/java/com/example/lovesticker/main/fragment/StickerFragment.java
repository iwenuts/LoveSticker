package com.example.lovesticker.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentStickerBinding;
import com.example.lovesticker.main.viewmodel.StickerViewModel;
import com.example.lovesticker.sticker.fragment.AnimationFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


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
        initPager();


    }

    private void initPager() {
        viewBinding.stickerViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {  //fragment与页面的序号
                switch (position){
                    case 0: return new AnimationFragment();

                    case 1: return new AnimationFragment();

                    case 2: return new AnimationFragment();

                    default: return new AnimationFragment();
                }
            }

            @Override
            public int getItemCount() { //有几个页面
                return 6;
            }
        });


        new TabLayoutMediator(viewBinding.tabLayout, viewBinding.stickerViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Animation");
                        break;
                    case 1: tab.setText("New");
                        break;
                    case 2: tab.setText("Emoji");
                        break;
                    case 3: tab.setText("Love");
                        break;
                    case 4: tab.setText("Hello");
                        break;
                    case 5: tab.setText("Bear");
                        break;

                }
            }
        }).attach();





    }

    @Override
    protected void initClickListener() {

    }

    @Override
    protected void dataObserver() {

    }


}