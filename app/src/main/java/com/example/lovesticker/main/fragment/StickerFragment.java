package com.example.lovesticker.main.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.view.View;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentStickerBinding;
import com.example.lovesticker.main.viewmodel.StickerViewModel;
import com.example.lovesticker.sticker.activity.LoadingCategoriesActivity;
import com.example.lovesticker.sticker.fragment.AnimationFragment;
import com.example.lovesticker.sticker.fragment.LoveFragment;
import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.List;


public class StickerFragment extends BaseFragment<StickerViewModel, FragmentStickerBinding> {
    private static StickerFragment stickerFragment;
    private List<AnimatedCategoriesBean.CategoriesData> mCategoriesData;

    public StickerFragment() {
        // Required empty public constructor
    }


    public static StickerFragment newInstance() {
        return stickerFragment;
    }


    @Override
    protected void initView() {
        stickerFragment = this;  //在MainActivity已经启动过，重新new的话会生成新的Fragment
        viewModel.requestAllAnimatedCategoriesData();
        viewBinding.loadingData.setVisibility(View.VISIBLE);
        viewBinding.stickerViewPager.setUserInputEnabled(false);


    }

    public void getPosition(int position){  //拿到单个种类所在位置后并进行跳转到对应界面
        viewBinding.stickerViewPager.setCurrentItem(position +1);
    }


    private void initPager() {
        viewBinding.stickerViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {  //fragment与页面的序号
                switch (position){
                    case 0: return  AnimationFragment.newInstance();

                    default: return LoveFragment.newInstance(mCategoriesData.get(position-1).getLink());
                }
            }

            @Override
            public int getItemCount() { //有几个页面
                return mCategoriesData.size() +1;
            }
        });


        new TabLayoutMediator(viewBinding.tabLayout, viewBinding.stickerViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Animation");
                        break;

                    default: tab.setText(mCategoriesData.get(position-1).getTitle());
                        break;
                }
            }
        }).attach();

    }


    @Override
    protected void dataObserver() {
        viewModel.getAllAnimatedCategoriesLiveData().observe(getViewLifecycleOwner(), new Observer<List<AnimatedCategoriesBean.CategoriesData>>() {
            @Override
            public void onChanged(List<AnimatedCategoriesBean.CategoriesData> categoriesData) {
                if (categoriesData != null){
//                    Log.e("###", "categoriesData size: " + categoriesData.size());
//                    String s = categoriesData.get(0).getLink();
//                    Log.e("###", "getLink: " + s.substring(1));

                    mCategoriesData = categoriesData;
                    viewBinding.loadingData.setVisibility(View.GONE);
                    initPager();

                }
            }
        });
    }



    @Override
    protected void initClickListener() {

        viewBinding.loadingAnimationCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoadingCategoriesActivity.class);
                intent.putExtra("categoriesData", (Serializable) mCategoriesData);
                getContext().startActivity(intent);

            }
        });

    }

}