package com.maxim.widgets.testdatafactories;

import com.maxim.widgets.models.Widget;

public class WidgetTestDataFactory {
    public static Widget getValid() {
        return new Widget(50, 50, 1, 100, 100);
    }
    public static Widget getValidZIndexLess() {
        return new Widget(50, 100, -9, 100, 100);
    }

    public static Widget getValidZIndexHundredXY() {
        return new Widget(100, 100, -9, 100, 100);
    }
}
