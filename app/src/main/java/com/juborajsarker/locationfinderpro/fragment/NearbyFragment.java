package com.juborajsarker.locationfinderpro.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.juborajsarker.locationfinderpro.ApiService.Api;
import com.juborajsarker.locationfinderpro.R;
import com.juborajsarker.locationfinderpro.activity.NearbyMapsActivity;
import com.juborajsarker.locationfinderpro.adapter.PlaceAdapter;
import com.juborajsarker.locationfinderpro.java_class.Geometry;
import com.juborajsarker.locationfinderpro.java_class.ModelPlace;
import com.juborajsarker.locationfinderpro.java_class.OpeningHours;
import com.juborajsarker.locationfinderpro.java_class.Photo;
import com.juborajsarker.locationfinderpro.java_class.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyFragment extends Fragment {




    View view;

    Spinner categorySP;
    EditText radiusET;
    ImageView searchBTN;
    LinearLayout mapLayout;
    RecyclerView recyclerView;
    PlaceAdapter adapter;
    List<Result> resultList;

    List<Geometry> geometryList;
    List<com.juborajsarker.locationfinderpro.java_class.Location> locationList;
    List<Photo> photoList;
    List<OpeningHours> openingHoursList;

    Geometry geometry;
    com.juborajsarker.locationfinderpro.java_class.Location location;
    OpeningHours openingHours;


    int counter = 0;

    boolean clicked = false;
    boolean refresh = false;


    int radius;
    int placePosition;
    String category;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int PLACE_PICKER_REQUEST = 1;

    double latitude;
    double longitude;


    ProgressDialog progressDialog;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager lm;
    LocationManager locationManager;

    double lat = 0;
    double lng = 0;

    WifiManager wifi;


    public NearbyFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_nearby, container, false);

        categorySP = (Spinner) view.findViewById(R.id.spinner_nearby_choice);
        radiusET = (EditText) view.findViewById(R.id.radius_ET);
        searchBTN = (ImageView) view.findViewById(R.id.btn_search);
        mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);






        resultList = new ArrayList<>();
        geometryList = new ArrayList<>();
        locationList = new ArrayList<>();
        photoList = new ArrayList<>();
        openingHoursList = new ArrayList<>();


        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                for (int i = 0; i < resultList.size(); i++) {

                    geometry = resultList.get(i).getGeometry();
                    geometryList.add(geometry);

                    location = resultList.get(i).getGeometry().getLocation();
                    locationList.add(location);

                    photoList = resultList.get(i).getPhotos();

                    openingHours = resultList.get(i).getOpeningHours();
                    openingHoursList.add(openingHours);
                }

                hideInput();

                Gson gson = new Gson();

                String locationListGson = gson.toJson(locationList);
                String resultListGson = gson.toJson(resultList);

                Intent intent = new Intent(getContext(), NearbyMapsActivity.class);
                intent.putExtra("currentLat", lat);
                intent.putExtra("currentLng", lng);

                intent.putExtra("locationListGson", locationListGson);
                intent.putExtra("resultListGson", resultListGson);
                startActivity(intent);

            }
        });


        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideInput();

                if (checkConnection()){

                    if (!checkLocationPermission()) {

                        checkLocationPermission();


                    } else {

                        if (radiusET.getText().toString().equals("") ||
                                Integer.parseInt(radiusET.getText().toString()) == 0) {

                            radiusET.setError("Please enter a valid radius !!!");

                        } else {


                            if (categorySP.getSelectedItemPosition() > 0) {

                                counter++;
                                clicked = true;

                                if (lat == 0 && lng == 0) {

                                    radius = Integer.parseInt(radiusET.getText().toString());
                                    placePosition = categorySP.getSelectedItemPosition();
                                    getCategory();
                                    refreshLocation();


                                } else {

                                    radius = Integer.parseInt(radiusET.getText().toString());
                                    placePosition = categorySP.getSelectedItemPosition();
                                    getCategory();
                                    getNearbyPlaces();
                                }

                            } else {

                                Toast.makeText(getContext(), "Please select a valid place type !!!", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }

                }else {

                    Toast.makeText(getContext(), "No Internet connection. ", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }




    private void getNearbyPlaces() {


        if (lat != 0 && lng != 0) {




        //    Toast.makeText(getContext(), "Location traced successfully !!!", Toast.LENGTH_SHORT).show();

            int finalRadius = radius * 1000;

            String apiKey = "AIzaSyCyKugNDY5CdTwYmpcYSZ1xcI0jhkQRQ70";
            String url = createUrl(lat, lng, category, finalRadius, apiKey);
            Log.d("finalUrl", url);



            if (refresh){

                progressDialog.dismiss();
                refresh = false;
            }

            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage("Please wait while loading data ......");
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();


//            String baseUrl = "https://maps.googleapis.com/maps/api/place/search/json?"
//                    + "&location="
//                    + Double.toString(lat) +","
//                    +Double.toString(lng)
//                    + "&radius=" + finalRadius
//                    +"&types="+category
//                    + "&sensor/";

            String restUrl = "json?&location=" + Double.toString(lat) + ","
                    + Double.toString(lng)
                    + "&radius=" + finalRadius
                    + "&types=" + category
                    + "&sensor=false&key=" + apiKey;


            Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();

            Api api = retrofit.create(Api.class);
            Call<ModelPlace> call = api.getResults(restUrl);


            call.enqueue(new Callback<ModelPlace>() {
                @Override
                public void onResponse(Call<ModelPlace> call, Response<ModelPlace> response) {

                    ModelPlace modelPlace = response.body();


                    resultList = modelPlace.getResults();
                    adapter = new PlaceAdapter(resultList, getContext(), lat, lng, categorySP.getSelectedItemPosition());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    dialog.dismiss();

                    if (resultList.size() > 0){

                        mapLayout.setVisibility(View.VISIBLE);

                    }else {

                        Toast.makeText(getContext(), "No place found between " + radius + " kilometer", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<ModelPlace> call, Throwable t) {

                    Toast.makeText(getContext(), "Error found", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Log.d("ErrorOccur", t.getMessage());


                }
            });


        }


        clicked = false;


    }

    private void getCategory() {

        switch (placePosition) {

            case 0: {

                category = "null";
                break;

            }
            case 1: {

                category = "airport";
                break;

            }
            case 2: {

                category = "atm";
                break;

            }
            case 3: {

                category = "bank";
                break;

            }
            case 4: {

                category = "bakery";
                break;

            }
            case 5: {

                category = "bar";
                break;

            }
            case 6: {

                category = "book_store";
                break;

            }
            case 7: {

                category = "bus_station";
                break;

            }
            case 8: {

                category = "car_rental";
                break;

            }
            case 9: {

                category = "car_repair";
                break;

            }
            case 10: {

                category = "car_wash";
                break;

            }
            case 11: {

                category = "cafe";
                break;

            }
            case 12: {

                category = "clothing_store";
                break;

            }
            case 13: {

                category = "dentist";
                break;

            }
            case 14: {

                category = "doctor";
                break;

            }
            case 15: {

                category = "electronics_store";
                break;

            }
            case 16: {

                category = "embassy";
                break;

            }
            case 17: {

                category = "fire_station";
                break;

            }
            case 18: {

                category = "gas_station";
                break;

            }
            case 19: {

                category = "local_government_office";
                break;

            }
            case 20: {

                category = "gym";
                break;

            }
            case 21: {

                category = "hospital";
                break;

            }
            case 22: {

                category = "jewelry_store";
                break;

            }
            case 23: {

                category = "laundry";
                break;

            }
            case 24: {

                category = "mosque";
                break;

            }
            case 25: {

                category = "movie_theater";
                break;

            }
            case 26: {

                category = "park";
                break;

            }
            case 27: {

                category = "pharmacy";
                break;

            }
            case 28: {

                category = "police";
                break;

            }
            case 29: {

                category = "post_office";
                break;

            }
            case 30: {

                category = "restaurant";
                break;

            }
            case 31: {

                category = "school";
                break;

            }
            case 32: {

                category = "shopping_mall";
                break;

            }
            case 33: {

                category = "university";
                break;

            }
        }
    }


    public boolean checkLocationPermission() {


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


            return false;

        } else {

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {


                }

            }

        }
    }


    public void checkGpsStatus() {

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("GPS is not enabled ");
            dialog.setIcon(android.R.drawable.ic_menu_mylocation);
            dialog.setMessage("Please turn on GPS first.");
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings),

                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);

                        }
                    });

            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                }
            });
            dialog.show();
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {


            lat = loc.getLatitude();
            lng = loc.getLongitude();


            progressDialog.dismiss();

            if (clicked) {

                getNearbyPlaces();
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    private void refreshLocation() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {


                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    getNearbyPlaces();


                }else {


                    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


                        refresh = true;
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Please wait while tracing your current location .......");
                        progressDialog.setCancelable(false);
                        progressDialog.show();


                        locationManager = (LocationManager)
                                getActivity().getSystemService(Context.LOCATION_SERVICE);

                        final LocationListener locationListener = new MyLocationListener();

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            progressDialog.dismiss();

                            return;
                        }



                        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 30000, 10, locationListener);

                        }else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener);
                        }


                    }else {

                        checkGpsStatus();
                        Toast.makeText(getContext(), "GPS off", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });




    }


    public String createUrl(double latitude, double longitude,String category, int radius, String API_KEY){

        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");

        if (category.equals("")) {

            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=" + radius);
            //   urlString.append("&types="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=" + radius);
            urlString.append("&types="+category);
            urlString.append("&sensor=false&key=" + API_KEY);



        }


        return urlString.toString();

    }


    public void hideInput(){

        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) {


        }
    }


    public boolean checkConnection(){

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfoWifi.isConnected();
        NetworkInfo networkInfoMobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfoMobile.isConnected();

        if (isWifiConn || isMobileConn){

            return true;

        }else {

            return false;
        }
    }



}
