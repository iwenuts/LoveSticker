package com.example.lovesticker.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ConvertUtils;
import com.example.lovesticker.R;
import com.example.lovesticker.util.logger.MLog;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<VM extends BaseViewModel, VB> extends AppCompatActivity {

    protected VM viewModel;
    protected VB viewBinding;
    protected Handler mainHandler;

    private AlertDialog baseDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.logLifeStateA("onCreate");

        setContentView(initViewBinding());
        mainHandler = new Handler(Looper.getMainLooper());

        initViewModel();
        initStatusBar();

        initView();
        initClickListener();
        dataObserver();
    }

    protected View initViewBinding() {
        try {
            Type genericSuperclass = this.getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            if(parameterizedType != null) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Class<VB> tClass = (Class<VB>) actualTypeArguments[1];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class);
                viewBinding = (VB) inflate.invoke(viewBinding,getLayoutInflater());
                Method getRoot = tClass.getMethod("getRoot");
                return (View) getRoot.invoke(viewBinding);
            }else{
                throw new ClassCastException("");
            }
        } catch (Exception e) {
            Log.e("###", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    protected abstract void initView();

    protected abstract void dataObserver();

    protected abstract void initClickListener();

    protected void initStatusBar() {

//        ImmersionBar.with(this)
//                .fullScreen(true)
//                .hideBar(BarHide.FLAG_HIDE_BAR)
//                .init();

        ImmersionBar.with(this)
                .statusBarColor("#00FFFFFF")
                .navigationBarColor("#00FFFFFF")
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .statusBarView(getStatusView())
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .init();
    }

    protected View getStatusView() {
        return null;
    }

    private void initProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);

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
        baseDlg.getWindow().setLayout(ConvertUtils.dp2px(100f),
                ConvertUtils.dp2px(200f));
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


    public static final class MessageDialogFragment extends DialogFragment {
        private static final String ARG_TITLE_ID = "title_id";
        private static final String ARG_MESSAGE = "message";

        public static DialogFragment newInstance(@StringRes int titleId, String message) {
            DialogFragment fragment = new MessageDialogFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(ARG_TITLE_ID, titleId);
            arguments.putString(ARG_MESSAGE, message);
            fragment.setArguments(arguments);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @StringRes final int title = getArguments().getInt(ARG_TITLE_ID);
            String message = getArguments().getString(ARG_MESSAGE);

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dismiss());

            if (title != 0) {
                dialogBuilder.setTitle(title);
            }
            return dialogBuilder.create();
        }
    }

    protected void shareAny(String path){
        Intent whatsappIntent = new Intent(android.content.Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));//add image path
        startActivity(Intent.createChooser(whatsappIntent, "Share image using"));


//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
////        shareIntent.setPackage("com.whatsapp");
////        shareIntent.putExtra(Intent.EXTRA_TEXT,title + "\n\nLink : " + link );
//        shareIntent.setType("image/gif");
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
//
//        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }



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
        return new ViewModelProvider(this, createVMFactory()).get(cls);
    }

    protected ViewModelProvider.Factory createVMFactory() {
        return new ViewModelProvider.NewInstanceFactory();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MLog.logLifeStateA("onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        MLog.logLifeStateA("onSaveInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MLog.logLifeStateA("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLog.logLifeStateA("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.logLifeStateA("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.logLifeStateA("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLog.logLifeStateA("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.logLifeStateA("onDestroy");
        dismissProgressDialog();
        mainHandler.removeCallbacksAndMessages(null);
        mainHandler = null;
    }
}
