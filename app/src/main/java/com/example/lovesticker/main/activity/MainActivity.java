package com.example.lovesticker.main.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.example.lovesticker.BuildConfig;
import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityMainBinding;
import com.example.lovesticker.databinding.ActivitySingleAnimatedDetailsBinding;
import com.example.lovesticker.main.view.CustomDialog;
import com.example.lovesticker.main.view.FixFragmentNavigator;
import com.example.lovesticker.main.fragment.MineFragment;
import com.example.lovesticker.main.fragment.PackFragment;
import com.example.lovesticker.main.fragment.StickerFragment;
import com.example.lovesticker.main.model.LoveStickerBean;
import com.example.lovesticker.util.constant.LSConstant;
import com.example.lovesticker.util.event.LSEventUtil;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lovesticker.util.ads.MaxADManager.loadInterstitialBackAd;
import static com.example.lovesticker.util.ads.MaxADManager.loadInterstitialDetailAd;
import static com.example.lovesticker.util.ads.MaxADManager.loadRewardAd;

public class MainActivity extends BaseActivity<BaseViewModel, ActivityMainBinding> {
    private ActivityMainBinding viewBinding;

    @Override
    protected void initViewBinding() {
        viewBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(viewBinding.getRoot());
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        LSMKVUtil.put("rewardInterval", 0);
        LSMKVUtil.put("hitsNumber", 0);

        if (!LSConstant.initReward){
            loadRewardAd(this);
        }

        if (!LSConstant.initInterstitial){
            Log.e("###", "LSConstant.initInterstitial " );
            loadInterstitialDetailAd(this);
        }

        if (!LSConstant.initInterstitialBack){
            Log.e("###", "LSConstant.initInterstitialBack " );
            loadInterstitialBackAd(this);
        }


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

            if (item.getItemId() == R.id.packFragment){
                LSEventUtil.logToTabPack();
            }else if (item.getItemId() == R.id.stickerFragment){
                LSEventUtil.logToTabStickers();
            }else {
                LSEventUtil.logToTabMine();
            }


            if (item.isChecked()){
                return true;
            }
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
                    if (BuildConfig.DEBUG){
                        LSMKVUtil.put("loadad",true);
                    }else {
                        LSMKVUtil.put("loadad",loveStickerBean.getLoadad());
                    }
                    LSMKVUtil.put("isForce",loveStickerBean.getForce());

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
    public void onBackPressed() {
        ActivityUtils.startHomeActivity();
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

    private void permissionsRD(){
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList, "The permission to be " +
                                    "reapplied is the permission that the program must rely on",
                            "I understand", "cancel");
                }).request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {

            }else {
                Toast.makeText(this, "You have denied permission to " +
                        "read and write photos and files on your device", Toast.LENGTH_SHORT).show();
            }
        });

    }

}