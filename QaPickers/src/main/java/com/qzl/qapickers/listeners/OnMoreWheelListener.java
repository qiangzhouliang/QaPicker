package com.qzl.qapickers.listeners;

/**
 * Created by 涛 on 2017-03-15.
 * 针对地址选择，车牌选择等提供的外部回调接口
 * @author matt
 * blog: addapp.cn
 */

public interface OnMoreWheelListener {
    void onFirstWheeled(int index, String item);

    void onSecondWheeled(int index, String item);

    void onThirdWheeled(int index, String item);
}
