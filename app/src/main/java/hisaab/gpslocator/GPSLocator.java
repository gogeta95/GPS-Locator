package hisaab.gpslocator;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;


public class GPSLocator extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private int duration;
    private int resolution;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            duration = intent.getIntExtra("duration", 4);
            resolution = intent.getIntExtra("resolution", 7);
            mGoogleApiClient.connect();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Started!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return START_STICKY;
    }

    /*protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            duration = intent.getIntExtra("duration", 4);
            resolution = intent.getIntExtra("resolution", 7);
            mGoogleApiClient.connect();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Started!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }*/


    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest().setInterval(resolution)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(resolution > 5000 ? resolution / 2 : resolution);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                    SystemClock.sleep(duration);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, GPSLocator.this);
                    stopSelf();

            }
        }).start();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
           Toast.makeText(GPSLocator.this,"Connected!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("asas", "suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("sasas", "FAiled");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GPSLocator.this, "Destroyed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(final Location location) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSONObject json= new JSONObject();
                        json.put("device_id", Settings.Secure.getString(GPSLocator.this.getContentResolver(), Settings.Secure.ANDROID_ID));
                        json.put("time", Calendar.getInstance().getTime());
                        json.put("latitude", location.getLatitude());
                        json.put("longitude", location.getLongitude());
                        URL url= new URL("http://192.168.1.101/Hisaab/hisaab.php");
                        final HttpURLConnection  conn= (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setFixedLengthStreamingMode(json.toString().getBytes().length);
                        conn.getOutputStream().write(json.toString().getBytes());
                        conn.getOutputStream().flush();
                        conn.disconnect();
                    }
                    catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

    }
}
