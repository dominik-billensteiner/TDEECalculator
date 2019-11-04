package com.example.tdeecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;


public class MyPreferenceManager {
    protected SharedPreferences sp;
    protected Context appContext;


    public MyPreferenceManager(Context context) {
        this.appContext = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    /* Gets all preferences from the settingsmenu */
    public Person getPreferencesWithDefaultValues(Person person) {
        person.weight = getPrefWeight();
        person.height = getPrefHeight();
        person.age = getPrefAge();
        person.gender = getPrefGender();
        person.measurementType = getPrefMeasurementType();
        person.energyUnit = getPrefEnergyUnit();
        person.bmrFormula = getPrefBmrFormula();
        person.pal = getPrefPal();
        return person;
    }

    /* Return preferences without the category default-value */
    public Person getPreferencesNoDefaultValues(Person person) {
        person.measurementType = getPrefMeasurementType();
        person.energyUnit = getPrefEnergyUnit();
        person.bmrFormula = getPrefBmrFormula();
        return person;
    }

    public Double getPrefWeight() {
        return Double.parseDouble(sp.getString("weight_settings", ""));
    }

    public Double getPrefHeight() {
        return Double.parseDouble(sp.getString("height_settings", ""));
    }

    public int getPrefAge() {
        return Integer.parseInt(sp.getString("age_settings", ""));
    }

    public int getPrefGender() {
        return Integer.parseInt(sp.getString("gender_settings", ""));
    }

    public @Constant.MeasurementType
    int getPrefMeasurementType() {
        return Integer.parseInt(sp.getString("measurement_settings", ""));
    }

    public @Constant.EnergyUnit
    int getPrefEnergyUnit() {
        return Integer.parseInt(sp.getString("energy_unit_settings", ""));
    }

    public @Constant.BmrFormula
    int getPrefBmrFormula() {
        return Integer.parseInt(sp.getString("formula_settings", ""));
    }

    public Double getPrefPal() {
        return Double.parseDouble(sp.getString("pal_settings", ""));
    }

    public void printAllPreferences() {
        Log.d("TDEECalculator_LOG", sp.getAll().toString());
    }
}
