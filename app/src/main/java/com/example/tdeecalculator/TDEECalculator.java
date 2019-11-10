package com.example.tdeecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

// TEST GITHUB COMMIT AND PUBLISH

// Todo: Change Widget variable names to weight_label, weight_value, weight_unit usw.

// Extending AppCompatActivity

public class TDEECalculator extends AppCompatActivity {
    // holds static app context, used globally through get getAppContext()
    private static Context context;

    // variables used for application
    private EnergyReqCalc erCalc;
    private EditText etPal;
    private TextView tvBmi;
    private TextView tvBmr;
    private TextView tvTdee;
    private TextView tvWeightUnit;
    private TextView tvHeightUnit;
    private NumberPicker npWeight;
    private NumberPicker npHeight;
    private NumberPicker npAge;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private MyPreferenceManager prefManager;

    /* Changes to true if the user clicks on Settings in the Options Menu of the Main app. Used for
     *  1) determining the origin in method @Override onResume, so if the resume is coming from
     *     Settings, then certain preference option are beeing checked for changes, and invoke actions
     *     corresponding to those changes.
     */
    private boolean resumeFromSettings = false;

    /* Changes to true if the user performs a calculation. It is used to
    *   1) prevent an autocalculation, when the preferences of bmr formula is changed
    *   2) prevent displaying 0 kcal for tdee and bmr, when the energy unit is changed
     */
    private boolean calculationDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        setContentView(R.layout.activity_main);

