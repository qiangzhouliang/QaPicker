package com.qzl.qapickers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.qzl.qapickers.picker.SinglePicker;

import java.util.List;


public class CustomSinglePicker<T> extends SinglePicker<T> {

    View view;
    TextView tvTitle;
    TextView tvOk;
    String title;
    String confirm;
    public CustomSinglePicker(Activity activity, T[] items) {
        super(activity, items);
        initTopView();
    }

    public CustomSinglePicker(Activity activity, List<T> items) {
        super(activity, items);
        initTopView();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if(null!=tvTitle)
            tvTitle.setText(title);
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
        if(null!=tvTitle)
            tvOk.setText(confirm);
    }
//    @Nullable
//    @Override
//    protected View makeFooterView() {
//        return null;
//    }

    @Nullable
    @Override
    protected View makeHeaderView() {
        setTopLineVisible(false);
        if(null==view){
            return super.makeHeaderView();
        }else{
            return view;
        }

    }
    private void initTopView(){
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_top_button, null);
        tvTitle=view.findViewById(R.id.tv_title);
        tvOk=view.findViewById(R.id.tv_right);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        this.dismiss();
    }
}
