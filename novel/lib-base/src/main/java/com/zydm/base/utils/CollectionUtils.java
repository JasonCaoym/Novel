package com.zydm.base.utils;

import java.util.ArrayList;

public class CollectionUtils {

    public static <T> ArrayList<ArrayList<T>> split(ArrayList<T> resList, int count) {
        if (resList == null || resList.size() == 0 || count < 1)
            return null;
        ArrayList<ArrayList<T>> ret = new ArrayList<>();
        int size = resList.size();
        if (size <= count) {
            ret.add(resList);
            return ret;
        }

        int pre = size / count;
        int last = size % count;
        for (int i = 0; i < pre; i++) {
            ArrayList<T> itemList = new ArrayList<T>(count);
            for (int j = 0; j < count; j++) {
                itemList.add(resList.get(i * count + j));
            }
            ret.add(itemList);
        }

        if (last > 0) {
            ArrayList<T> itemList = new ArrayList<T>(count);
            for (int i = 0; i < last; i++) {
                itemList.add(resList.get(pre * count + i));
            }
            ret.add(itemList);
        }

        return ret;

    }
}
