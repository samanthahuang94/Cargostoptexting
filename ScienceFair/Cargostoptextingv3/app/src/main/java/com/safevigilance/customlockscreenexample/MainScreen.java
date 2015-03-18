package com.safevigilance.customlockscreenexample;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.SettingInjectorService;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Button;

import com.safevigilance.customlockscreenexample.HomeLocker;

public class MainScreen extends Activity{

    private LocationManager lm;
    private LocationListener ll;
    private HomeLocker mHomeLocker;

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

        mHomeLocker = new HomeLocker();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomeLocker.unlock();
        mHomeLocker = null;
    }

    public void showAlert(View view){
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        AlertDialog.Builder alert = myAlert;
        alert.setMessage("To unlock your phone we need to acquire the speed of your device\n");
        alert.setTitle("Please turn on High Accuracy location services");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setPositiveButton("Give Permission", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }); AlertDialog alertDialog = alert.create();
        myAlert.show();

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

