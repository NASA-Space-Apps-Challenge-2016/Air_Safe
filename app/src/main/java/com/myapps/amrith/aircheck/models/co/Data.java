package com.myapps.amrith.aircheck.models.co;

/**
 * Created by hp on 24-04-2016.
 */
public class Data {
    public double precision;
    public double pressure;
    public double value;

    @Override
    public String toString() {
        return precision+" "+pressure+" "+value;
    }
}
