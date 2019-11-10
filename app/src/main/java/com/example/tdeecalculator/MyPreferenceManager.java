package com.example.tdeecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;


public class MyPreferenceManager {
    private SharedPreferences sp;
    private Context context;

    /*
     * Default Constructor
     */
    public MyPreferenceManager() {
        sp = null;
        context = null;
    }

    /*
     * Constructor with app context loads default shared preferences
     */
    public MyPreferenceManager(Context context) {
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*
     * Gets all preferences from the settingsmenu
     */
    public EnergyReqCalc getPreferencesWithDefaultValues() {
        EnergyReqCalc erCalc = new EnergyReqCalc();
        erCalc.measurementType = getPrefMeasurementType();
        erCalc.weight = getPrefWeight(erCalc.measurementType);
        erCalc.height = getPrefHeight();
        erCalc.age = getPrefAge();
        erCalc.gender = getPrefGender();
        erCalc.energyUnit = getPrefEnergyUnit();
        erCalc.bmrFormula = getPrefBmrFormula();
        erCalc.pal = getPrefPal();
        return erCalc;
    }

    /*
     * Returns preferences for measurement system, energy unit and bmr formula
     */
    public EnergyReqCalc getPreferencesNoDefaultValues(EnergyReqCalc erCalc) {
        erCalc.measurementType = getPrefMeasurementType();
        erCalc.energyUnit = getPrefEnergyUnit();
        erCalc.bmrFormula = getPrefBmrFormula();
        return erCalc;
    }

    /*
     * Returns measurement system saved in preferences
     */
    public @Constant.MeasurementType int getPrefMeasurementType() {
        return Integer.parseInt(sp.getString(
                context.getString(R.string.key_measurement),
                context.getString(R.string.default_measurement))
        );
    }
        /* NOT NECCESARY TO CHECK????
         Check if app is called the first time/or preferences are not set
        if (mPref == "") {
            // Firstrun of the app
            String locale = context.getResources().getConfiguration().locale.getCountry();
            SharedPreferences.Editor editor =  sp.edit();
            // Check if the country set in the phones current preferences uses imperial system
            if (checkLocaleForImperialMeasurement(locale)) {
                // Set default weight and default height to imperial system
                editor.putInt(context.getString(R.string.key_weight),
                        Integer.parseInt(context.getString(R.string.default_weight_imperial)));
                editor.putInt(context.getString(R.string.key_height),
                        Integer.parseInt(context.getString(R.string.default_height)));
                editor.commit();
                return Constant.MEASUREMENT_IMPERIAL;
            } else {
                // set dfeault weight and default height to metric system
                editor.putInt(context.getString(R.string.key_weight),
                        Integer.parseInt(context.getString(R.string.default_weight_metric)));
                editor.putString(context.getString(R.string.key_height),
                        context.getString(R.string.default_height));
                editor.commit();*/

    /*
     * Returns default weight saved in preferences
     */
    private Double getPrefWeight(@Constant.MeasurementType int measurementType) {
        if (measurementType == Constant.MEASUREMENT_IMPERIAL) {
            return Double.parseDouble(sp.getString(context.getString(R.string.key_weight_imperial),
                    context.getString(R.string.default_weight_imperial)));
        }
        else return Double.parseDouble(sp.getString(context.getString(R.string.key_weight_metric),
                context.getString(R.string.default_weight_metric)));

        /* FOR BUGGY VERSION WITH CANNOT CAST INT TO STRING
        if (measurementType == Constant.MEASUREMENT_IMPERIAL) {
            return new Double(sp.getInt(context.getString(R.string.key_weight),
                            Integer.parseInt(context.getString(R.string.default_weight_imperial))));
        }
        else return new Double(sp.getInt(context.getString(R.string.key_weight),
                            Integer.parseInt(context.getString(R.string.default_weight_metric)))
        );*/
    }
            /* Working block
            int value = sp.getInt(context.getString(R.string.key_weight),
                    Integer.parseInt(context.getString(R.string.default_weight_metric)));
            Double dValue = Double.parseDouble(String.format("%d", value));
            return dValue; */
            /* Remnants of Integer cannot be cast to java.lang.string
                      /*
            Double value = sp.getString(context.getString("weight_settings",
                    context.getString(R.string.default_weight_metric));*/
                    /*
            int value = sp.getInt(context.getString(R.string.key_weight),
                    Integer.parseInt(context.getString(R.string.default_weight_metric)));
            Double dValue = Double.parseDouble(String.format("%d", value));
            return dValue;
            /*
            return Double.parseDouble(
                    sp.getString(context.getResources().getString(R.string.key_weight).toString(),
                            (context.getResources().getString(R.string.default_weight_metric)).toString()));*
             */


    /*
     * Returns default height saved in preferences
     */
    private Double getPrefHeight() {
        return 170.0;
        /*return new Double(sp.getInt(context.getString(R.string.key_height),
                        Integer.parseInt(context.getString(R.string.default_height)))
                );*/
    }

    /*
     * Returns default age saved in preferences
     */
    private int getPrefAge() {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_age),
                context.getString(R.string.default_age)));
    }

    private int getPrefGender() {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_gender),
                String.valueOf(Constant.GENDER_FEMALE)));
    }

    private @Constant.EnergyUnit
    int getPrefEnergyUnit() {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_energy_unit),
                context.getString(R.string.default_energy_unit)));
    }

    private @Constant.BmrFormula
    int getPrefBmrFormula() {
        return Integer.parseInt(sp.getString(context.getString(R.string.key_formula),
                context.getString(R.string.default_formula)));
    }

    private Double getPrefPal() {
        return Double.parseDouble(sp.getString(context.getString(R.string.key_pal),
                context.getString(R.string.default_pal)));
    }

    // Checks if country set as location on phone uses imperial measurement
    private boolean checkLocaleForImperialMeasurement(String locale) {
        for (int i = 0; i < Constant.COUNTRIES_WITH_IMPERIAL_MEASUREMENT.length; i++) {
            if (locale.equals(Constant.COUNTRIES_WITH_IMPERIAL_MEASUREMENT[i]))
                return true;
        }
        return false;
    }

    public void printAllPreferences() {
        Log.d("TDEECalculator_LOG", sp.getAll().toString());
    }
}
