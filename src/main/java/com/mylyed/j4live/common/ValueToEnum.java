/*
 * Flazr <http://flazr.com> Copyright (C) 2009  Peter Thomas.
 *
 * This file is part of Flazr.
 *
 * Flazr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flazr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flazr.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mylyed.j4live.common;

import java.util.Arrays;


/**
 * 定义 value为int值的枚举的接口
 *
 * @param <T>
 */
public class ValueToEnum<T extends Enum<T> & ValueToEnum.NumberValue> {

    public interface NumberValue {
        Number value();
    }

    //用空间换时间
    private final Enum[] storage;
    //value最大值
    private final Number maxIndex;

    public ValueToEnum(final T[] enumValues) {
        final Number[] intValues = new Number[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            intValues[i] = enumValues[i].value();
        }
        Arrays.sort(intValues);
        maxIndex = intValues[intValues.length - 1];
        storage = new Enum[maxIndex.intValue() + 1];
        for (final T t : enumValues) {
            storage[t.value().intValue()] = t;
        }
    }

    @SuppressWarnings("unchecked")//抑制转换警告
    public T valueToEnum(final Number i) {
        final T t;
        try {
            t = (T) storage[i.intValue()];
        } catch (Exception e) { // index out of bounds
            throw new RuntimeException(getErrorLogMessage(i) + ", " + e);
        }
        if (t == null) {
            throw new RuntimeException(getErrorLogMessage(i) + ", 不匹配");
        }
        return t;
    }

    /**
     * 获取枚举最大intValue
     *
     * @return
     */
    public Number getMaxIndex() {
        return maxIndex;
    }

    private String getErrorLogMessage(final Number i) {
        return "获取枚举失败，intValue：" + i.intValue();
    }

}
