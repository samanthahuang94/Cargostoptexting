package com.safevigilance.customlockscreenexample;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;


public class MainScreen extends Activity {

    private LocationManager lm;
    private LocationListener ll;
    double mySpeed, maxSpeed;

    //Disabling the hard buttons
    @Override
    public void onBackPressed() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main_screen);

        Button UnlockButton = (Button) findViewById(R.id.UnlockButton);
        UnlockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        final TextView msg = (TextView) findViewById(R.id.currSpeedText);
        final TextView kph = (TextView) findViewById(R.id.LocationServicesError2);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new SpeedoActionListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        double speed;

        Intent serviceIntent;
        if (isMyServiceRunning(MyService.class)) {
            Log.d("RUNNING?", "YES");
        } else {
            Log.d("RUNNING?", "NO");
            serviceIntent = new Intent(this, MyService.class);
            startService(serviceIntent);

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;

            }
        }
        return false;
    }

    private class SpeedoActionListener implements LocationListener

    {


        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                if (location.hasSpeed()) {
                    final TextView kph = (TextView) findViewById(R.id.LocationServicesError2);
                    mySpeed = location.getSpeed() * 3.6;
                    kph.setText("\nCurrent speed: " + String.format("%.2f", mySpeed) + " km/h, \nMax speed: " + String.format("%.2f", maxSpeed) + " km/h");
                    if (mySpeed >= 1) {
                        final Button UnlockButton = (Button) findViewById(R.id.UnlockButton);

                        UnlockButton.setVisibility(View.GONE);

                    } else if (mySpeed < 1) {
                        final Button UnlockButton = (Button) findViewById(R.id.UnlockButton);

                        UnlockButton.setVisibility(View.VISIBLE);

                    }
                    final TextView ErrorMessage = (TextView) findViewById(R.id.LocationServicesError);
                    ErrorMessage.setVisibility(View.GONE);
                    final TextView ErrorMessage2 = (TextView) findViewById(R.id.LocationServicesError3);
                    ErrorMessage2.setVisibility(View.GONE);
                }
            }
        }


        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle
                extras) {
            // TODO Auto-generated method stub

        }
    }
}

