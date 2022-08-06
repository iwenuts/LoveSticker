package com.example.lovesticker.main.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityMainBinding;
import com.example.lovesticker.main.FixFragmentNavigator;
import com.example.lovesticker.main.fragment.MineFragment;
import com.example.lovesticker.main.fragment.PackFragment;
import com.example.lovesticker.main.fragment.StickerFragment;
import com.example.lovesticker.main.model.LoveStickerBean;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.gyf.immersionbar.ImmersionBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            return true;
        });

        LoveStickerData.getInstance().getLoveStickerData().enqueue(new Callback<LoveStickerBean>() {
            @Override
            public void onResponse(Call<LoveStickerBean> call, Response<LoveStickerBean> response) {

                LoveStickerBean loveStickerBean = response.body();

                if (loveStickerBean != null) {
//                        Log.e("###", "loveStickerBean " );
//                    Log.e("###",  "BuildConfig : " + BuildConfig.VERSION_CODE + " " + "getUv: "+  loveStickerBean.getUv() );
                    LSMKVUtil.put("rewardinter",loveStickerBean.getRewardinter());
                    LSMKVUtil.put("loadad",loveStickerBean.getLoadad());

                    if (BuildConfig.VERSION_CODE < loveStickerBean.getUv()) {
                        CustomDialog customDialog = new CustomDialog(MainActivity.this);
                        customDialog.setTitle("New version");
                        customDialog.setMessage(loveStickerBean.getContent());
                        customDialog.setCancel("", new CustomDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(CustomDialog dialog) {
                                Toast.makeText(MainActivity.this, "Cancellation successful！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        customDialog.setConfirm("", dialog -> {
                            String packageName = TextUtils.isEmpty(loveStickerBean.getPkg())
                                    ? getPackageName()
                                    : loveStickerBean.getPkg();

                            gotoGooglePlayCBC(MainActivity.this, packageName);

                        });
                        customDialog.setNowUpDate(dialog -> {
                            String packageName = TextUtils.isEmpty(loveStickerBean.getPkg())
                                    ? getPackageName()
                                    : loveStickerBean.getPkg();

                            gotoGooglePlayCBC(MainActivity.this, packageName);
                        });
                        customDialog.setCanceledOnTouchOutside(false);
                        customDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoveStickerBean> call, Throwable t) {

            }
        });

    }


    @Override
    protected void dataObserver() {


    }

    @Override
    protected void initClickListener() {

    }

    public void gotoGooglePlayCBC(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) return;
        try {
            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + packageName));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (ActivityNotFoundException ee) {
            try {
                Intent launchIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(launchIntent);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
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