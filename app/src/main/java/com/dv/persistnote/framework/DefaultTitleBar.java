package com.dv.persistnote.framework;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dv.persistnote.R;
import com.dv.persistnote.base.ResTools;

import org.w3c.dom.Text;

/**
 * Created by Hang on 2016/3/21.
 */
public class DefaultTitleBar extends RelativeLayout implements View.OnClickListener {

    private ImageView mBackButton;
    private TextView mTitleText;

    public DefaultTitleBar(Context context) {
        super(context);

        mBackButton = new ImageView(getContext());
        mBackButton.setImageDrawable(ResTools.getDrawable(R.drawable.back));
        mBackButton.setOnClickListener(this);

        LayoutParams lp = new LayoutParams(ResTools.getDimenInt(R.dimen.common_icon_width), ResTools.getDimenInt(R.dimen.common_icon_width));
        lp.addRule(ALIGN_PARENT_LEFT);
        lp.addRule(CENTER_VERTICAL);
        addView(mBackButton, lp);

        mTitleText = new TextView(getContext());
        mTitleText.setTextColor(ResTools.getColor(R.color.default_black));
        mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResTools.getDimenInt(R.dimen.title_bar_text));
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_IN_PARENT);
        addView(mTitleText, lp);

        setBackgroundColor(ResTools.getColor(R.color.default_white));
        int padding = ResTools.getDimenInt(R.dimen.common_margin_16);
        setPadding(padding, 0, padding, 0);
    }

    public void setTitle(String title) {
        mTitleText.setText(title);
    }

    @Override
    public void onClick(View view) {

    }
}
