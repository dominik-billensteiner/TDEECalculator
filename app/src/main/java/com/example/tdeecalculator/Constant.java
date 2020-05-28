package com.example.tdeecalculator;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// https://stackoverflow.com/questions/35625247/android-is-it-ok-to-put-intdef-values-inside-interface

public class Constant {

    // United States, Myanmar and Liberia you the imperial measurement system
    // This constant holds the ISO3166-1 code for preference purposes
    public static final String[] COUNTRIES_WITH_IMPERIAL_MEASUREMENT = {"US", "MM", "LR"};

    // Conversion value to calculate cm to inches and visa versa
    public static final Double INCH_IN_CM = 2.54;

    // Conversion value to calculate cm to feet and visa versa
    public static final Double FEET_IN_CM = 30.48;

    // Conversion value to calculate pund to kg and visa versa
    public static final Double POUND_TO_KG = 2.2046;

    // Data type definition for gender
    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;

    @IntDef(value = {GENDER_FEMALE, GENDER_MALE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gender {
    }

    // Data type definition for the measurement system
    public static final int MEASUREMENT_METRIC = 0;
    public static final int MEASUREMENT_IMPERIAL = 1;

    @IntDef(value = {MEASUREMENT_METRIC, MEASUREMENT_IMPERIAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeasurementType {
    }

    // Data type definition for energy unit
    public static final int UNIT_KCAL = 0;
    public static final int UNIT_MJ = 1;

    @IntDef(value = {UNIT_KCAL, UNIT_MJ})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EnergyUnit {
    }

    // Data type definition for basal metabolic rate formula
    public static final int BMR_HARRIS_AND_BENEDICT = 0;
    public static final int BMR_HENRY = 1;
    public static final int BMR_MIFFLIN = 2;
    public static final int BMR_MUELLER = 3;
    public static final int BMR_SCHOFIELD = 4;

    @IntDef(value = {BMR_HARRIS_AND_BENEDICT, BMR_HENRY,
            BMR_MIFFLIN, BMR_MUELLER, BMR_SCHOFIELD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BmrFormula {

    }
}