        // Set toolbar menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load preferences and configurate widgets
        configurateWidgets();
    }

    /*
     * onResume the widgets are reset with the preference settings
     */
    @Override
    protected void onResume() {
        super.onResume();
        /* Does not work, although settings change, preverCalc and this.erCalc a re the same
        EnergyReqCalc preverCalc = this.erCalc;
        erCalc = prefManager.getPreferencesNoDefaultValues(erCalc);
        prefManager.printAllPreferences(); */

        /* Does work without flag
        EnergyReqCalc newerCalc = new EnergyReqCalc();
        newerCalc = prefManager.getPreferencesNoDefaultValues(newerCalc);
        prefManager.printAllPreferences(); */

        // resume from settings, check for changed preferences
        if (resumeFromSettings) {
            // create new erCalc instance
            EnergyReqCalc newerCalc = new EnergyReqCalc();
            newerCalc = prefManager.getPreferencesNoDefaultValues(newerCalc);

            // check for changes in measurement system
            if (erCalc.measurementType != newerCalc.measurementType) {
                erCalc.measurementType = newerCalc.measurementType;
                if (erCalc.measurementType == Constant.MEASUREMENT_METRIC) {
                    setWeightHeightInputMetric();
                    // call method to reconfigure numberpickers to metric
                    // no recalculation needed
                }
                else if (erCalc.measurementType == Constant.MEASUREMENT_IMPERIAL) {
                    // call method to reconfigure numberpickers to imperial
                    // no recalculation needed
                    setWeightHeightInputImperial();
                }
            }

            // check for changes of the energy unit
            if (erCalc.energyUnit != newerCalc.energyUnit) {

                // assign the changed settings
                erCalc.energyUnit = newerCalc.energyUnit;

                if (erCalc.energyUnit == Constant.UNIT_KCAL) {
                    // If the new energy unit is kcal, than the values must be converted to MJ
                    erCalc.bmr = erCalc.toKcal(erCalc.bmr);
                    erCalc.tdee = erCalc.toKcal(erCalc.tdee);
                } else {
                    if (erCalc.energyUnit == Constant.UNIT_MJ) {
                        // If the new energy unit is MJ, than the values must be converted to kcal
                        erCalc.bmr = erCalc.toMj(erCalc.bmr);
                        erCalc.tdee = erCalc.toMj(erCalc.tdee);
                    }
                }
                if (calculationDone) displayResults();
            }

            // Check for changes of the bmr formula
            if (erCalc.bmrFormula != newerCalc.bmrFormula) {
                erCalc.bmrFormula = newerCalc.bmrFormula;
                erCalc.calculateTdee();
                if (calculationDone) displayResults();
            }
            resumeFromSettings = false;
        }
    }

    /*
     * SUMMARY: Sets up all widget with default/preferenced configuration
     */
    private void configurateWidgets() {
        // Initialisation of input-handling variables
        etPal = (EditText) findViewById(R.id.et_pal_input);
        tvBmi = (TextView) findViewById(R.id.tv_bmi_result);
        tvBmr = (TextView) findViewById(R.id.tv_bmr_result);
        tvTdee = (TextView) findViewById(R.id.tv_tdee_result);
        tvWeightUnit = (TextView) findViewById(R.id.tv_weight_unit);
        tvHeightUnit = (TextView) findViewById(R.id.tv_height_unit);
        rbMale = (RadioButton) findViewById((R.id.rb_male));
        rbFemale = (RadioButton) findViewById((R.id.rb_female));
        npWeight = (NumberPicker) findViewById(R.id.np_weight);
        npHeight = (NumberPicker) findViewById(R.id.np_height);
        npAge = (NumberPicker) findViewById(R.id.np_age);

        // Load Settings from DefaultSharedPreferences
        prefManager = new MyPreferenceManager(this);
        erCalc = prefManager.getPreferencesWithDefaultValues();
        //prefManager.printAllPreferences();

        // Set gender selection according to preference settings
        if (erCalc.gender == Constant.GENDER_FEMALE) {
            rbFemale.setChecked(true);
        }
        else rbMale.setChecked(true);

        // Set widgets according to measurement system selected in preferences
        if (erCalc.measurementType == Constant.MEASUREMENT_METRIC) {
            // Configure numberpickers for weight and height
            setWeightHeightInputMetric();
        } else if (erCalc.measurementType == Constant.MEASUREMENT_IMPERIAL) {
            // Configure numberpickers for weight and height
            setWeightHeightInputImperial();
            setUnitLabelsToImperial();
        }
        // Configure number picker for age input
        npAge.setMaxValue(Integer.parseInt(getString(R.string.age_max_value)));
        npAge.setMinValue(Integer.parseInt(getString(R.string.age_min_value)));
        npAge.setValue(erCalc.age);
        npAge.setWrapSelectorWheel(false);
    }

    /*
     * Configures input widgets for height and weight for metric measurement system.
     * Sets max- and min-value, as well as displayed value for numberpickers.
     */
    private void setWeightHeightInputMetric() {
        // configure weight inputs
        npWeight.setMaxValue(Integer.parseInt(getString(R.string.weight_max_value_metric)));
        npWeight.setMinValue(Integer.parseInt(getString(R.string.weight_min_value_metric)));
        npWeight.setValue((int)Math.round(erCalc.weight));
        npWeight.setWrapSelectorWheel(false);
        tvWeightUnit.setText(getString(R.string.unit_metric_kg));

        // configure height inputs
        npHeight.setMaxValue(Integer.parseInt(getString(R.string.height_max_value_metric)));
        npHeight.setMinValue(Integer.parseInt(getString(R.string.height_min_value_metric)));
        npHeight.setValue((int)Math.round(erCalc.height));
        npHeight.setWrapSelectorWheel(false);
        tvHeightUnit.setText(getString(R.string.unit_metric_cm));
    }

    /*
     * Configures input widgets for height and weight for imperial measurement system.
     * Sets max- and min-value, as well as displayed value for numberpickers.
     */
    private void setWeightHeightInputImperial() {
        // configure weight inputs
        npWeight.setMaxValue(Integer.parseInt(getString(R.string.weight_max_value_imperial)));
        npWeight.setMinValue(Integer.parseInt(getString(R.string.weight_min_value_imperial)));
        npWeight.setValue((int)Math.round(erCalc.weight));

        // configure height inputs
        final ArrayList alHeight = getImperialHeightArray();
        // create array for height selection in ft and in
        npHeight.setMaxValue(alHeight.size()-1);
        npHeight.setFormatter(new NumberPicker.Formatter() {
                                  @Override
                                  public String format (int value) {
                                      return alHeight.get(value).toString();
                                  }
        });

    }

    /*
     * Sets the displayed units of weight and height to lb and ft"inches, respectivly
     */
    private void setUnitLabelsToImperial()
    {
        tvWeightUnit.setText(getString(R.string.unit_imperial_lb));
        tvHeightUnit.setText(getString(R.string.unit_imperial_inches));
    }

    /*
     * Clicking of the Floating Action Button 'fab_calculate' in content_main invokes the
     * calculation and display of BMI, BMR and TDEE.
     */
    public void onClickFabCalculate(View view)
    {
        // Ensure that preference changes in the current app session are used for the calculation
        prefManager.getPreferencesNoDefaultValues(erCalc);

        // Get user input
        erCalc.age = npAge.getValue();
        erCalc.weight = npWeight.getValue();
        erCalc.height = npHeight.getValue();
        erCalc.pal = Double.parseDouble(etPal.getText().toString());

        // Calculate and display body mass index
        erCalc.calculateBmi();
        displayBMI();

        // Check and set gender selection for following calculations
        if (rbFemale.isChecked()) {
            erCalc.gender = Constant.GENDER_FEMALE;
        } else if (rbMale.isChecked()) {
            erCalc.gender = Constant.GENDER_MALE;
        }

        erCalc.calculateTdee();
        displayResults();
        calculationDone = true;
    }

    /*
     * Displays BMI in the corresponding TextView
     */
    private void displayBMI()
    {
        tvBmi.setText(String.format(Locale.getDefault(), "%1.1f", erCalc.bmi));
    }

    /*
     * Displays BMR and TDEE in their corresponding TextViews
     */
    private void displayResults() {
        // Check for preferred display unit
        if (erCalc.energyUnit == Constant.UNIT_KCAL) {
            // Display as kcal
            tvBmr.setText(String.format(Locale.getDefault(), "%1.0f kcal ", erCalc.bmr));
            tvTdee.setText(String.format(Locale.getDefault(), "%1.0f kcal", erCalc.tdee));
        } else if (erCalc.energyUnit == Constant.UNIT_MJ) {
            // Display as MJ
            tvBmr.setText(String.format(Locale.getDefault(), "%1.2f MJ", erCalc.bmr));
            tvTdee.setText(String.format(Locale.getDefault(), "%1.2f MJ", erCalc.tdee));
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
        if (erCalc.gender == Constant.GENDER_FEMALE)
            msgGender = sArray[Constant.GENDER_FEMALE];
        else if (erCalc.gender == Constant.GENDER_MALE)
            msgGender = sArray[Constant.GENDER_MALE];

        // Build string for bmr formula
        sArray = getResources().getStringArray(R.array.pref_formula_entries);

        switch (erCalc.bmrFormula) {
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
        if (erCalc.measurementType == Constant.MEASUREMENT_METRIC) {
            msgUnitWeight = getString(R.string.unit_metric_m);
            msgUnitHeight = getString(R.string.unit_metric_cm);
        }
        if (erCalc.measurementType == Constant.MEASUREMENT_IMPERIAL){
            msgUnitWeight = getString(R.string.unit_imperial_lb);
            msgUnitHeight = getString(R.string.unit_imperial_inches);
        }

        // Build strings for bmr and tdee and add selected energy unit
        sArray = getResources().getStringArray(R.array.pref_energy_unit_entries);

        if (erCalc.energyUnit == Constant.UNIT_KCAL) {
            msgBmr = String.format(Locale.getDefault(), "%1.0f", erCalc.bmr) +
                    " " + sArray[Constant.UNIT_KCAL];
            msgTdee = String.format(Locale.getDefault(), "%1.0f", erCalc.tdee) +
                    " " + sArray[Constant.UNIT_KCAL];
        }
        else if (erCalc.energyUnit == Constant.UNIT_MJ) {
            msgBmr = String.format(Locale.getDefault(), "%1.2f", erCalc.bmr) +
                    " " + sArray[Constant.UNIT_MJ];
            msgTdee = String.format(Locale.getDefault(), "%1.2f", erCalc.tdee) +
                    " " + sArray[Constant.UNIT_MJ];
        }

        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<html><body><h1>TDEECalculator</h1>")
                        .append("<h2>Settings</h2>")
                        .append("<p>Weight: " + erCalc.weight + " " + msgUnitWeight + "<br>")
                        .append("Height: " + erCalc.height + " " + msgUnitHeight + "<br>")
                        .append("Age: " + erCalc.age + "<br>")
                        .append("Gender: " + msgGender + "<br></p>")
                        .append("<h2>Calculation</h2>")
                        .append("BMI: <br>")
                        .append(String.format(Locale.getDefault(), "%1.1f kg/m²", erCalc.bmi) + "<br>")
                        .append("<p>Basal metabolic rate: <br>")
                        .append(msgBmr + "<br>")
                        .append("calculated with " + msgFormula + "<br>")
                        .append("<p>Physical activity level: <br>")
                        .append(erCalc.pal + "<br>")
                        .append("<p>Total daily energy expenditure: <br>")
                        .append(msgTdee + "<br></p>")
                        .append("</body></html>")
                        .toString())
        );

        startActivity(Intent.createChooser(emailIntent, getString(R.string.title_intent_email)));
    }

    // Creates a string-array of imperial height values (format: ft"in)
    public ArrayList<String> getImperialHeightArray() {
        ArrayList aL = new ArrayList();
        aL.add("5\"2");
        aL.add("5\"3");
        aL.add("5\"4");
        aL.add("5\"5");
        aL.add("5\"6");
       /* int pos = 0;
        for (int feet = 0; feet <= 12; feet++) {
            for (int inches = 0; inches < 12; inches++) {
                aL.add(String.format("%d\"%d",feet,inches));
            }
        }
        aL.trimToSize();*/
        return aL;
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
            resumeFromSettings = true;
            startActivity(new Intent(this, MainSettings.class));
            return true;
        }
        else if (item.getItemId() == R.id.action_send_email) {
            // Check if a calculation has been done
            if (erCalc.tdee > 0) sendResultsByEMail();
            else showToast(getString(R.string.msg_menu_email_warning));
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Get the static app context
     */
    public static Context getAppContext() {
        return TDEECalculator.context;
    }


}
