package com.example.lovesticker.mine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivitySetUpBinding;
import com.gyf.immersionbar.ImmersionBar;

public class SetUpActivity extends BaseActivity<BaseViewModel, ActivitySetUpBinding> {


    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarView(viewBinding.statusBar).init();
    }

    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

    }
}