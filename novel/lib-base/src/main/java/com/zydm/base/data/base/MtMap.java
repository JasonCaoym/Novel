package com.zydm.base.data.base;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;

/**
 * Created by yan on 16-10-22.
 */

public class MtMap<K, V> extends ArrayMap<K, V> {
    public MtMap() {
    }

    public MtMap(int capacity) {
        super(capacity);
    }

    public MtMap(SimpleArrayMap map) {
        super(map);
    }

    @Override
    public MtMap<K, V> clone() {
        return new MtMap<>(this);
    }

    public void putAllArrayMap(SimpleArrayMap<? extends K, ? extends V> array) {
        super.putAll(array);
    }

}
