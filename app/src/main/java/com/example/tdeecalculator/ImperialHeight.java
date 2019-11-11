package com.example.tdeecalculator;

import java.util.ArrayList;

public class ImperialHeight {
    public double feet;
    public double inches;

    /* Default constructor */
    public ImperialHeight() {
        feet = 0;
        inches = 0.0;
    }

    /* Constructor sets values of feet and inches to object */
    public ImperialHeight(double feet, double inches) {
        this.feet = feet;
        this.inches = inches;
    }

    /* Constructor for metric height value insertion */
    public ImperialHeight(Double height) {
        // Convert  cm to inches by deviding with 2.54
        double dHeightImp = height / Constant.INCH_IN_CM;

        // Calculate feet by deviding inches with 12 and rounding down
        this.feet = new Double(Math.floor(dHeightImp / 12));

        // Calculate inches between feet with mod 12
        // Multiplaction is need to ensure the displaying of 1 decimal points
        this.inches = new Double(Math.round((dHeightImp%12)*10))/10;
    }

    /*
     * Returns height for metric measurement system in cm
     */
    public Double getMetricHeight() {
        double height = feet * Constant.FEET_IN_CM;
        height += inches * Constant.INCH_IN_CM;
        return new Double(Math.round(height));
    }

    /*
     * Returns a string-array of imperial height values (format: ft"in)
     */
    public ArrayList<String> getImperialHeightArray() {
        ArrayList aL = new ArrayList();
        Double dInches = 0.0;
      /*  aL.add("5\"2");
        aL.add("5\"3");
        aL.add("5\"4");
        aL.add("5\"5");
        aL.add("5\"6");*/
        int pos = 0;
        for (int feet = 0; feet <= 12; feet++) {
            for (int inches = 0; inches < 100; inches++) {
                dInches = new Double (inches);
                dInches = dInches / 10;
                aL.add(String.format("%d\"%1.1f",feet,dInches));
            }
        }
        return aL;
    }
}
