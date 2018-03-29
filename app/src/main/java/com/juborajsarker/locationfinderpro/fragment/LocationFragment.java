package com.juborajsarker.locationfinderpro.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.juborajsarker.locationfinderpro.R;
import com.juborajsarker.locationfinderpro.activity.MapsActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationFragment extends Fragment {



    private static final String TAG = "Details";
    View view;


    TextView addressTV, cityTV, subCityTV, posterCodeTV, divisionTV, countryTV, countryCodeTV, latitudeTV, longitudeTV;
    LinearLayout refreshLayout, mapLayout;

    ProgressDialog progressDialog;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager lm;
    LocationManager locationManager;

    double lat, lng;

    String longitude, latitude;

    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    LocationListener locationListener;


    public LocationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_location, container, false);

        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);




        init();
        checkLocationPermission();






        return view;
    }


    private void init() {


        addressTV = (TextView) view.findViewById(R.id.addressTV);
        cityTV = (TextView) view.findViewById(R.id.cityTV);
        subCityTV = (TextView) view.findViewById(R.id.subcityTV);
        posterCodeTV = (TextView) view.findViewById(R.id.posterCodeTV);
        divisionTV = (TextView) view.findViewById(R.id.divisionTV);
        countryTV = (TextView) view.findViewById(R.id.countryTV);
        countryCodeTV = (TextView) view.findViewById(R.id.countryCodeTV);
        latitudeTV = (TextView) view.findViewById(R.id.latitudeTV);
        longitudeTV = (TextView) view.findViewById(R.id.longitudeTV);

        refreshLayout = (LinearLayout) view.findViewById(R.id.refreshLayout);
        mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);


        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {


                    refreshLocation();
                    Toast.makeText(getContext(), "Location updated successfully !!!", Toast.LENGTH_SHORT).show();


                } else {


                    checkLocationPermission();
                }

            }
        });


        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please wait.....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (lat != 0 && lng != 0) {

                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);
                    progressDialog.dismiss();

                } else {

                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please refresh the location first !!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }




    @Override
    public void onResume() {
        super.onResume();


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fullLocationService();

           // getPlaceResult();


        }


    }

    private void fullLocationService() {


        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait while fetching data from GPS .......");
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


            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {


                            if (location != null) {


                                lat = location.getLatitude();
                                lng = location.getLongitude();

                                setLocation(location);


                            } else {


                                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        return;
                                    }


                                    locationManager.requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


                                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

                                }


                            }
                        }
                    });


        } else {

            checkGpsStatus();
            Toast.makeText(getContext(), "GPS off", Toast.LENGTH_SHORT).show();
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


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {


            longitude = String.valueOf(loc.getLongitude());
            latitude = String.valueOf(+loc.getLatitude());

            lat = loc.getLatitude();
            lng = loc.getLongitude();

            mustExecute();


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





//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, getContext());
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    public void getPlaceResult() {
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            return;
//        }
//        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
//        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
//                            placeLikelihood.getPlace().getName(),
//                            placeLikelihood.getLikelihood()));
//
//
//
//                }
//                likelyPlaces.release();
//            }
//        });
//    }

    private void refreshLocation() {


        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait while fetching data from GPS .......");
            progressDialog.setCancelable(false);
            progressDialog.show();


            locationManager = (LocationManager)
                    getActivity().getSystemService(Context.LOCATION_SERVICE);

              locationListener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                progressDialog.dismiss();

                return;
            }


            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 10, locationListener);




            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
            }


        } else {

            checkGpsStatus();
            Toast.makeText(getContext(), "GPS off", Toast.LENGTH_SHORT).show();
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

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {


                }

            }

        }
    }


    public void mustExecute(){

        String cityName = null;
        String address = null;
        String subCity = null;
        String posterCode = null;
        String division = null;
        String country = null;
        String countryCode = null;



        try {


            Geocoder gcd = new Geocoder(getContext());
            List<Address> addresses;

            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {

                cityName = addresses.get(0).getLocality();
                address = addresses.get(0).getFeatureName();
                subCity = addresses.get(0).getSubLocality();
                posterCode = addresses.get(0).getPostalCode();
                division = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                countryCode = addresses.get(0).getCountryCode();


            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }




        addressTV.setText(address + ", " + subCity + ", " + cityName);

       // addressTV.setText(address);
      //  cityTV.setText(cityName);
      //  subCityTV.setText(subCity);
        posterCodeTV.setText(posterCode);
        divisionTV.setText(division);
        countryTV.setText(country);
        countryCodeTV.setText(countryCode);
        latitudeTV.setText(latitude);
        longitudeTV.setText(longitude);

        progressDialog.dismiss();


    }

    public void setLocation(Location loc) {

        String longitude = String.valueOf(loc.getLongitude());
        String latitude = String.valueOf(+loc.getLatitude());


        /*------- To get city name from coordinates -------- */
        String cityName = null;
        String address = null;
        String subCity = null;
        String posterCode = null;
        String division = null;
        String country = null;
        String countryCode = null;


        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {

                cityName = addresses.get(0).getLocality();
                address = addresses.get(0).getFeatureName();
                subCity = addresses.get(0).getSubLocality();
                posterCode = addresses.get(0).getPostalCode();
                division = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                countryCode = addresses.get(0).getCountryCode();

                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        addressTV.setText(address + ", " + subCity + ", " + cityName);

        // addressTV.setText(address);
        //  cityTV.setText(cityName);
        //  subCityTV.setText(subCity);
        posterCodeTV.setText(posterCode);
        divisionTV.setText(division);
        countryTV.setText(country);
        countryCodeTV.setText(countryCode);
        latitudeTV.setText(latitude);
        longitudeTV.setText(longitude);

        progressDialog.dismiss();
    }


}
