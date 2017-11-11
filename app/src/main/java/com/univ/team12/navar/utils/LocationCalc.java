package com.univ.team12.navar.utils;

import com.univ.team12.navar.utils.LatLng;

/**
 * Created by Amal Krishnan on 08-05-2017.
 */

public class LocationCalc {

    private final static double EarthRadius=6371.0d;

//    private class LatLng{
//        double lat;
//        double lng;
//
//        LatLng(double lat,double lng){
//            this.lat=lat;
//            this.lng=lng;
//        }
//
//        public double getLat(){
//            return lat;
//        }
//
//        public double getLng(){
//            return lng;
//        }
//    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EarthRadius * c;
    }

    public static double calcBearing(double lat1_D,double lat2_D,double lng1_D,double lng2_D){
        double lat1_R =Math.toRadians(lat1_D);
        double lat2_R=Math.toRadians(lat2_D);
        double lng1_R =Math.toRadians(lng1_D);
        double lng2_R=Math.toRadians(lng2_D);

        double y=Math.sin(lng2_R-lng1_R)*Math.cos(lat2_R);
        double x=Math.cos(lat1_R)*Math.sin(lat2_R)-Math.sin(lat1_R)*Math.cos(lat2_R)*Math.cos(lng2_R-lng1_R);

        return  Math.toDegrees(Math.atan2(y,x));
    }

    public static LatLng calcLatLngfromBearing(double lat1_D, double lng1_D, double bear, double d){
        double lat1_R=Math.toRadians(lat1_D);
        double lng1_R=Math.toRadians(lng1_D);

        double lat2_R = Math.asin( Math.sin(lat1_R)*Math.cos(d/EarthRadius) +
                Math.cos(lat1_R)*Math.sin(d/EarthRadius)*Math.cos(bear) );
        double lng2_R = lng1_R + Math.atan2(Math.sin(bear)*Math.sin(d/EarthRadius)*Math.cos(lat1_R),
                Math.cos(d/EarthRadius)-Math.sin(lat1_R)*Math.sin(lat2_R));

        return new LatLng(Math.toDegrees(lat2_R),Math.toDegrees(lng2_R));
    }

}
