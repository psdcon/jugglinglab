package com.jonglen7.jugglinglab.util;

import android.graphics.Color;

public class ColorConverter {

    public float[] hex2rgba(int hexColor) {
        float alpha = ((float) Color.alpha(hexColor)) / 255.0f;
        float red = ((float) Color.red(hexColor)) / 255.0f;
        float green = ((float) Color.green(hexColor)) / 255.0f;
        float blue = ((float) Color.blue(hexColor)) / 255.0f;
        return new float[]{red, green, blue, alpha};
    }

}
