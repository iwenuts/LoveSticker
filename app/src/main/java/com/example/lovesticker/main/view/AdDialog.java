package com.example.lovesticker.main.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.lovesticker.databinding.ActivityAdDialogBinding;
import com.example.lovesticker.databinding.ActivityCustomDialogBinding;

public class AdDialog  extends Dialog implements View.OnClickListener {
    private ActivityAdDialogBinding viewBinding;
    private Context context;
    //声明xml文件中组件中的text变量，为string类，方便之后改
    private String title,message;
    private String cancel,confirm;

    //声明两个点击事件，等会一定要为取消和确定这两个按钮也点击事件
    private OnCancelListener cancelListener;
    private OnConfirmListener confirmListener;

    public AdDialog(@NonNull Context context) {
        super(context);
    }

    //设置四个组件的内容
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setCancel(String cancel, OnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener=cancelListener;
    }
    public void setConfirm(String confirm, OnConfirmListener confirmListener){
        this.confirm=confirm;
        this.confirmListener=confirmListener;
    }


    //写两个接口，当要创建一个CustomDialog对象的时候，必须要实现这两个接口
    //也就是说，当要弹出一个自定义dialog的时候，取消和确定这两个按钮的点击事件，一定要重写！
    public interface OnCancelListener{
        void onCancel(CustomDialog dialog);
    }
    public interface OnConfirmListener{
        void onConfirm(CustomDialog dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityAdDialogBinding.inflate(LayoutInflater.from(context));
        setContentView(viewBinding.getRoot());

        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p =getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {

    }
}