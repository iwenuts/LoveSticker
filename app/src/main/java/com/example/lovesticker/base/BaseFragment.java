package com.example.lovesticker.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.example.lovesticker.util.logger.MLog;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VM extends BaseViewModel,VB extends ViewBinding> extends Fragment {

    protected VM viewModel;
    protected VB viewBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.logLifeStateF("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MLog.logLifeStateF("onCreateView");
        return initViewBinding(inflater,container);
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

    protected View initViewBinding(LayoutInflater inflater,ViewGroup viewGroup){
        try {
            Type genericSuperclass = this.getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            if(parameterizedType != null) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Class<VB> tClass = (Class<VB>) actualTypeArguments[1];
                Method inflate = tClass.getMethod("inflate", LayoutInflater.class,
                        ViewGroup.class,boolean.class);
                viewBinding = (VB) inflate.invoke(viewBinding,inflater,viewGroup,false);

                Method getRoot = tClass.getMethod("getRoot");
                return (View) getRoot.invoke(viewBinding);
            }else{
                throw new ClassCastException("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLog.logLifeStateF("onDestroyView");
    }
}

