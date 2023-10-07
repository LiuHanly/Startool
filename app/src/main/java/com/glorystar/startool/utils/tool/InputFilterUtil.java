package com.glorystar.startool.utils.tool;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFilterUtil {
    /**
     * @param editText
     */
    public static void setEditTextInhibitInput(EditText editText){
        InputFilter[] filters = new InputFilter[3];
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))return "";
                else return null;
            }
        };
        filters[0] = filter;

        InputFilter filter1 =new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        filters[1] = filter1;

        InputFilter filter2 =new InputFilter.LengthFilter(32);
        filters[2] = filter2;

        editText.setFilters(filters);
    }
}
