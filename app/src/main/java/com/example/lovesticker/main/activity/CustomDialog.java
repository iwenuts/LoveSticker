package com.example.lovesticker.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.lovesticker.R;
import com.example.lovesticker.databinding.ActivityCustomDialogBinding;
import com.example.lovesticker.util.mmkv.LSMKVUtil;

public class CustomDialog extends Dialog implements View.OnClickListener{
    private ActivityCustomDialogBinding viewBinding;
    private Context context;

    //声明xml文件中组件中的text变量，为string类，方便之后改
    private String title,message;
    private String cancel,confirm;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;
    private IOnNowUpDateListener nowUpDateListener;

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    //设置四个组件的内容
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setCancel(String cancel,IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener=cancelListener;
    }
    public void setConfirm(String confirm,IOnConfirmListener confirmListener){
        this.confirm=confirm;
        this.confirmListener=confirmListener;
    }

    public void setNowUpDate(IOnNowUpDateListener nowUpDateListener){
        this.nowUpDateListener = nowUpDateListener;
    }


    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface IOnCancelListener{
        void onCancel(CustomDialog dialog);
    }
    public interface IOnConfirmListener{
        void onConfirm(CustomDialog dialog);
    }

    public interface IOnNowUpDateListener{
        void onNowUpdate(CustomDialog dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityCustomDialogBinding.inflate(LayoutInflater.from(context));
        setContentView(viewBinding.getRoot());


        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p =getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
//        p.width = (int)(size.x * 0.8);//是dialog的宽度为app界面的80%
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);

        if (LSMKVUtil.getBoolean("isForce",false)){
            viewBinding.update.setVisibility(View.GONE);
            viewBinding.later.setVisibility(View.GONE);
            viewBinding.nowUpdate.setVisibility(View.VISIBLE);
        }else {
            viewBinding.update.setVisibility(View.VISIBLE);
            viewBinding.later.setVisibility(View.VISIBLE);
            viewBinding.nowUpdate.setVisibility(View.GONE);
        }

        //设置组件对象的text参数
        if (!TextUtils.isEmpty(title)){
            viewBinding.upgradeTitle.setText(title);
        }
        if (!TextUtils.isEmpty(message)){
            viewBinding.upgradeContent.setText(message);
        }


        //为两个按钮添加点击事件
        viewBinding.update.setOnClickListener(this);
        viewBinding.later.setOnClickListener(this);
        viewBinding.nowUpdate.setOnClickListener(this);

    }
    //重写onClick方法
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.later:
                if(cancelListener!=null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.update:
                if(confirmListener!=null){
                    confirmListener.onConfirm(this);
                }
                dismiss();//按钮按之后会消失
                break;
            case R.id.now_update:
                if (nowUpDateListener != null){
                    nowUpDateListener.onNowUpdate(this);
                }
                dismiss();
                break;
        }
    }


}