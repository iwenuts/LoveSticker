package com.example.lovesticker.main.fragment;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.View;

import com.example.lovesticker.base.BaseFragment;
import com.example.lovesticker.databinding.FragmentMineBinding;
import com.example.lovesticker.main.viewmodel.MineViewModel;
import com.example.lovesticker.mine.activity.SetUpActivity;
import com.example.lovesticker.mine.fragment.SavedPacksFragment;
import com.example.lovesticker.mine.fragment.SavedStickerFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment<MineViewModel, FragmentMineBinding> {

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initClickListener() {

        viewBinding.mineViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position){
                    case 0: return new SavedPacksFragment();

                    default: return new SavedStickerFragment();

                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(viewBinding.mineTabLayout, viewBinding.mineViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("SAVED PACKS");
                            break;

                    case 1: tab.setText("SAVED STICKERS");
                            break;

                }


            }
        }).attach();

        viewBinding.setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SetUpActivity.class);
                getContext().startActivity(intent);
            }
        });




    }

    @Override
    protected void dataObserver() {

    }
}