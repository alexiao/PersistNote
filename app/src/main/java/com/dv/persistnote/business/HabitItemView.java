package com.dv.persistnote.business;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dv.persistnote.FakeDataHelper;
import com.dv.persistnote.HabitIconHelper;
import com.dv.persistnote.R;
import com.dv.persistnote.base.ResTools;
import com.dv.persistnote.base.network.bean.Result;
import com.dv.persistnote.business.account.AccountModel;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hang on 2016/3/23.
 */
public class HabitItemView extends RelativeLayout implements View.OnClickListener{

    private final static int ID_ICON = 101;
    private final static int ID_TITLE = 102;

    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubTitle;
    private ImageView mCheckIcon;

    public HabitItemView(Context context) {
        super(context);

        LayoutParams lp = new LayoutParams(ResTools.getDimenInt(R.dimen.habit_icon_width), ResTools.getDimenInt(R.dimen.habit_icon_width));
        mIcon = new ImageView(getContext());
        mIcon.setId(ID_ICON);
        lp.addRule(ALIGN_PARENT_LEFT);
        lp.addRule(CENTER_VERTICAL);
        lp.rightMargin = 10;
        addView(mIcon, lp);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTitle = new TextView(getContext());
        mTitle.setId(ID_TITLE);
        mTitle.setTextColor(ResTools.getColor(R.color.default_black));
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResTools.getDimenInt(R.dimen.common_text_size_16));
        lp.addRule(RIGHT_OF, ID_ICON);
        lp.topMargin = ResTools.getDimenInt(R.dimen.common_text_size_22);
        addView(mTitle, lp);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mSubTitle = new TextView(getContext());
        lp.addRule(RIGHT_OF, ID_ICON);
        lp.addRule(BELOW, ID_TITLE);
        addView(mSubTitle, lp);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mCheckIcon = new ImageView(getContext());
        lp.addRule(ALIGN_PARENT_RIGHT);
        lp.addRule(CENTER_VERTICAL);
        addView(mCheckIcon, lp);

        setBackgroundColor(ResTools.getColor(R.color.default_white));
        int padding = ResTools.getDimenInt(R.dimen.common_margin_16);
        setPadding(padding, 0, padding, 0);

        setOnClickListener(this);
    }

    public void setChecked(boolean checked) {

    }

    public void setData(Drawable drawable, String title, String subTitle) {
        mIcon.setImageDrawable(drawable);
        mTitle.setText(title);
        mSubTitle.setText(subTitle);
    }

    public void bindData(FakeDataHelper.HabitInfo info) {
        mIcon.setImageDrawable(HabitIconHelper.getHabitIcon(info.mHabitId));
        mTitle.setText(info.mHabitName);
        mSubTitle.setText("已经坚持"+info.mCount+"天");
    }

    @Override
    public void onClick(View view) {
        AccountModel.getInstance().testPostRequest("NetworkTest",
                new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {
                mSubTitle.setText(result.getRetData().getTransResult().get(0).getDst());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
