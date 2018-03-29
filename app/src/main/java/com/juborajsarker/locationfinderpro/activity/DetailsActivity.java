package com.juborajsarker.locationfinderpro.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.juborajsarker.locationfinderpro.R;
import com.juborajsarker.locationfinderpro.java_class.Location;
import com.juborajsarker.locationfinderpro.java_class.OpeningHours;
import com.juborajsarker.locationfinderpro.java_class.Result;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {



    TextView nameTV, addressTV, phoneTV, websiteTV, latitudeTV, longitudeTV, availabilityTV, ratingTV, numberOfReviewTV;
    ImageView phoneIV, browseIV, mainIV;
    LinearLayout mapLayout;
    RatingBar ratingBar;

    TextView currentLatTV, currentLngTV;

    List <Result> resultList;
    List <Location> locationList;
    List <OpeningHours> openingHoursList;

    String placeID;
    Bitmap bitmap;


    String name, address, website, phone, photoUrl;
    float rating;
    double latitude, longitude;
    boolean availability;
    boolean error;

    double currentLat, currentLng;


    protected GeoDataClient mGeoDataClient;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 55;


    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data......\nPlease wait ......");
        progressDialog.setCancelable(false);
        progressDialog.show();




        mGeoDataClient = Places.getGeoDataClient(this, null);
        init();



        locationList = new ArrayList<>();
        openingHoursList = new ArrayList<>();
        resultList = new ArrayList<>();


        Intent intent = getIntent();

        name = intent.getStringExtra("nameValue");
        address = intent.getStringExtra("addressValue");
        photoUrl = intent.getStringExtra("photoUrl");
        rating = intent.getFloatExtra("ratingValue", 0);
        latitude = intent.getDoubleExtra("lat", 0);
        longitude = intent.getDoubleExtra("lng", 0);
        availability = intent.getBooleanExtra("available", false);

        website = intent.getStringExtra("websiteValue");
        phone = intent.getStringExtra("phoneValue");

        placeID = intent.getStringExtra("placeId");

        error = intent.getBooleanExtra("error", false);

        currentLat = intent.getDoubleExtra("currentLat",0);
        currentLng = intent.getDoubleExtra("currentLng", 0);






        if (availability){

            availabilityTV.setText("OPEN NOW");

        }else {

            if (error){

                availabilityTV.setText("OPEN / CLOSE (not mention)");

            }else {

                availabilityTV.setText("CLOSE NOW");
            }
        }
        nameTV.setText(name);
        websiteTV.setText(website);
        phoneTV.setText(phone);
        addressTV.setText(address);

        ratingTV.setText(String.valueOf(rating));
        ratingBar.setRating(rating);

        latitudeTV.setText(String.valueOf(latitude));
        longitudeTV.setText(String.valueOf(longitude));

        currentLatTV.setText(String.valueOf(currentLat));
        currentLngTV.setText(String.valueOf(currentLng));


        getPhotos(placeID);
        getPlaceDetails(placeID);



    }

    private void init() {

        nameTV = (TextView) findViewById(R.id.nameTV);
        addressTV = (TextView) findViewById(R.id.addressTV);
        phoneTV = (TextView) findViewById(R.id.phoneTV);
        websiteTV = (TextView) findViewById(R.id.websiteTV);
        latitudeTV = (TextView) findViewById(R.id.latitudeTV);
        longitudeTV = (TextView) findViewById(R.id.longitudeTV);
        availabilityTV = (TextView) findViewById(R.id.availabilityTV);
        ratingTV = (TextView) findViewById(R.id.ratingTV);
      //  numberOfReviewTV = (TextView) findViewById(R.id.numberOfReviewTV);

        phoneIV = (ImageView) findViewById(R.id.phoneIV);
        browseIV = (ImageView) findViewById(R.id.browserIV);
        mainIV = (ImageView) findViewById(R.id.mainImage);

        mapLayout = (LinearLayout) findViewById(R.id.mapLayout);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar) ;


        currentLatTV = (TextView) findViewById(R.id.currentLat);
        currentLngTV = (TextView) findViewById(R.id.currentLng);





        phoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phoneTV.getText().toString().equals("")
                        || phoneTV.getText().toString().equals("Not found !!!")){

                    Toast.makeText(DetailsActivity.this, "No number found for phone call !!!", Toast.LENGTH_SHORT).show();


                }else {


                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneTV.getText().toString()));

                    if (!checkCallPermission()) {

                        checkCallPermission();

                    }else {

                        startActivity(callIntent);
                    }


                }

            }
        });


        browseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (websiteTV.getText().toString().equals("")
                        || websiteTV.getText().toString().equals("Not found !!!")){

                    Toast.makeText(DetailsActivity.this, "No website found for browse !!!", Toast.LENGTH_SHORT).show();


                }else {

                    Intent intent = new Intent(DetailsActivity.this, WebviewActivity.class);
                    intent.putExtra("url", websiteTV.getText().toString());
                    startActivity(intent);
                }

            }
        });


        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent intent = new Intent(DetailsActivity.this, SingleMapsActivity.class);
                intent.putExtra("name", nameTV.getText().toString());
                intent.putExtra("address", addressTV.getText().toString());
                intent.putExtra("rating", ratingTV.getText().toString());

                intent.putExtra("currentLat", Double.parseDouble(currentLatTV.getText().toString()));
                intent.putExtra("currentLng", Double.parseDouble(currentLngTV.getText().toString()));

                intent.putExtra("lat", Double.parseDouble(latitudeTV.getText().toString()));
                intent.putExtra("lng", Double.parseDouble(longitudeTV.getText().toString()));

                startActivity(intent);

            }
        });


    }







    private void getPhotos(String placeID) {
        final   String placeId = placeID;
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                try {

                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            bitmap = photo.getBitmap();


                            Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 330, 189, true);
                            mainIV.setImageBitmap(bMapScaled);

                            progressDialog.dismiss();
                        }
                    });
                }catch (Exception e){

                    mainIV.setImageDrawable(ContextCompat.getDrawable(DetailsActivity.this, R.drawable.error_image));
                    progressDialog.dismiss();

                }
            }
        });
    }



    public void getPlaceDetails(String placeId){

        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {

                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    try {

                        websiteTV.setText(myPlace.getWebsiteUri().toString());
                        phoneTV.setText(myPlace.getPhoneNumber().toString());
                        addressTV.setText(myPlace.getAddress().toString());

                    }catch (Exception e){


                        websiteTV.setText("Not found !!!");
                        phoneTV.setText("Not found !!!");
                    }
                    places.release();

                } else {
                    Log.e("PlaceError", "Place not found.");
                }
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    public boolean checkCallPermission() {


        if (ContextCompat.checkSelfPermission(DetailsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(DetailsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_LOCATION);


            return false;

        } else {

            return true;
        }
    }
}
