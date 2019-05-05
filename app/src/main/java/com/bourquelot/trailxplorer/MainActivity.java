package com.bourquelot.trailxplorer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private boolean started;
    private Button startbutton;
    private Chronometer cmTimer;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 99;
    public String current_file_name;
    public File GPSdir;
    private gpxWriter GPX;
    private File importFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        started = false;
        Button importButton = findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
        startbutton = findViewById(R.id.startbutton);
        cmTimer = findViewById(R.id.elapsedtimetext);
        cmTimer.setBase(SystemClock.elapsedRealtime());
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (started) {
                    cmTimer.stop();
                    switchToResults();
                }
                else{
                    createGpxFile();
                    startRecording();
                }
            }
        });
        initializeLocationListener();
        GPSdir = initializeGpsDirectory();
    }

    private void selectFile(){
        final Intent i = new Intent(this, results.class);
        FileChooser fc = new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                gpxParser.parse(file.getAbsolutePath());

                startActivity(i);
            }
        });
        fc.setExtension(".gpx");
        fc.showDialog();
    }

    private void switchToResults(){
        //Stops requesting updates from the locationManager
        locationManager.removeUpdates(locationListener);
        GPX.finishWriting();
        //Switches to the results activity
        Intent i = new Intent(this, results.class);
        startActivity(i);
    }

    private void startRecording(){
        if (checkLocationPermission()) {
            cmTimer.start();
            arraygpx.setlocationArray(new ArrayList<Location>());
            startbutton.setText(R.string.stop_tracking_text);
            started = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }

    }

    private boolean checkLocationPermission(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.message_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    private void initializeLocationListener(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location : ", location.toString());
                //Write the location in the file
                GPX.writeNewLocation(location);
                //Add the location to the array to be passed on
                arraygpx.addlocationArray(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void createGpxFile(){
        //First checks if the external storage is available to save to
        if (!isExternalStorageWritable()){
            throw new Error("External Storage is unavailable");
        }

        //defines the current file name to be the time
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        current_file_name = df.format(c);
        //prints the filename for debugging purposes
        System.out.println(current_file_name);

        //Create a new gpx file in the directory
        File current_gpx_file = new File(GPSdir, current_file_name + ".gpx");
        if(!current_gpx_file.exists()) {
            try {
                current_gpx_file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        //Starts writing to the file

        GPX = new gpxWriter();
        GPX.currentFile = current_gpx_file;
        GPX.startWriting();
    }

    public File initializeGpsDirectory(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
        // Get the directory for the user's GPS files, or creates it if it's not already present
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/GPSTracks/");
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }
    private boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        else{
            return false;
        }
    }
}
