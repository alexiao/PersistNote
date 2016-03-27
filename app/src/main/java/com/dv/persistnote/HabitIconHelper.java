package com.dv.persistnote;

import android.graphics.drawable.Drawable;

import com.dv.persistnote.base.ResTools;
import com.dv.persistnote.business.HabitConstDef;

/**
 * Created by Hang on 2016/3/23.
 */
public class HabitIconHelper {

    public static Drawable getHabitIcon(int habitId) {
        Drawable drawable = null;
        switch (habitId) {
            case HabitConstDef.ID_RUNNING:
                drawable = ResTools.getDrawable(R.drawable.habit_running);
                break;
            case HabitConstDef.ID_BREAKFAST:
                drawable = ResTools.getDrawable(R.drawable.habit_breakfast);
                break;
            case HabitConstDef.ID_PAINTING:
                drawable = ResTools.getDrawable(R.drawable.habit_drawing);
                break;
        }
        return drawable;
    }
}
