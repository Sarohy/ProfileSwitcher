package com.fiv.app.profileswitcher;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private AlarmManager mAlarmManager;
    ListView listView;
    ArrayList<PinableLocation> PinableLocationArrayList;
    FloatingActionButton fab;
    MyAdapter myAdapter;
    int request_code=1;
    RelativeLayout emptylayout;
    EditText password;
    EditText email;
    EditText problem;
    boolean bound = false;
    ServiceConnection serviceConnection = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

                bound = false;

            }
        };

        getSupportActionBar().setElevation(0);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        emptylayout = (RelativeLayout) findViewById(R.id.emptyview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        PinableLocationArrayList = new ArrayList<PinableLocation>();
        listView= (ListView) findViewById(R.id.list_view);
        myAdapter= new MyAdapter(this,PinableLocationArrayList);
        listView.setEmptyView(findViewById(R.id.emptyview2));
        listView.setAdapter(myAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if add is loaded show add else move to sec activity
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("array",PinableLocationArrayList);
                    startActivityForResult(intent,request_code);
            }
        });
        getDataFromDataBase();
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mWifi.isConnected()) {
            builALertMessageNoWifi();
        }
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        stopService(new Intent(getApplication(),MyService.class));
        Intent intent = new Intent(this,MyService.class);
        intent.putExtra("pinablelocations",PinableLocationArrayList);
        startService(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkDrawOverlayPermission();
        Log.e("in main's on resume","------------------------------------------------------------------------>");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode==this.request_code)
            {
                if(resultCode==RESULT_OK)
                {
                    PinableLocation pinablelocation = (PinableLocation) data.getSerializableExtra("pinablelocation");
                    PinableLocationArrayList.add(pinablelocation);
                    myAdapter.notifyDataSetChanged();
                    addToDataBase(pinablelocation);
                    setAlarm(pinablelocation);
                }
            }
            else
            {
                Log.e("onactivity","---------------------------------->");
            }
    }
    void setAlarm(PinableLocation pinableLocation)
    {
        stopService(new Intent(getApplication(),MyService.class));
        Intent intent = new Intent(this,MyService.class);
        intent.putExtra("pinablelocations",PinableLocationArrayList);
        startService(intent);
    }



    void addToDataBase(PinableLocation pinableLocation)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDbHelper.S_TIME,pinableLocation.getStartTime().toString());
        contentValues.put(MyDbHelper.E_TIME,pinableLocation.getEndTime().toString());
        contentValues.put(MyDbHelper.LATITUDE,pinableLocation.getLatitude().toString());
        contentValues.put(MyDbHelper.LONGITUDE,pinableLocation.getLongitude().toString());
        contentValues.put(MyDbHelper.RADIUS,pinableLocation.getRadius());
        contentValues.put(MyDbHelper.LOC_NAME,pinableLocation.getLocation());
       getContentResolver().insert(Uri.parse("content://com.example.user.mapsapplication/"+MyDbHelper.TABLE_NAME),contentValues);
    }

    void getDataFromDataBase()
    {
        String[] col_to_select = {MyDbHelper.S_TIME,MyDbHelper.E_TIME,MyDbHelper.LATITUDE,MyDbHelper.LONGITUDE,MyDbHelper.RADIUS,MyDbHelper.LOC_NAME};
        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.user.mapsapplication/"+MyDbHelper.TABLE_NAME),col_to_select,null,null,null);
        Log.e("cursercount",cursor.getCount()+"------------------------------------------------------>");
        Log.e("curser_col_count",cursor.getColumnCount()+"------------------------------------------------------>");
        while(cursor.moveToNext())
        {
            Log.e("getdatafromdb","------------------------------------------------------>");
            PinableLocation pinableLocation;
            Time stime;
            Time etime;
            String lat;
            String lng;
            int rad;
            String name;
            List<String> timeList = Arrays.asList( cursor.getString(0).split(","));
            stime = new Time(timeList.get(0),timeList.get(1),timeList.get(2));
            timeList = Arrays.asList( cursor.getString(1).split(","));
            etime = new Time(timeList.get(0),timeList.get(1),timeList.get(2));
            lat=cursor.getString(2);
            lng=cursor.getString(3);
            rad=cursor.getInt(4);
            name=cursor.getString(5);
            pinableLocation = new PinableLocation(stime,etime, Double.valueOf(lat), Double.valueOf(lng),rad,name);
            PinableLocationArrayList.add(pinableLocation);
            myAdapter.notifyDataSetChanged();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void builALertMessageNoWifi()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your WIFI seeems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();

    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, 10);
            }
            else
            {
                Log.e("bind","----------------------------------------------------------->");
            }
        }
    }
    @Override
    protected void onPause() {
        Log.e("onPause","--------------------------------------------------------->");
        Log.e("unbind","----------------------------------------------------------->");
        super.onPause();
    }
}
