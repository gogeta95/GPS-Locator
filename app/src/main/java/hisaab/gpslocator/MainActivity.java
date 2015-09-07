package hisaab.gpslocator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final NumberPicker resolution= (NumberPicker) findViewById(R.id.duration);
        resolution.setMaxValue(120);
        resolution.setMinValue(1);
        final NumberPicker duration= (NumberPicker) findViewById(R.id.resolution);
        duration.setMaxValue(600);
        duration.setMinValue(1);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GPSLocator.class);
                //Convert To milliseconds
                intent.putExtra("resolution", resolution.getValue() * 1000);
                intent.putExtra("duration", duration.getValue() * 60 * 1000);
                Toast.makeText(MainActivity.this, intent.getExtras().toString(),Toast.LENGTH_SHORT).show();
                startService(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), 1);
            dialog.setCancelable(false);
            dialog.show();
        }

    }

}
