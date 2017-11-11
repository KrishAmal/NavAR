package com.univ.team12.navar.ar;

import android.location.Location;

import com.beyondar.android.plugin.BeyondarObjectPlugin;
import com.beyondar.android.plugin.GeoObjectPlugin;
import com.beyondar.android.util.math.Distance;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;

import java.util.Iterator;

/**
 * Created by Amal Krishnan on 27-03-2017.
 */

public class ArObject extends BeyondarObject {

    private double mLongitude;
    private double mLatitude;
    private double mAltitude;
    private String placeId;

    public ArObject(long id) {
        super(id);
        this.setVisible(true);
    }

    public ArObject() {
        this.setVisible(true);
    }

    public void setPlaceId(String placeId){
        this.placeId=placeId;
    }

    public String getPlaceId(){
            return placeId;
    }

    public void setGeoPosition(double latitude, double longitude) {
        this.setGeoPosition(latitude, longitude, this.mAltitude);
    }

    public void setGeoPosition(double latitude, double longitude, double altitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mAltitude = altitude;
        Object var7 = this.lockPlugins;
        synchronized(this.lockPlugins) {
            Iterator var9 = this.plugins.iterator();

            while(var9.hasNext()) {
                BeyondarObjectPlugin plugin = (BeyondarObjectPlugin)var9.next();
                if(plugin instanceof GeoObjectPlugin) {
                    ((GeoObjectPlugin)plugin).onGeoPositionChanged(latitude, longitude, altitude);
                }
            }

        }
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public double getAltitude() {
        return this.mAltitude;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public void setLocation(Location location) {
        if(location != null) {
            this.setGeoPosition(location.getLatitude(), location.getLongitude());
        }
    }

    public double calculateDistanceMeters(GeoObject geo) {
        return this.calculateDistanceMeters(geo.getLongitude(), geo.getLatitude());
    }

    public double calculateDistanceMeters(double longitude, double latitude) {
        return Distance.calculateDistanceMeters(this.getLongitude(), this.getLatitude(), longitude, latitude);
    }

}
