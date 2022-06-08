package com.qzl.qapickers.picker;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.qzl.qapickers.adapter.ArrayWheelAdapter;
import com.qzl.qapickers.listeners.OnItemPickListener;
import com.qzl.qapickers.listeners.OnMoreItemPickListener;
import com.qzl.qapickers.listeners.OnMoreWheelListener;
import com.qzl.qapickers.util.LogUtils;
import com.qzl.qapickers.widget.WheelView;

import java.util.List;


/**
 * 两级、三级联动选择器。默认只初始化第一级数据，第二三级数据由联动获得。
 * <p/>
 * @author matt
 * blog: addapp.cn
 * @see DataProvider
 */
public class LinkagePicker extends WheelPicker {
    protected String selectedFirstItem = "", selectedSecondItem = "", selectedThirdItem = "";
    protected String firstLabel = "", secondLabel = "", thirdLabel = "";
    protected int selectedFirstIndex = 0, selectedSecondIndex = 0, selectedThirdIndex = 0;
    protected DataProvider provider;
    private OnMoreItemPickListener onMoreItemPickListener;
    private float firstColumnWeight = 0;//第一级显示的宽度比
    private float secondColumnWeight = 0;//第二级显示的宽度比
    private float thirdColumnWeight = 0;//第三级显示的宽度比
    private OnMoreWheelListener onMoreWheelListener;

    public LinkagePicker(Activity activity) {
        super(activity);
    }

    public LinkagePicker(Activity activity, DataProvider provider) {
        super(activity);
        this.provider = provider;
    }

    protected void setProvider(DataProvider provider) {
        this.provider = provider;
    }

    public void setSelectedIndex(int firstIndex, int secondIndex) {
        setSelectedIndex(firstIndex, secondIndex, 0);
    }

    public void setSelectedIndex(int firstIndex, int secondIndex, int thirdIndex) {
        selectedFirstIndex = firstIndex;
        selectedSecondIndex = secondIndex;
        selectedThirdIndex = thirdIndex;
    }

    public void setSelectedItem(String firstText, String secondText) {
        setSelectedItem(firstText, secondText, "");
    }

    public void setSelectedItem(String firstText, String secondText, String thirdText) {
        if (null == provider) {
            throw new IllegalArgumentException("please set data provider at first");
        }
        List<String> firstData = provider.provideFirstData();
        for (int i = 0; i < firstData.size(); i++) {
            String ft = firstData.get(i);
            if (ft.contains(firstText)) {
                selectedFirstIndex = i;
                LogUtils.verbose("init select first text: " + ft + ", index:" + selectedFirstIndex);
                break;
            }
        }
        List<String> secondData = provider.provideSecondData(selectedFirstIndex);
        for (int j = 0; j < secondData.size(); j++) {
            String st = secondData.get(j);
            if (st.contains(secondText)) {
                selectedSecondIndex = j;
                LogUtils.verbose("init select second text: " + st + ", index:" + selectedSecondIndex);
                break;
            }
        }
        if (provider.isOnlyTwo()) {
            return;//仅仅二级联动
        }
        List<String> thirdData = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
        for (int k = 0; k < thirdData.size(); k++) {
            String tt = thirdData.get(k);
            if (tt.contains(thirdText)) {
                selectedThirdIndex = k;
                LogUtils.verbose("init select third text: " + tt + ", index:" + selectedThirdIndex);
                break;
            }
        }
    }

    public void setLabel(String firstLabel, String secondLabel) {
        setLabel(firstLabel, secondLabel, "");
    }

    public void setLabel(String firstLabel, String secondLabel, String thirdLabel) {
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.thirdLabel = thirdLabel;
    }

    public String getSelectedFirstItem() {
        selectedFirstItem = provider.provideFirstData().get(selectedFirstIndex);
        return selectedFirstItem;
    }

    public String getSelectedSecondItem() {
        selectedSecondItem = provider.provideSecondData(selectedFirstIndex).get(selectedSecondIndex);
        return selectedSecondItem;
    }

    public String getSelectedThirdItem() {
        selectedThirdItem = provider .provideThirdData(selectedFirstIndex,selectedSecondIndex).get(selectedThirdIndex);
        return selectedThirdItem;
    }

    public int getSelectedFirstIndex() {
        return selectedFirstIndex;
    }

    public int getSelectedSecondIndex() {
        return selectedSecondIndex;
    }

    public int getSelectedThirdIndex() {
        return selectedThirdIndex;
    }

    /**
     * 设置每列的宽度比例，将屏幕分为三列，每列范围为0.0～1.0，如0.3333表示约占屏幕的三分之一。
     */
    public void setColumnWeight(@FloatRange(from = 0, to = 1) float firstColumnWeight,
                                @FloatRange(from = 0, to = 1) float secondColumnWeight,
                                @FloatRange(from = 0, to = 1) float thirdColumnWeight) {
        this.firstColumnWeight = firstColumnWeight;
        this.secondColumnWeight = secondColumnWeight;
        this.thirdColumnWeight = thirdColumnWeight;
    }

