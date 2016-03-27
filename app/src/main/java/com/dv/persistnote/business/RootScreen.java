package com.dv.persistnote.business;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dv.persistnote.FakeDataHelper;
import com.dv.persistnote.FakeDataHelper.HabitInfo;
import com.dv.persistnote.R;
import com.dv.persistnote.base.ResTools;
import com.dv.persistnote.framework.DefaultScreen;
import com.dv.persistnote.framework.ui.UICallBacks;

import java.util.List;

/**
 * Created by Hang on 2016/3/13.
*/
public class RootScreen extends DefaultScreen {

    private LinearLayout mContainer;

    public RootScreen(Context context, UICallBacks callBacks) {
        super(context, callBacks);
        init();
        setBackgroundColor(ResTools.getColor(R.color.default_grey));
        setTitle(ResTools.getString(R.string.app_name));
    }

    private void init() {
        mContainer = new LinearLayout(getContext());
        mContainer.setOrientation(LinearLayout.VERTICAL);
        setContent(mContainer);


        updateViews();
    }

    private void updateViews() {
        mContainer.removeAllViews();

        List<HabitInfo> habitInfos = FakeDataHelper.getMyHabitInfos();
        for (HabitInfo info : habitInfos) {
            HabitItemView itemView = new HabitItemView(getContext());
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ResTools.getDimenInt(R.dimen.habit_item_height));
            lp.topMargin = ResTools.getDimenInt(R.dimen.habit_item_margin_top);

            itemView.bindData(info);
            mContainer.addView(itemView, lp);
        }

    }


    public void setCheckInText(String checkInText) {
    }


}
