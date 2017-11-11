package com.univ.team12.navar.utils;

/**
 * Created by Amal Krishnan on 08-05-2017.
 */

public class LatLng {

        private double lat;
        private double lng;

        public LatLng(double lat,double lng){
            this.lat=lat;
            this.lng=lng;
        }

        public double getLat(){
            return lat;
        }

        public double getLng(){
            return lng;
        }
}
