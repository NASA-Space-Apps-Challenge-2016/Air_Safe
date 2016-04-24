package com.myapps.amrith.aircheck.models.co;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by hp on 24-04-2016.
 */
public class ServerResponse {
    public String time;
    public LatLng location;
    List<Data> data;

    @Override
    public String toString() {
        return time+location.latitude+" "+location.longitude + data.toString();
    }
}
