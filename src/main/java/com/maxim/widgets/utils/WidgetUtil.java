package com.maxim.widgets.utils;

public class WidgetUtil {
    public static Boolean isNotNullOrZeroAttributes(Integer x, Integer y, Integer width, Integer height) {
        return x != null && y != null && x != 0 && y !=0  && width != null && height != null && width != 0 && height !=0;
    }
}
