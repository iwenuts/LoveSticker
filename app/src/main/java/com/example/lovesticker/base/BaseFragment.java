package com.example.lovesticker.base;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ConvertUtils;
import com.example.lovesticker.R;
import com.example.lovesticker.util.ads.MaxADManager;
import com.example.lovesticker.util.logger.MLog;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VM extends BaseViewModel,VB> extends Fragment {

    protected VM viewModel;

    private AlertDialog baseDlg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.logLifeStateF("onCreate");
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MLog.logLifeStateF("onViewCreated");
        initViewModel();

        initView();
        initClickListener();
        dataObserver();

    }

    protected abstract void initView();
    protected abstract void initClickListener();
    protected abstract void dataObserver();

    private void initViewModel() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        if(parameterizedType != null) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Class<VM> tClass = (Class<VM>) actualTypeArguments[0];
            viewModel = createViewModel(tClass);
        }else{
            throw new ClassCastException("");
        }
    }

    private VM createViewModel(Class<VM> cls) {
        return new ViewModelProvider(requireActivity(),createVMFactory()).get(cls);
    }

    protected ViewModelProvider.Factory createVMFactory() {
        return new ViewModelProvider.NewInstanceFactory();
    }

    private void initProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.pull_down_refresh, null);

        builder.setView(view);
        baseDlg = builder.create();
        baseDlg.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        baseDlg.setCanceledOnTouchOutside(false);
        baseDlg.setCancelable(false);
        baseDlg.setOnDismissListener(d -> {

        });
    }

    protected void showProgressDialog() {
        if (baseDlg == null) {
            initProgressDialog();
        }
        baseDlg.show();
        baseDlg.setOnDismissListener(null);
//        baseDlg.getWindow().setLayout(ConvertUtils.dp2px(100f),
//                ConvertUtils.dp2px(200f));
    }

    protected void showProgressDialog(DialogInterface.OnDismissListener dismissListener) {
        if (baseDlg == null) {
            initProgressDialog();
        }
        baseDlg.show();
        baseDlg.setOnDismissListener(dismissListener);
        baseDlg.getWindow().setLayout(ConvertUtils.dp2px(100f),
                ConvertUtils.dp2px(200f));
    }

    protected void dismissProgressDialog() {
        if (baseDlg != null) {
            try {
                baseDlg.dismiss();
                baseDlg.setOnDismissListener(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        MLog.logLifeStateF("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        MLog.logLifeStateF("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MLog.logLifeStateF("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        MLog.logLifeStateF("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.logLifeStateF("onDestroy");
        dismissProgressDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLog.logLifeStateF("onDestroyView");
    }
}

