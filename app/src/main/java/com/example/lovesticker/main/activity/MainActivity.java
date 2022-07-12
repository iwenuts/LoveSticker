package com.example.lovesticker.main.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityMainBinding;
import com.example.lovesticker.main.fragment.MineFragment;
import com.example.lovesticker.main.fragment.PackFragment;
import com.example.lovesticker.main.fragment.StickerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;

public class MainActivity extends BaseActivity<BaseViewModel, ActivityMainBinding> {

    private Fragment packFragment,stickerFragment,mineFragment;
    private Fragment[] fragments;

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(viewBinding.bottomNavigationView,navController);





    }



    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

    }


}