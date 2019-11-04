package com.example.tdeecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

// Todo: Change Widget variable names to weight_label, weight_value, weight_unit usw.

// Extending AppCompatActivity

public class TDEECalculator extends AppCompatActivity {
    // Todo: Save and get string values from xml file
    private Person person;
    private EditText etPal;
    private TextView tvBmi;
    private TextView tvBmr;
    private TextView tvTdee;
    private TextView tvWeightUnit;
    private NumberPicker npWeight;
    private NumberPicker npHeight;
    private NumberPicker npAge;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private MyPreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load preferences and configurate widgets
        person = new Person();
        configurateWidgets();
    }

    /*
     * SUMMARY: Gets all widgets by viewid and configures preferences of Numberpickers
     * CALLED BY TDEECalculator : onCreate
     */
    private void configurateWidgets() {
        // Initialisation of input-handling variables
        etPal = (EditText) findViewById(R.id.et_pal_input);
        tvBmi = (TextView) findViewById(R.id.tv_bmi_result);
        tvBmr = (TextView) findViewById(R.id.tv_bmr_result);
        tvTdee = (TextView) findViewById(R.id.tv_tdee_result);
        tvWeightUnit = (TextView) findViewById(R.id.tv_weight_unit);
        rbMale = (RadioButton) findViewById((R.id.rb_male));
        rbFemale = (RadioButton) findViewById((R.id.rb_female));

        // Load Settings from DefaultSharedPreferences
        prefManager = new MyPreferenceManager(this);
        prefManager.getPreferencesWithDefaultValues(person);
        prefManager.printAllPreferences();

        // Set gender selection according to preference settings
        if (person.gender == Constant.GENDER_FEMALE) {
            rbFemale.setChecked(true);
        }
        else rbMale.setChecked(true);

        // Set measurement system according to preference settings
        if (person.measurementType == Constant.MEASUREMENT_METRIC) {
            tvWeightUnit.setText("(" + getString(R.string.unit_metric_kg) + ")");
        } else if (person.measurementType == Constant.MEASUREMENT_IMPERIAL) {
            tvWeightUnit.setText("(" + getString(R.string.unit_imperial_lb) + ")");
        }

        // Configure number picker for weight input
        npWeight = (NumberPicker) findViewById(R.id.np_weight);
        npWeight.setMaxValue(Integer.parseInt(getString(R.string.weight_max_value)));
        npWeight.setMinValue(Integer.parseInt(getString(R.string.weight_min_value)));
        npWeight.setValue((int)Math.round(person.weight));
        npWeight.setWrapSelectorWheel(false);
        //npWeight.setTextSize(20); does not work atm

        // Configure number picker for height input
        npHeight = (NumberPicker) findViewById(R.id.np_height);
        npHeight.setMaxValue(Integer.parseInt(getString(R.string.height_max_value)));
        npHeight.setMinValue(Integer.parseInt(getString(R.string.height_min_value)));
        npHeight.setValue((int)Math.round(person.height));
        npHeight.setWrapSelectorWheel(false);
        //npHeight.setTextSize(20); does not work atm

        // Configure number picker for age input
        npAge = (NumberPicker) findViewById(R.id.np_age);
        npAge.setMaxValue(Integer.parseInt(getString(R.string.age_max_value)));
        npAge.setMinValue(Integer.parseInt(getString(R.string.age_min_value)));
        npAge.setValue(person.age);
        npAge.setWrapSelectorWheel(false);
    }

    // Calculate Total Daily Energie Expenditure (TDEE) invoked by clicking of Button 'fab_calculate' in content_main
    public void onClickFabCalculate(View view)
    {
        // Ensure that preference changes in the current app session are used for the calculation
        prefManager.getPreferencesNoDefaultValues(person);

        // Get user input
        person.age = npAge.getValue();
        person.weight = npWeight.getValue();
        person.height = npHeight.getValue();
        person.pal = Double.parseDouble(etPal.getText().toString());

        // Calculate and display body mass index
        person.calculateBmi();
        tvBmi.setText(String.format(Locale.getDefault(), "%1.1f", person.bmi));
        //tvBmi.setVisibility(View.VISIBLE);

        // Check and set gender selection for following calculations
        if (rbFemale.isChecked()) {
            person.gender = Constant.GENDER_FEMALE;
        } else if (rbMale.isChecked()) {
            person.gender = Constant.GENDER_MALE;
        }

        person.calculateTdee();

        // Check for preferred display unit
        if (person.energyUnit == Constant.UNIT_KCAL) {
            // Display as kcal
            tvBmr.setText(String.format(Locale.getDefault(), "%1.0f kcal ", person.bmr));
            tvTdee.setText(String.format(Locale.getDefault(), "%1.0f kcal", person.tdee));
        } else if (person.energyUnit == Constant.UNIT_MJ) {
            // Display as MJ
            tvBmr.setText(String.format(Locale.getDefault(), "%1.2f MJ", person.bmr));
            tvTdee.setText(String.format(Locale.getDefault(), "%1.2f MJ", person.tdee));
        }
    }

    // Send calculation results by email
    public void sendResultsByEMail() {
        String msgGender = "";
        String msgFormula = "";
        String msgUnitHeight = "";
        String msgUnitWeight = "";
        String msgBmr = "";
        String msgTdee = "";

        // Create an intent which only shows email apps
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

        // Build string for gender
        String[] sArray = getResources().getStringArray(R.array.pref_gender_entries);
        if (person.gender == Constant.GENDER_FEMALE)
            msgGender = sArray[Constant.GENDER_FEMALE];
        else if (person.gender == Constant.GENDER_MALE)
            msgGender = sArray[Constant.GENDER_MALE];

        // Build string for bmr formula
        sArray = getResources().getStringArray(R.array.pref_formula_entries);

        switch (person.bmrFormula) {
            case Constant.BMR_HARRIS_AND_BENEDICT:
                msgFormula = sArray[Constant.BMR_HARRIS_AND_BENEDICT];
                break;
            case Constant.BMR_HENRY:
                msgFormula = sArray[Constant.BMR_HENRY];
                break;
            case Constant.BMR_MIFFLIN:
                msgFormula = sArray[Constant.BMR_MIFFLIN];
                break;
            case Constant.BMR_MUELLER:
                msgFormula = sArray[Constant.BMR_MUELLER];
                break;
            case Constant.BMR_SCHOFIELD:
                msgFormula = sArray[Constant.BMR_SCHOFIELD];
                break;
        }

        // Build string for weight and height units
        if (person.measurementType == Constant.MEASUREMENT_METRIC) {
            msgUnitWeight = getString(R.string.unit_metric_m);
            msgUnitHeight = getString(R.string.unit_metric_cm);
        }
        if (person.measurementType == Constant.MEASUREMENT_IMPERIAL){
            msgUnitWeight = getString(R.string.unit_imperial_lb);
            msgUnitHeight = getString(R.string.unit_imperial_inches);
        }

        // Build strings for bmr and tdee and add selected energy unit
        sArray = getResources().getStringArray(R.array.pref_energy_unit_entries);

        if (person.energyUnit == Constant.UNIT_KCAL) {
            msgBmr = String.format(Locale.getDefault(), "%1.0f", person.bmr) +
                    " " + sArray[Constant.UNIT_KCAL];
            msgTdee = String.format(Locale.getDefault(), "%1.0f", person.tdee) +
                    " " + sArray[Constant.UNIT_KCAL];
        }
        else if (person.energyUnit == Constant.UNIT_MJ) {
            msgBmr = String.format(Locale.getDefault(), "%1.2f", person.bmr) +
                    " " + sArray[Constant.UNIT_MJ];
            msgTdee = String.format(Locale.getDefault(), "%1.2f", person.tdee) +
                    " " + sArray[Constant.UNIT_MJ];
        }

        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<html><body><h1>TDEECalculator</h1>")
                        .append("<h2>Settings</h2>")
                        .append("<p>Weight: " + person.weight + " " + msgUnitWeight + "<br>")
                        .append("Height: " + person.height + " " + msgUnitHeight + "<br>")
                        .append("Age: " + person.age + "<br>")
                        .append("Gender: " + msgGender + "<br></p>")
                        .append("<h2>Calculation</h2>")
                        .append("BMI: <br>")
                        .append(String.format(Locale.getDefault(), "%1.1f kg/m²", person.bmi) + "<br>")
                        .append("<p>Basal metabolic rate: <br>")
                        .append(msgBmr + "<br>")
                        .append("calculated with " + msgFormula + "<br>")
                        .append("<p>Physical activity level: <br>")
                        .append(person.pal + "<br>")
                        .append("<p>Total daily energy expenditure: <br>")
                        .append(msgTdee + "<br></p>")
                        .append("</body></html>")
                        .toString())
        );

        startActivity(Intent.createChooser(emailIntent, getString(R.string.title_intent_email)));
    }

    // Displays Toast
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            // Launch settings activity
            startActivity(new Intent(this, MainSettings.class));
            return true;
        }
        else if (item.getItemId() == R.id.action_send_email) {
            // Check if a calculation has been done
            if (person.tdee > 0) sendResultsByEMail();
            else showToast(getString(R.string.msg_menu_email_warning));
        }
        return super.onOptionsItemSelected(item);
    }


}
