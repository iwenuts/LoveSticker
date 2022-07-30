package com.example.lovesticker.main.activity;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.Navigation;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityMainBinding;
import com.example.lovesticker.main.FixFragmentNavigator;
import com.example.lovesticker.main.fragment.MineFragment;
import com.example.lovesticker.main.fragment.PackFragment;
import com.example.lovesticker.main.fragment.StickerFragment;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;

public class MainActivity extends BaseActivity<BaseViewModel, ActivityMainBinding> {


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.fragment);

        //fragment的重复加载问题和NavController有关
        final NavController navController = NavHostFragment.findNavController(navHostFragment);
        NavigatorProvider provider = navController.getNavigatorProvider();
        //设置自定义的navigator
        FixFragmentNavigator fixFragmentNavictor = new FixFragmentNavigator(this, navHostFragment.getChildFragmentManager(), navHostFragment.getId());
        provider.addNavigator(fixFragmentNavictor);
//        NavigationUI.setupWithNavController(viewBinding.bottomNavigationView,navController);

        NavGraph navDestinations = initNavGraph(provider, fixFragmentNavictor);
        navController.setGraph(navDestinations);


        //控制多次的点击同一个item时fragment的多次刷新问题
        viewBinding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            navController.navigate(item.getItemId());

//            if (item.isChecked()){
//                return true;
//            }
            return  true;
        });

        MaxADManager.initInterstitialDetailAd(this);
        MaxADManager.initInterstitialBacklAd(this);
    }



    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

    }


    private NavGraph initNavGraph(NavigatorProvider provider, FixFragmentNavigator fragmentNavigator) {
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        //用自定义的导航器来创建目的地
        FragmentNavigator.Destination destination1 = fragmentNavigator.createDestination();
        destination1.setId(R.id.packFragment);
        destination1.setClassName(PackFragment.class.getCanonicalName());
        navGraph.addDestination(destination1);

        FragmentNavigator.Destination destination2 = fragmentNavigator.createDestination();
        destination2.setId(R.id.stickerFragment);
        destination2.setClassName(StickerFragment.class.getCanonicalName());
        navGraph.addDestination(destination2);

        FragmentNavigator.Destination destination3 = fragmentNavigator.createDestination();
        destination3.setId(R.id.mineFragment);
        destination3.setClassName(MineFragment.class.getCanonicalName());
        navGraph.addDestination(destination3);


        navGraph.setStartDestination(destination1.getId());
        return navGraph;

    }



}