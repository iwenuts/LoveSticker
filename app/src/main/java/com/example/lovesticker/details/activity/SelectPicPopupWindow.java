package com.example.lovesticker.details.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.lovesticker.R;

public class SelectPicPopupWindow extends PopupWindow {
    private LinearLayout delete;
    private View menuview;
    private Context context;

    @SuppressLint("UseCompatLoadingForDrawables")
    public SelectPicPopupWindow(Context context, View.OnClickListener itemsOnclick){
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menuview = inflater.inflate(R.layout.item_popupwindows,null);
        delete = menuview.findViewById(R.id.delete);
        delete.setOnClickListener(itemsOnclick);
        //设置SelectPicPopupWindow的View
        this.setContentView(menuview);
        //设置SelectPicPopupWindow**弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        //修改高度显示，解决被手机底部虚拟键挡住的问题
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popupwindows_bg));
        //menuview添加ontouchlistener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        menuview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = menuview.findViewById(R.id.ll_popup).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (y < height){
                        dismiss();
                    }
                }
                return true;
            }
        });


    }


}
