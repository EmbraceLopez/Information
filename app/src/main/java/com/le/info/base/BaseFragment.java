package com.le.info.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kongzue.dialog.v2.WaitDialog;
import com.le.info.widget.QMUITipDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;
    protected Activity activity;
    protected CompositeDisposable compositeDisposable;
    protected QMUITipDialog loadDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(),null);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view,savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 事件管理
     * @param disposable
     */
    protected void addDisposable(Disposable disposable){
        if(compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }

        if(disposable != null && !disposable.isDisposed()){
            compositeDisposable.add(disposable);
        }
    }

    protected Dialog getLoadingDialog() {
        if (loadDialog == null) {
            loadDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .create();
        }
        return loadDialog;
    }

    protected void dismissLoadingDialog() {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }


    /**
     * @return 返回当前frament需要引用的布局
     */
    public abstract int getLayoutId();

    public abstract void init(View view,Bundle savedInstanceState) throws Exception;

    @Override
    public void onDestroyView() {
        if(compositeDisposable != null && compositeDisposable.size() > 0){
            compositeDisposable.clear();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 显示fragment
     * @param fragment
     */
    protected void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    /**
     * 替换fragment
     * @param layoutId
     * @param fragment
     */
    protected void replaceFragment(int layoutId,Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(layoutId,fragment);
        transaction.commit();
    }

    /**
     * 隐藏fragment
     * @param fragment
     */
    protected void hideFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    /**
     * 获取上下文context
     * @return
     */
    @Override
    public Context getContext() {
        Context context = super.getContext();
        if(context != null){
            return context;
        }
        return MyApplication.getContext();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
