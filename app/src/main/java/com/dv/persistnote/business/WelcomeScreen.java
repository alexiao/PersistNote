package com.dv.persistnote.business;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.persistnote.R;
import com.dv.persistnote.framework.ActionId;
import com.dv.persistnote.framework.ui.AbstractScreen;
import com.dv.persistnote.framework.FontManager;
import com.dv.persistnote.framework.ui.UICallBacks;


/**
 * Created by Hang on 2016/3/14.
 */
public class WelcomeScreen extends AbstractScreen implements View.OnClickListener{

    private TextView mCenterText;

    private TextView mRegisterButton;

    private TextView mLoginButton;

    public WelcomeScreen(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        init();
    }

    private void init() {

        mCenterText = new TextView(getContext());
        Drawable drawable = getResources().getDrawable(R.drawable.main_logo);
        int edge = (int)getResources().getDimension(R.dimen.welcome_icon_width);
        drawable.setBounds(0, 0, edge, edge);
        mCenterText.setCompoundDrawables(null, drawable, null, null);
        mCenterText.setCompoundDrawablePadding(edge / 3);
        mCenterText.setText(getResources().getString(R.string.slogen));
        mCenterText.setTextColor(getResources().getColor(R.color.default_black));
        mCenterText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.common_text_size_20));
        mCenterText.setTypeface(FontManager.getInstance().getDefaultTypeface());

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = (int) getResources().getDimension(R.dimen.welcome_icon_top_margin);
        addView(mCenterText, lp);


        LinearLayout container = new LinearLayout(getContext());

        mRegisterButton = createActionButton("注册");
        mLoginButton = createActionButton("登录");

        container.addView(mRegisterButton);
        container.addView(mLoginButton);


        lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.bottomMargin = (int) getResources().getDimension(R.dimen.welcome_icon_top_margin);
        lp.leftMargin = lp.rightMargin = (int) getResources().getDimension(R.dimen.action_button_margin);
        addView(container, lp);
    }


    private TextView createActionButton(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.common_text_size_22));
        textView.setTextColor(getResources().getColor(R.color.light_main_color));
        textView.setTypeface(FontManager.getInstance().getDefaultTypeface());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        textView.setLayoutParams(lp);
        textView.setOnClickListener(this);

        return textView;
    }

    @Override
    public void onClick(View view) {
        if(view == mRegisterButton) {
            Toast.makeText(getContext(), "点击注册", Toast.LENGTH_SHORT).show();
        } if (view == mLoginButton) {
            Toast.makeText(getContext(), "点击登录", Toast.LENGTH_SHORT).show();
            mCallBacks.handleAction(ActionId.OnLoginClick, null, null);
        }
    }
}
