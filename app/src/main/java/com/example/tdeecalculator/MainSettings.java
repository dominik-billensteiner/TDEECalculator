package com.example.tdeecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import java.util.Map;

public class MainSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        initActionBar();
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the back button in the top left corner of settings is pressed, the activity closes
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        SharedPreferences sp;
        MyPreferenceManager prefManager;
        Context context;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            // set application context
            context = TDEECalculator.getAppContext();
            // set input type for EditTextPreferences to number decimal
            setInputTypeToDecimal(getString(R.string.key_pal));
            setInputTypeToDecimal(getString(R.string.key_age));
            setInputTypeToDecimal(getString(R.string.key_weight_metric));
            setInputTypeToDecimal(getString(R.string.key_weight_imperial));
            setInputTypeToDecimal(getString(R.string.key_height_metric));
            // setup preferences according to measurement system
            prefManager = new MyPreferenceManager(context);
            @Constant.MeasurementType int measurementType = prefManager.getPrefMeasurementType();
            showWeightHeightPreferences(measurementType);
        }

        /*
         * Displays weight and height preferences for preferred measurement system
         */
        private void showWeightHeightPreferences(@Constant.MeasurementType int measurementType) {
            EditTextPreference weightMetricPref = getPreferenceManager().findPreference(context.getString(R.string.key_weight_metric));
            EditTextPreference heightMetricPref = getPreferenceManager().findPreference(context.getString(R.string.key_height_metric));
            EditTextPreference weightImperialPref = getPreferenceManager().findPreference(context.getString(R.string.key_weight_imperial));
            EditTextPreference heightImperialFeetPref = getPreferenceManager().findPreference(context.getString(R.string.key_height_imperial_ft));
            EditTextPreference heightImperialInchesPref = getPreferenceManager().findPreference(context.getString(R.string.key_height_imperial_in));

            if (measurementType == Constant.MEASUREMENT_METRIC)
            {
                Log.d("tdeecalc_log", "measurement changed to metric");
                weightMetricPref.setVisible(true);
                heightMetricPref.setVisible(true);
                weightImperialPref.setVisible(false);
                heightImperialFeetPref.setVisible(false);
                heightImperialInchesPref.setVisible(false);
            }
            else if (measurementType == Constant.MEASUREMENT_IMPERIAL)
            {
                Log.d("tdeecalc_log", "measurement changed to imperial");
                weightImperialPref.setVisible(true);
                heightImperialFeetPref.setVisible(true);
                heightImperialInchesPref.setVisible(true);
                weightMetricPref.setVisible(false);
                heightMetricPref.setVisible(false);
            }
        }

        /*
         * Listens for measurement type changes in Settings to reconfigure preferences of weight
         * and height.
         */
        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            this.sp = PreferenceManager.getDefaultSharedPreferences(TDEECalculator.getAppContext());
            sp.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            this.sp = PreferenceManager.getDefaultSharedPreferences(TDEECalculator.getAppContext());
            sp.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sp, String key)
        {
            // check if the settings of measurement system has been changed
            if (key.equals(context.getString(R.string.key_measurement)))
            {
                // get the value of changed setting key
                @Constant.MeasurementType int measurementType = prefManager.getPrefMeasurementType();
                showWeightHeightPreferences(measurementType);
            }
        }

        private void setInputTypeToDecimal(String key) {
            EditTextPreference etPref = getPreferenceManager().findPreference(key);
            etPref.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            });
        }
    }
}