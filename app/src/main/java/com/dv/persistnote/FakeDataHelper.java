package com.dv.persistnote;

import com.dv.persistnote.business.HabitConstDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hang on 2016/3/23.
 */
public class FakeDataHelper {

    public static class HabitInfo {
        public int mHabitId;
        public String mHabitName;
        public int mCount;
        public boolean mChecked;

        public HabitInfo(int id, String habit, int count, boolean check) {
            mHabitId = id;
            mHabitName = habit;
            mCount = count;
            mChecked = check;
        }
    }

    public static List<HabitInfo> getMyHabitInfos() {
        List list = new ArrayList();
        list.add(new HabitInfo(HabitConstDef.ID_BREAKFAST, "吃早餐", 23, true));
        list.add(new HabitInfo(HabitConstDef.ID_RUNNING,"跑步", 16, true));
        list.add(new HabitInfo(HabitConstDef.ID_PAINTING,"画画", 12, false));

        return list;
    }

}
