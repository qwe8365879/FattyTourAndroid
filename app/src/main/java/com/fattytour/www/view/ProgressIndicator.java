package com.fattytour.www.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fattytour.www.R;

/**
 * Created by Junjie on 10/01/2017.
 */

public class ProgressIndicator extends LinearLayout {
    private TextView progressIndicatorTitle;

    public ProgressIndicator(Context context) {
        super(context);
        init();
    }

    public ProgressIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.progress_indicator, this);
        this.progressIndicatorTitle = (TextView) findViewById(R.id.progressIndicatorTitle);
    }

    public void setTitle(String title){
        this.progressIndicatorTitle.setText(title);
    }
}
