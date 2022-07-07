package com.example.lovesticker.main.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.lovesticker.R;
import com.example.lovesticker.base.BaseActivity;
import com.example.lovesticker.base.BaseViewModel;
import com.example.lovesticker.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<BaseViewModel, ActivityMainBinding> {

    @Override
    protected void initView() {
        viewBinding.getRoot();
    }

    @Override
    protected void dataObserver() {

    }

    @Override
    protected void initClickListener() {

    }
}