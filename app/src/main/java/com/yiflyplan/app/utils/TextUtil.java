package com.yiflyplan.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextUtil {

    public static void disallowSpacesUtil(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 禁止EditText输入空格
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < str.length; i++) {
                        sb.append(str[i]);
                    }
                    editText.setText(sb.toString());
                    editText.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
