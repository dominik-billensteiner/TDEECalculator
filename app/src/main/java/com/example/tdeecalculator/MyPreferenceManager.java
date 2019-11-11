package com.example.tdeecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;


public class MyPreferenceManager {
    private SharedPreferences sp;
    private Context context;
    private EnergyReqCalc erCalc;

    /*
     * Default Constructor
     */
    public MyPreferenceManager() {
        sp = null;
        context = null;
        erCalc = null;
    }

    /*
     * Constructor with app context loads default shared preferences
     */
    public MyPreferenceManager(Context context) {
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        erCalc = new EnergyReqCalc();
    }

    /*
     * Gets all preferences from the settingsmenu
     */
    public EnergyReqCalc getPreferencesWithDefaultValues() {
        erCalc.measurementType = getPrefMeasurementType();
        erCalc.weight = getPrefWeight(erCalc.measurementType);
        erCalc.height = getPrefHeight(erCalc.measurementType);
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
    public EnergyReqCalc getPreferencesNoDefaultValues(EnergyReqCalc givenErc) {
        givenErc.measurementType = getPrefMeasurementType();
        givenErc.energyUnit = getPrefEnergyUnit();
        givenErc.bmrFormula = getPrefBmrFormula();
        return givenErc;
    }

    /*
     * Returns measurement system (int) saved in preferences
     */
    public @Constant.MeasurementType int getPrefMeasurementType() {
        return Integer.parseInt(sp.getString(
                context.getString(R.string.key_measurement),
                context.getString(R.string.default_measurement))
        );
    }

    /*
     * Returns default weight (double) saved in preferences
     */
    private Double getPrefWeight(@Constant.MeasurementType int measurementType) {
        if (measurementType == Constant.MEASUREMENT_IMPERIAL) {
            return Double.parseDouble(sp.getString(context.getString(R.string.key_weight_imperial),
                    context.getString(R.string.default_weight_imperial)));
        } else return Double.parseDouble(sp.getString(context.getString(R.string.key_weight_metric),
                context.getString(R.string.default_weight_metric)));
    }

    /*
     * Returns default height saved in preferences
     */
    private Double getPrefHeight(@Constant.MeasurementType int measurementType) {
        if (measurementType == Constant.MEASUREMENT_IMPERIAL) {
            Double ft = Double.parseDouble(sp.getString(context.getString(R.string.key_height_imperial_ft),
                    context.getString(R.string.default_height_imperial_ft)));
            Double in = Double.parseDouble(sp.getString(context.getString(R.string.key_height_imperial_in),
                    context.getString(R.string.default_height_imperial_in)));
            ImperialHeight iHeight = new ImperialHeight(ft, in);
            return iHeight.getMetricHeight();
        }
        else return Double.parseDouble(sp.getString(context.getString(R.string.key_weight_metric),
                context.getString(R.string.default_weight_metric)));
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
