package com.yiflyplan.app.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 *  点击发送按钮后手机会接收到验证码，倒计时60s
 * @ClassName: TimeCount
 * @date 2017年1月10日
 */
public class TimeCountUtil extends CountDownTimer {

    private Button btn_count;

    public TimeCountUtil(long millisInFuture, long countDownInterval, Button btn_count) {
        super(millisInFuture, countDownInterval);
        this.btn_count = btn_count;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        btn_count.setEnabled(false);
        btn_count.setText(millisUntilFinished / 1000 + "秒");
    }

    @Override
    public void onFinish() {
        btn_count.setEnabled(true);
        btn_count.setText("获取短信验证码");

    }

}

