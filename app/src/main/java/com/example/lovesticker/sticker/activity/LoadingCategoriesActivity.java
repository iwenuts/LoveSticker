package com.example.lovesticker.sticker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityLoadingCategoriesBinding;
import com.example.lovesticker.sticker.adapter.LoadingCategoriesAdapter;
import com.example.lovesticker.sticker.fragment.LoveFragment;
import com.example.lovesticker.sticker.model.AnimatedCategoriesBean;
import com.example.lovesticker.sticker.model.SingleAnimatedCategoriesBean;
import com.example.lovesticker.util.inteface.ImageListener;
import com.example.lovesticker.util.mmkv.LSMKVUtil;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

public class LoadingCategoriesActivity extends BaseActivity<BaseViewModel, ActivityLoadingCategoriesBinding> {

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();

        List<AnimatedCategoriesBean.CategoriesData> categoriesData = (List<AnimatedCategoriesBean.CategoriesData>)
                getIntent().getSerializableExtra("categoriesData");

        Log.e("###", "categoriesData: "+ categoriesData.size() );

        if (categoriesData != null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            viewBinding.categoriesRecycler.setLayoutManager(layoutManager);
            LoadingCategoriesAdapter adapter = new LoadingCategoriesAdapter(categoriesData,this);
            viewBinding.categoriesRecycler.setAdapter(adapter);
        }

    }

    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

        viewBinding.categoriesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (LSMKVUtil.getBoolean("categoriesJump",false)){
//            LSMKVUtil.put("categoriesJump",false);
//            finish();
//        }
//    }
}