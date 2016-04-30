package com.isabellegeorge.pinballwizard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Epicodus on 4/29/16.
 */
public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.LocationsViewHolder>{
    private ArrayList<Location> locations = new ArrayList<>();
    private Context c;

    public LocationsListAdapter(Context c, ArrayList<Location> locations){
        this.locations = locations;
        this.c = c;
        Log.v("HEREHERE", locations+"");
    }

    @Override
    public LocationsListAdapter.LocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LocationsViewHolder viewHolder = new LocationsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationsListAdapter.LocationsViewHolder holder, int position){
        holder.bindLocation(locations.get(position));
    }

    @Override
    public int getItemCount(){
        return locations.size();
    }


    public class LocationsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.locationName) TextView locationName;
        @Bind(R.id.locationType) TextView locationType;
        @Bind(R.id.numberMachines) TextView numberMachines;
        private Context c;

        public LocationsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            c = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getLayoutPosition();
                    Intent i = new Intent(c, LocationDetailActivity.class);
                    //putExtras
                    c.startActivity(i);
                }
            });
        }


         public void bindLocation(Location location){
             int machineNumber = 0;
            locationName.setText(location.getLocationName());
            locationType.setText(location.getLocationType());
             for(int i=0; i<location.getMachineConditions().size(); i++){
                 machineNumber++;
             }
            numberMachines.setText(machineNumber + " Machines");
        }
    }
}
