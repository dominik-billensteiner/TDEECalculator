package com.example.tdeecalculator;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class EnergyReqCalc {
    private @Constant.EnergyUnit
    int formulaEnergyUnit;
    public @Constant.BmrFormula
    int bmrFormula;
    public @Constant.Gender
    int gender;
    public @Constant.MeasurementType
    int measurementType;
    public @Constant.EnergyUnit
    int energyUnit;

    public double bmi = 0;
    public double bmr = 0;
    public double pal = 1.4;
    public double tdee = 0;
    public int age;
    public double height;
    public double weight;

    // Default Constructor
    public EnergyReqCalc() {
        age = 0;
        weight = 0;
        height = 0;
        this.gender = Constant.GENDER_FEMALE;
        measurementType = Constant.MEASUREMENT_METRIC;
        energyUnit = Constant.UNIT_KCAL;
        bmrFormula = Constant.BMR_HARRIS_AND_BENEDICT;
    }

    // Constructor
    public EnergyReqCalc(int age, double weight, double height,
                  @Constant.Gender int gender,
                  @Constant.MeasurementType int measurementType,
                  @Constant.EnergyUnit int energyUnit,
                  @Constant.BmrFormula int bmrFormula,
                  double pal) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.measurementType = measurementType;
        this.energyUnit = energyUnit;
        this.bmrFormula = bmrFormula;
        this.pal = pal;
    }

    // Calculate Body Mass Index (BMI) in kg/m²
    public void calculateBmi() {
        bmi = (weight / ((height / 100) * (height / 100)));
        /*
        if (measurementType == Constant.MEASUREMENT_METRIC) {
            bmi = (weight / ((height / 100) * (height / 100)));
        } else if (measurementType == Constant.MEASUREMENT_IMPERIAL) {
            //Todo: implement conversion
            bmi = 99.0;
        }*/
    }

    // Calculate Total Daily Energy Expenditure
    public void calculateTdee() {
                // Calculate basal metabolic rate by selected formula and remember energy unit of formula
        if (bmrFormula == Constant.BMR_HARRIS_AND_BENEDICT) {
            formulaEnergyUnit = Constant.UNIT_KCAL;
            bmr = calculateBmrWithHarrisAndBenedict();
        }
        else if (bmrFormula == Constant.BMR_HENRY) {
            formulaEnergyUnit = Constant.UNIT_MJ;
            bmr = calculateBmrWithHenry();
        }
        else if (bmrFormula == Constant.BMR_MIFFLIN) {
            formulaEnergyUnit = Constant.UNIT_KCAL;
            bmr = calculateBmrWithMifflin();
        }
        else if (bmrFormula == Constant.BMR_MUELLER) {
            formulaEnergyUnit = Constant.UNIT_MJ;
            bmr = calculateBmrWithMueller();
        }
        else if (bmrFormula == Constant.BMR_SCHOFIELD) {
            formulaEnergyUnit = Constant.UNIT_MJ;
            bmr = calculateBmrWithSchofield();
        }

        // Check if unit conversion for energy is needed and return given value in correct unit
        if ((energyUnit == Constant.UNIT_KCAL) &&  (formulaEnergyUnit == Constant.UNIT_MJ)){
            // Conversion from MJ to kcal
            bmr = toKcal(bmr);
        }
        else if ((energyUnit == Constant.UNIT_MJ) &&  (formulaEnergyUnit == Constant.UNIT_KCAL)) {
            // Conversion from kcal to MJ
            bmr = toMj(bmr);
        }
        tdee = bmr * pal;
    }

    /* Calculates basal metabolic rate (BMR) with the formula from Harris and Benedict et al (1919).
    - The formula is based on the following data: 136 men, 103 women; age 21-70; height 151-200cm; weight 25-124,9kg
    - Considers age, gender, weight and height
    - Suited for general estimations, when no specified criteria, which benefit another formula, are met.
    ## FORMULA REVIEW: YES-DONE!
    ## FORMULA TESTING: NO
     */
    private Double calculateBmrWithHarrisAndBenedict() {
        Double fBmr = 0.0;
        if (gender == Constant.GENDER_FEMALE) {
            // Calcualte with formula for women
            return (655.0955 + 9.5634 * weight + 1.8496 * height - 4.6756 * age);
        } else if (gender == Constant.GENDER_MALE) {
            // Calcualte with formula for men
            return (66.4730 + 13.7516 * weight + 5.0033 * height - 6.7550 * age);
        }
        return fBmr;
    }

    /* Calculates basal metabolic rate (BMR) with the formula from Henry (2005).
    - The formula is a further development of the Schofield formula
    - It considers age, gender, weight
    - Different formulas for different age groups, also suited for children
    - Excludes italians and includes probands living in tropcial areas
    - Weblink: https://www.cambridge.org/core/journals/public-health-nutrition/article/basal-metabolic-rate-studies-in-humans-measurement-and-development-of-new-equations/61A9EA486ABFA478FEF2FCE1E70D5BEE
     ## FORMULA REVIEW: NO
     ## FORMULA TESTING: NO
     */
    private Double calculateBmrWithHenry() {
        Double fBmr = 0.0;
        if (energyUnit == Constant.UNIT_MJ) {
            // Choose calculation methods for preferred energy unit
            if (gender == Constant.GENDER_MALE) {
                // Calculate with age group specific formula for men in MJ
                if (age <= 3)
                    fBmr = (0.255 * weight - 0.141);
                else if ((age > 3) && (age <= 10))
                    fBmr = (0.0937 * weight + 2.15);
                else if ((age > 10) && (age <= 18))
                    fBmr = (0.0769 * weight + 2.43);
                else if ((age > 18) && (age <= 30))
                    fBmr = (0.0669 * weight + 2.28);
                else if ((age > 30) && (age <= 60))
                    fBmr = (0.0592 * weight + 2.48);
                else if (age > 60)
                    fBmr = (0.0563 * weight + 2.15);
            } else if (gender == Constant.GENDER_FEMALE) {
                // Calculate with age group specific formula for women in MJ
                if (age <= 3)
                    fBmr = (0.246 * weight - 0.0965);
                else if ((age > 3) && (age <= 10))
                    fBmr = (0.0842 * weight + 2.12);
                else if ((age > 10) && (age <= 18))
                    fBmr = (0.0465 * weight + 3.18);
                else if ((age > 18) && (age <= 30))
                    fBmr = (0.0546 * weight + 2.33);
                else if ((age > 30) && (age <= 60))
                    fBmr = (0.0407 * weight + 2.90);
                else if (age > 60)
                    fBmr = (0.0424 * weight + 2.38);
            }
        } else if (measurementType == Constant.UNIT_KCAL) {
            // Choose calculation methods for preferred energy unit
            if (gender == Constant.GENDER_MALE) {
                // Calculate with age group specific formula for men in MJ
                if (age <= 3)
                    fBmr = (61.0 * weight - 33.7);
                else if ((age > 3) && (age <= 10))
                    fBmr = (23.3 * weight + 514);
                else if ((age > 10) && (age <= 18))
                    fBmr = (18.4 * weight + 581);
                else if ((age > 18) && (age <= 30))
                    fBmr = (16.0 * weight + 545);
                else if ((age > 30) && (age <= 60))
                    fBmr = (14.2 * weight + 593);
                else if (age > 60)
                    fBmr = (13.5 * weight + 514);
            } else if (gender == Constant.GENDER_FEMALE) {
                // Calculate with age group specific formula for women in MJ
                if (age <= 3)
                    fBmr = (58.9 * weight - 23.1);
                else if ((age > 3) && (age <= 10))
                    fBmr = (20.1 * weight + 507);
                else if ((age > 10) && (age <= 18))
                    fBmr = (11.1 * weight + 761);
                else if ((age > 18) && (age <= 30))
                    fBmr = (13.1 * weight + 558);
                else if ((age > 30) && (age <= 60))
                    fBmr = (9.74 * weight + 694);
                else if (age > 60)
                    fBmr = (10.1 * weight + 569);
            }
        }
        return fBmr;
    }

    /* Calculates basal metabolic rate (BMR) with the formula from Mifflin et al. (1990)
    - The formula is based on the following data: 498 healthy probands;
        264 normalweight; 234 overweight; age 19-78
    - Considers age, gender, weight and weight
    - Suited for general estimations, when no specified criteria, which benefit another formula, are met.
     ## FORMULA REVIEW: NO
     ## FORMULA TESTING: NO
     */
    private Double calculateBmrWithMifflin() {
        return (9.99 * weight
                + 6.25 * height
                - 4.92 * age
                + 166 * gender
                - 161);
    }

    /* Calculates basal metabolic rate (BMR) with the formula for resting energy expenditure (REE) from MUELLER et al (2006).
    - The formula is based on the following data: 2528 subjects, age 5-91 years, german residents.
    - Considers different BMI classes and is therefore recommendend for the calculation in obese subjects
     ## FORMULA REVIEW: NO
     ## FORMULA TESTING: NO
     */
    private Double calculateBmrWithMueller() {
        Double fBmr = 0.0;
        // Calculation of BMR by BMI classes
        if (bmi <= 18.5) {
            fBmr = (0.07122 * weight - 0.02149 * age + 0.82 * gender + 0.731);
        } else if ((bmi > 18.5) && (bmi <= 25)) {
            fBmr = (0.02219 * weight + 0.02118 * height + 0.884 * gender - 0.01191 * age + 1.233);
        } else if ((bmi > 25) && (bmi < 30)) {
            fBmr = (0.04507 * weight + 1.006 * gender - 0.01553 * age + 3.407);
        } else if (bmi >= 30) {
            fBmr = (0.05 * weight + 1.103 * gender - 0.01586 * age + 2.924);
        }
        return fBmr;
    }

    // not suitable for tropical populations
    private Double calculateBmrWithSchofield() {
        Double fBmr = 0.0;
        if (energyUnit == Constant.UNIT_MJ) {
            // Choose calculation methods for preferred energy unit
            if (gender == Constant.GENDER_MALE) {
                // Calculate with age group specific formula for men in MJ
                if (age <= 3)
                    fBmr = (0.249 * weight - 0.127);
                else if ((age > 3) && (age <= 10))
                    fBmr = (0.095 * weight + 2.110);
                else if ((age > 10) && (age <= 18))
                    fBmr = (0.074 * weight + 2.754);
                else if ((age > 18) && (age <= 30))
                    fBmr = (0.063 * weight + 2.896);
                else if ((age > 30) && (age <= 60))
                    fBmr = (0.048 * weight + 3.695);
                else if (age > 60)
                    fBmr = (0.049 * weight + 2.459);
            } else if (gender == Constant.GENDER_FEMALE) {
                // Calculate with age group specific formula for women in MJ
                if (age <= 3)
                    fBmr = (0.244 * weight - 0.130);
                else if ((age > 3) && (age <= 10))
                    fBmr = (0.085 * weight + 2.033);
                else if ((age > 10) && (age <= 18))
                    fBmr = (0.056 * weight + 2.898);
                else if ((age > 18) && (age <= 30))
                    fBmr = (0.062 * weight + 2.036);
                else if ((age > 30) && (age <= 60))
                    fBmr = (0.034 * weight + 3.538);
                else if (age > 60)
                    fBmr = (0.038 * weight + 2.755);
            }
        }
        else if (energyUnit == Constant.UNIT_KCAL) {
            // Choose calculation methods for preferred energy unit
            if (gender == Constant.GENDER_MALE) {
                // Calculate with age group specific formula for men in kcal
                if (age <= 3)
                    fBmr = (59.512 * weight - 30.4);
                else if ((age > 3) && (age <= 10))
                    fBmr = (22.706 * weight + 504.3);
                else if ((age > 10) && (age <= 18))
                    fBmr = (17.686 * weight + 658.2);
                else if ((age > 18) && (age <= 30))
                    fBmr = (15.057 * weight + 692.2);
                else if ((age > 30) && (age <= 60))
                    fBmr = (11.472 * weight + 873.1);
                else if (age > 60)
                    fBmr = (11.711 * weight + 587.7);
            } else if (gender == Constant.GENDER_FEMALE) {
                // Calculate with age group specific formula for women in kcal
                if (age <= 3)
                    fBmr = (58.317 * weight - 31.1);
                else if ((age > 3) && (age <= 10))
                    fBmr = (20.315 * weight + 485.9);
                else if ((age > 10) && (age <= 18))
                    fBmr = (13.384 * weight + 692.6);
                else if ((age > 18) && (age <= 30))
                    fBmr = (14.818 * weight + 486.6);
                else if ((age > 30) && (age <= 60))
                    fBmr = (8.126 * weight + 845.6);
                else if (age > 60)
                    fBmr = (9.082 * weight + 658.5);
            }
        }
        return fBmr;
    }

    /*
     * Converts given value from MJ to kcal
     */
    public Double toKcal(double value){
        return (value * 1000) * 0.239;
    }

    /*
     * Converts given value from kcal to MJ
     */
    public Double toMj(double value) {
        return (value / 238.845896);
    }

    // Converts give value from pounds to kg
    public Double toKg (Double lbWeight) {
        return (lbWeight / Constant.POUND_TO_KG);
    }
}
