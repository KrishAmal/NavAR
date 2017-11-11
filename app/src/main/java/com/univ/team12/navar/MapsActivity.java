package com.univ.team12.navar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.univ.team12.navar.network.GeocodeResponse;
import com.univ.team12.navar.network.RetrofitInterface;
import com.univ.team12.navar.network.geocode.Location;
import com.univ.team12.navar.onboarding.DefaultIntro;
import com.univ.team12.navar.utils.UtilsCheck;
import com.univ.team12.navar.utils.PermissionCheck;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnConnectionFailedListener
            ,ConnectionCallbacks,GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener {

    private final static String TAG = "MapsActivity";

    SharedPreferences getPrefs;
    boolean isFirstStart;

    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;

    private Location location;

    private Marker RevMarker;

    @BindView(R.id.fab_menu_btn)
    FloatingActionMenu fab_menu;
    @BindView(R.id.ar_nav_btn)
    com.github.clans.fab.FloatingActionButton ar_nav_btn;
    @BindView(R.id.poi_browser_btn)
    com.github.clans.fab.FloatingActionButton poi_browser_btn;
    @BindView(R.id.decode_box)
    EditText decode_editText;
    @BindView(R.id.decode_btn)
    Button decode_button;
    @BindView(R.id.progressBar_maps)
    ProgressBar progressBar;
    @BindView(R.id.about_btn)
    com.github.clans.fab.FloatingActionButton about_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        Init_intro();
        PermissionCheck.initialPermissionCheckAll(this,this);

        progressBar.setVisibility(View.GONE);

        if(!UtilsCheck.isNetworkConnected(this)){
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                    "Turn Internet On", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

//        CardView.LayoutParams layoutParams=new CardView.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0,getStatusBarHeight(),0,0);
//        CardView cardView=(CardView) findViewById(R.id.decode_cardview);
//        cardView.setLayoutParams(layoutParams);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        ar_nav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, NavActivity.class);
                startActivity(intent);
            }
        });

        poi_browser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, PoiBrowserActivity.class);
                startActivity(intent);
            }
        });

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        decode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(TextUtils.isEmpty(decode_editText.getText())){
                        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                                "Search Field is Empty", Snackbar.LENGTH_SHORT);
                        mySnackbar.show();
                    }else{
                        Geocode_Call(decode_editText.getText().toString());
                    }
                }catch (NullPointerException npe){
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                            "Search Field is Empty", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void Geocode_Call(String address){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<GeocodeResponse> call = apiService.getGecodeData(address,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {

                progressBar.setVisibility(View.GONE);

                List<com.univ.team12.navar.network.geocode.Result> results=response.body().getResults();
                location=results.get(0).getGeometry().getLocation();
                Toast.makeText(MapsActivity.this,location.getLat()+","+location.getLng(), Toast.LENGTH_SHORT).show();

                try{
                    mMap.clear();
                    LatLng loc = new LatLng(location.getLat(),location.getLng());
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(results.get(0).getFormattedAddress())
                            .snippet(results.get(0).getGeometry().getLocationType()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,14.0f));
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    //decode_button.setBackground(getDrawable());

//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            if(marker.isInfoWindowShown())
//                                fab_menu.hideMenu(true);
//                            else
//                                fab_menu.hideMenu(false);
//                            return false;
//                        }
//                    });
                }catch (NullPointerException npe){
                    Log.d(TAG, "onMapReady: Location is NULL");
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void Rev_Geocode_Call(LatLng latlng){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressBar.setVisibility(View.VISIBLE);

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<GeocodeResponse> call = apiService.getRevGecodeData(latlng.latitude+","+latlng.longitude,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {

                progressBar.setVisibility(View.GONE);
                List<com.univ.team12.navar.network.geocode.Result> results=response.body().getResults();
                String address=results.get(0).getFormattedAddress();
                Toast.makeText(MapsActivity.this,address, Toast.LENGTH_SHORT).show();

                RevMarker.setTitle(address);
                RevMarker.setSnippet(results.get(0).getGeometry().getLocationType());
//                try{
//
//                }catch (NullPointerException npe){
//                    Log.d(TAG, "onMapReady: Location is NULL");
//                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void Init_intro(){

        getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                //  If the activity has never started before...
//                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MapsActivity.this, DefaultIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
//                }
                }
            //}
        });

        if(isFirstStart){
            t.start();
        }
    }

//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

//    public void initialPermissionCheck() {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
//        }
//
//        if (ContextCompat.checkSelfPermission
//                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
//
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},30);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},40);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();

        RevMarker=mMap.addMarker(new MarkerOptions().position(latLng));
        Toast.makeText(this, latLng.latitude+" "+latLng.longitude, Toast.LENGTH_SHORT).show();
        Rev_Geocode_Call(latLng);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap.setOnMapLongClickListener(this);
        //mMap.setOnMapClickListener(this);
        Log.d(TAG, "onMapReady: MAP IS READY");
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Toast.makeText(MapsActivity.this, latLng.latitude+","+latLng.longitude, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //PermissionCheck.initialPermissionCheck(this,this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: Short Click "+latLng.toString());
    }
}