    /**
     * 设置每列的宽度比例，将屏幕分为两列，每列范围为0.0～1.0，如0.5表示占屏幕的一半。
     */
    public void setColumnWeight(@FloatRange(from = 0, to = 1) float firstColumnWeight,
                                @FloatRange(from = 0, to = 1) float secondColumnWeight) {
        this.firstColumnWeight = firstColumnWeight;
        this.secondColumnWeight = secondColumnWeight;
        this.thirdColumnWeight = 0;
    }

    /**
     * 设置滑动监听器
     */
    public void setOnMoreWheelListener(OnMoreWheelListener onMoreWheelListener) {
        this.onMoreWheelListener = onMoreWheelListener;
    }

    public void setOnMoreItemPickListener(OnMoreItemPickListener onMoreItemPickListener) {
        this.onMoreItemPickListener = onMoreItemPickListener;
    }

    /**
     * 根据比例计算，获取每列的实际宽度。
     * 三级联动默认每列宽度为屏幕宽度的三分之一，两级联动默认每列宽度为屏幕宽度的一半。
     */
    @Size(3)
    protected int[] getColumnWidths(boolean onlyTwoColumn) {
        LogUtils.verbose(this, String.format(java.util.Locale.CHINA, "column weight is: %f-%f-%f"
                , firstColumnWeight, secondColumnWeight, thirdColumnWeight));
        int[] widths = new int[3];
        // fixed: 17-1-7 Equality tests should not be made with floating point values.
        if ((int) firstColumnWeight == 0 && (int) secondColumnWeight == 0
                && (int) thirdColumnWeight == 0) {
            if (onlyTwoColumn) {
                widths[0] = screenWidthPixels / 2;
                widths[1] = widths[0];
                widths[2] = 0;
            } else {
                widths[0] = screenWidthPixels / 3;
                widths[1] = widths[0];
                widths[2] = widths[0];
            }
        }
        else {
            widths[0] = (int) (screenWidthPixels * firstColumnWeight);
            widths[1] = (int) (screenWidthPixels * secondColumnWeight);
            widths[2] = (int) (screenWidthPixels * thirdColumnWeight);
        }
        return widths;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        if (null == provider) {
            throw new IllegalArgumentException("please set data provider before make view");
        }
        int[] widths = getColumnWidths(provider.isOnlyTwo());
        LinearLayout layout = new LinearLayout(activity);
        layout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams wheelParams = new LinearLayout.LayoutParams(widths[0], WRAP_CONTENT);
        LinearLayout.LayoutParams wheelParams1 = new LinearLayout.LayoutParams(widths[1], WRAP_CONTENT);
        LinearLayout.LayoutParams wheelParams2 = new LinearLayout.LayoutParams(widths[2], WRAP_CONTENT);
        if(weightEnable){
            wheelParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            wheelParams1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            wheelParams2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            wheelParams.weight = 1;
            wheelParams1.weight = 1;
            if (!provider.isOnlyTwo()) {
                wheelParams2.weight = 1;
            }
        }
        //判断是选择ios滚轮模式还是普通模式
        final WheelView firstView = new WheelView(activity);
        firstView.setCanLoop(canLoop);
        firstView.setTextSize(textSize);
        firstView.setSelectedTextColor(textColorFocus);
        firstView.setUnSelectedTextColor(textColorNormal);
        firstView.setLineConfig(lineConfig);
        firstView.setAdapter(new ArrayWheelAdapter<>(provider.provideFirstData()));
        firstView.setCurrentItem(selectedFirstIndex);
        firstView.setLayoutParams(wheelParams);
        layout.addView(firstView);
        if (!TextUtils.isEmpty(firstLabel)){
            if(isOuterLabelEnable()){
                TextView labelView = new TextView(activity);
                labelView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                labelView.setTextSize(textSize);
                labelView.setTextColor(textColorFocus);
                labelView.setText(firstLabel);
                layout.addView(labelView);
            }else{
                firstView.setLabel(firstLabel);
            }

        }

        final WheelView secondView = new WheelView(activity);
        secondView.setCanLoop(canLoop);
        secondView.setTextSize(textSize);
        secondView.setSelectedTextColor(textColorFocus);
        secondView.setUnSelectedTextColor(textColorNormal);
        secondView.setLineConfig(lineConfig);
        secondView.setAdapter(new ArrayWheelAdapter<>(provider.provideSecondData(selectedFirstIndex)));
        secondView.setCurrentItem(selectedSecondIndex);
        secondView.setLayoutParams(wheelParams1);
        layout.addView(secondView);
        if (!TextUtils.isEmpty(secondLabel)){
            if(isOuterLabelEnable()){
                TextView labelView = new TextView(activity);
                labelView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                labelView.setTextSize(textSize);
                labelView.setTextColor(textColorFocus);
                labelView.setText(secondLabel);
                layout.addView(labelView);
            }else{
                secondView.setLabel(secondLabel);
            }

        }
        final WheelView thirdView = new WheelView(activity);
        if (!provider.isOnlyTwo()) {
            thirdView.setCanLoop(canLoop);
            thirdView.setTextSize(textSize);
            thirdView.setSelectedTextColor(textColorFocus);
            thirdView.setUnSelectedTextColor(textColorNormal);
            thirdView.setLineConfig(lineConfig);
            thirdView.setAdapter(new ArrayWheelAdapter<>(provider.provideThirdData(selectedFirstIndex, selectedSecondIndex)));
            thirdView.setCurrentItem(selectedThirdIndex);

            if (!TextUtils.isEmpty(thirdLabel)){
                if(isOuterLabelEnable()){
                    TextView labelView = new TextView(activity);
                    labelView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    labelView.setTextSize(textSize);
                    labelView.setTextColor(textColorFocus);
                    labelView.setText(thirdLabel);
                    layout.addView(labelView);
                }else{
                    thirdView.setLabel(thirdLabel);
                }

            }
        }

        firstView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int i,String item) {
                selectedFirstIndex = i;
                selectedFirstItem = item;
                if (!canLinkage) {
                    return;
                }
                selectedSecondIndex = 0;//重置第二级索引
                selectedThirdIndex = 0;//重置第三级索引
                if (onMoreWheelListener != null) {
                    onMoreWheelListener.onFirstWheeled(selectedFirstIndex, selectedFirstItem);
                }
                LogUtils.error(this, "change second data after first wheeled");
                //根据第一级数据获取第二级数据
                List<String> secondData = provider.provideSecondData(selectedFirstIndex);
                secondView.setAdapter(new ArrayWheelAdapter<>(secondData));
                secondView.setCurrentItem(selectedSecondIndex);
                if (provider.isOnlyTwo()) {
                    return;//仅仅二级联动
                }

                //根据第二级数据获取第三级数据
                List<String> thirdData = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                thirdView.setAdapter(new ArrayWheelAdapter<>(thirdData));
                thirdView.setCurrentItem(selectedThirdIndex);
            }
        });

        secondView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int i, String item) {
                selectedSecondItem = item;
                selectedSecondIndex = i;
                if (!canLinkage) {
                    return;
                }
                selectedThirdIndex = 0;//重置第三级索引
                if (onMoreWheelListener != null) {
                    onMoreWheelListener.onSecondWheeled(selectedSecondIndex, selectedSecondItem);
                }
                if (provider.isOnlyTwo()) {
                    return;//仅仅二级联动
                }
                LogUtils.error(this, "change third data after second wheeled");
                List<String> thirdData = provider.provideThirdData(selectedFirstIndex, selectedSecondIndex);
                //根据第二级数据获取第三级数据
                thirdView.setAdapter(new ArrayWheelAdapter<>(thirdData));
                thirdView.setCurrentItem(selectedThirdIndex);
            }
        });
        if (provider.isOnlyTwo()) {
            return layout;//仅仅二级联动
        }
        thirdView.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int i,String item) {
                selectedThirdItem = item;
                selectedThirdIndex = i;
                if (onMoreWheelListener != null) {
                    onMoreWheelListener.onThirdWheeled(selectedThirdIndex, selectedThirdItem);
                }
            }
        });
        return layout;

    }

    @Override
    public void onSubmit() {
        if (onMoreItemPickListener == null) {
            return;
        }
        selectedFirstItem = provider.provideFirstData().get(selectedFirstIndex);
        selectedSecondItem = provider.provideSecondData(selectedFirstIndex).get(selectedSecondIndex);
        if (provider.isOnlyTwo()) {
            onMoreItemPickListener.onItemPicked(selectedFirstItem, selectedSecondItem, null);
        } else {
            selectedThirdItem = provider .provideThirdData(selectedFirstIndex,selectedSecondIndex).get(selectedThirdIndex);
            onMoreItemPickListener.onItemPicked(selectedFirstItem, selectedSecondItem, selectedThirdItem);
        }
    }



    /**
     * 数据提供接口
     */
    public interface DataProvider {

        /**
         * 是否只是二级联动
         */
        boolean isOnlyTwo();

        /**
         * 提供第一级数据
         */
        List<String> provideFirstData();

        /**
         * 提供第二级数据
         */
        List<String> provideSecondData(int firstIndex);

        /**
         * 提供第三级数据
         */
        List<String> provideThirdData(int firstIndex, int secondIndex);

    }



}
