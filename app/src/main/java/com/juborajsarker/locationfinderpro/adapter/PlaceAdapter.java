package com.juborajsarker.locationfinderpro.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juborajsarker.locationfinderpro.R;
import com.juborajsarker.locationfinderpro.activity.DetailsActivity;
import com.juborajsarker.locationfinderpro.java_class.Result;

import java.util.ArrayList;
import java.util.List;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.CustomViewHolder> {

    private List<Result> resultList = new ArrayList<>();
    private Context context;
    double currentLat, currentLng;
    int spinnerPosition;


    public PlaceAdapter(List<Result> placeList, Context context, double currentLat, double currentLng, int spinnerPosition) {

        this.resultList = placeList;
        this.context = context;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.spinnerPosition = spinnerPosition;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_layout, parent, false);

        return new CustomViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        Result employee = resultList.get(position);
        holder.name.setText(employee.getName());
        holder.address.setText(employee.getVicinity());
        holder.rating.setText(String.valueOf(employee.getRating()));

        switch (spinnerPosition){

            case 1:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_airport));
                break;

            }case 2:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_atm));
                break;

            }case 3:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bank));
                break;

            }case 4:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bakery));
                break;

            }case 5:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_lbar));
                break;

            }case 6:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_book_store));
                break;

            }case 7:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bus_station));
                break;

            }case 8:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_card_rental));
                break;

            }case 9:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_card_repair));
                break;

            }case 10:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_lcard_wash));
                break;

            }case 11:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_coffee));
                break;

            }case 12:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clothing));
                break;

            }case 13:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_destist));
                break;

            }case 14:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_doctor));
                break;

            }case 15:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_electronic));
                break;

            }case 16:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_embassy));
                break;

            }case 17:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fire_station));
                break;

            }case 18:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gas));
                break;

            }case 19:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_govt));
                break;

            }case 20:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gym));
                break;

            }case 21:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_hospital));
                break;

            }case 22:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_jwellery));
                break;

            }case 23:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_laundry));
                break;

            }case 24:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_mosque));
                break;

            }case 25:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_movie));
                break;

            }case 26:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_park));
                break;

            }case 27:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pharmecy));
                break;

            }case 28:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_police));
                break;

            }case 29:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_post_office));
                break;

            }case 30:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_restaurant));
                break;

            }case 31:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_school));
                break;

            }case 32:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_shopping));
                break;

            }case 33:{

                holder.placeIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_university));
                break;

            }
        }



        holder.fullItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //https://maps.googleapis.com/maps/api/place/photo?photoreference=PHOTO_REFERENCE&sensor=false&maxheight=MAX_HEIGHT&maxwidth=MAX_WIDTH&key=YOUR_API_KEY

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("nameValue", resultList.get(position).getName());
                intent.putExtra("addressValue", resultList.get(position).getVicinity());
                intent.putExtra("ratingValue", resultList.get(position).getRating());
                intent.putExtra("lat", resultList.get(position).getGeometry().getLocation().getLat());
                intent.putExtra("lng", resultList.get(position).getGeometry().getLocation().getLng());


                intent.putExtra("currentLat", currentLat);
                intent.putExtra("currentLng", currentLng);



                try {

                    intent.putExtra("available", resultList.get(position).getOpeningHours().getOpenNow());
                    intent.putExtra("error", false);

                }catch (Exception e){

                    intent.putExtra("available", false);
                    intent.putExtra("error", true);
                }
                intent.putExtra("placeId", resultList.get(position).getPlaceId());
                context.startActivity(intent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address, rating;
        public LinearLayout fullItem;
        ImageView placeIV;

        public CustomViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.nameTV);
            address = (TextView) view.findViewById(R.id.addressTV);
            rating = (TextView) view.findViewById(R.id.ratingTV);
            fullItem = (LinearLayout) view.findViewById(R.id.fullItem);
            placeIV = (ImageView) view.findViewById(R.id.placeImageView);

        }
    }




}
