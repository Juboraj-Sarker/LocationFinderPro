package com.juborajsarker.locationfinderpro.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.juborajsarker.locationfinderpro.R;

public class SplashActivity extends AppCompatActivity {

    TextView errorTV;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    boolean gps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        errorTV = (TextView) findViewById(R.id.errorTV);

        errorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkGpsStatus();

                if (! checkConnection()){

                    Toast.makeText(SplashActivity.this, "Please turn on internet", Toast.LENGTH_SHORT).show();

                }else {


                    if (gps && checkConnection()){

                        errorTV.setText("Loading please wait ..........");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {



                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();

                            }
                        }, 5000);
                    }
                }

                }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        checkGpsStatus();

        if (checkConnection() && gps){

            errorTV.setText("Loading please wait ..........");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {



                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();

                }
            }, 5000);


        }else {

            if (!checkConnection()){

                Toast.makeText(this, "Please turn on internet to continue !!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean checkLocationPermission() {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


            return false;

        } else {

            return true;
        }
    }


    public void checkGpsStatus() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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


    }else if (gps_enabled || network_enabled){

            gps = true;
        }

    }


    public boolean checkConnection(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
