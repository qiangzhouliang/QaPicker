package com.qzl.qapickers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qzl.qapickers.picker.DatePicker;


public class CustomDatePicker extends DatePicker {
    View view;
    TextView tvTitle;
    TextView tvOk;
    String title;
    String confirm;

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


    public CustomDatePicker(Activity activity) {
        super(activity);
        initTopView();
    }

    public CustomDatePicker(Activity activity, int mode) {
        super(activity, mode);
        initTopView();
    }

//    @Nullable
//    @Override
//    protected View makeFooterView() {
//        return null;
//    }

    @NonNull
    @Override
    protected View makeCenterView() {
        return super.makeCenterView();
    }

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
    protected void onSubmit() {
        super.onSubmit();
        this.dismiss();
    }
}
