package com.mewu.plazastar.utils;

import java.text.DecimalFormat;

public class Numbers {

    public static String HumanizeNumber(long number){

        DecimalFormat formatter = new DecimalFormat("##.00");
        if (number < 1000){
            return number + "";
        }
        if (number < 1000000){
            return formatter.format(number / 1000f) + "K";
        }
        if (number < 1000000000){
            return formatter.format(number / 1000000f) + "M";
        }
        if (number < 1000000000000L){
            return formatter.format(number / 1000000000f) + "B";
        }
        if (number < 1000000000000000L){
            return formatter.format(number / 1000000000000L) + "T";
        }

        return number + "";
    }
}
