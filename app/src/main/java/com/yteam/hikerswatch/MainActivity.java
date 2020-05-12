package com.yteam.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;
    TextView altitude;
    TextView latitude;
    TextView longitude;
    TextView accuracy;
    TextView address;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        altitude = findViewById(R.id.altitudeTextView);
        latitude = findViewById(R.id.latitudeTextView);
        longitude = findViewById(R.id.longitudeTextView);
        accuracy = findViewById(R.id.accuracyTextView);
        address = findViewById(R.id.addressTextView);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateFields(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,0,locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateFields(lastLocation);
        }
    }

    public void updateFields(Location location){

        Log.i("Location",location.toString());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            latitude.setText(String.format("%.3f°",location.getLatitude()));

            longitude.setText(String.format("%.3f°",location.getLongitude()));

            if(location.hasAccuracy()){
                accuracy.setText(String.format("%.2fm",location.getAccuracy()));
            }

            if(location.hasAltitude()){
                altitude.setText(String.format("%.4f",location.getAltitude())+"m");
            }

            if(addressList != null && addressList.size() > 0){
                String addressText = "";
                if(addressList.get(0).getThoroughfare()!=null){
                    addressText += addressList.get(0).getThoroughfare() + ",\n";
                }
                if(addressList.get(0).getLocality()!=null){
                    addressText += addressList.get(0).getLocality() + ",\n";
                }
                if(addressList.get(0).getSubAdminArea()!=null){
                    addressText += addressList.get(0).getSubAdminArea();
                }
                address.setText(addressText);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
