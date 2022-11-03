package com.example.manoj.hyveg_observation.Others;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int min, max;
    private float f_min, f_max;
    String type;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max ,String type) {
        this.type = type;
        if (type.equals("int")) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }else {
            this.f_min = Float.parseFloat(min);
            this.f_max = Float.parseFloat(max);
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Remove the string out of destination that is to be replaced
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            if (type.equals("int")) {
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input))
                    return null;
            }else {
                float input = Float.parseFloat(newVal);
                if (isInRange1(f_min, f_max, input))
                    return null;
            }
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    private boolean isInRange1(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
