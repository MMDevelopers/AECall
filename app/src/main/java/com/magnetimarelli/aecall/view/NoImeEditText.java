package com.magnetimarelli.aecall.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by F49558B on 5/9/2017.
 */

public class NoImeEditText extends android.support.v7.widget.AppCompatEditText {

    public NoImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onCheckIsTextEditor() {
        return false;
    }
}
