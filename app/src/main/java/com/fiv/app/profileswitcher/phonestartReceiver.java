package com.fiv.app.profileswitcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 12/5/2016.
 */
public class phonestartReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Toast.makeText(context, "RestartPhone", Toast.LENGTH_SHORT).show();

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();

        String[] col_to_select = {MyDbHelper.S_TIME, MyDbHelper.E_TIME, MyDbHelper.LATITUDE, MyDbHelper.LONGITUDE, MyDbHelper.RADIUS, MyDbHelper.LOC_NAME};
        Cursor cursor = contentResolver.query(Uri.parse("content://com.example.user.mapsapplication/" + MyDbHelper.TABLE_NAME), col_to_select, null, null, null);


        Log.e("cursercount", cursor.getCount() + "------------------------------------------------------>");

        Log.e("curser_col_count", cursor.getColumnCount() + "------------------------------------------------------>");


        while (cursor.moveToNext()) {


            Log.e("getdatafromdb", "------------------------------------------------------>");

            PinableLocation pinableLocation;
            Time sstime;
            Time eetime;
            // double lat;
            // double lng;

            String lat;
            String lng;

            int rad;
            String name;

            List<String> timeList = Arrays.asList(cursor.getString(0).split(","));
            sstime = new Time(timeList.get(0), timeList.get(1), timeList.get(2));
            timeList = Arrays.asList(cursor.getString(1).split(","));
            eetime = new Time(timeList.get(0), timeList.get(1), timeList.get(2));

                    /*lat=cursor.getDouble(2);
                    lng=cursor.getDouble(3);*/
            lat = cursor.getString(2);
            lng = cursor.getString(3);
            rad = cursor.getInt(4);
            name = cursor.getString(5);

            // pinableLocation = new PinableLocation(stime,etime,lat,lng,rad,name);
            pinableLocation = new PinableLocation(sstime, eetime, Double.valueOf(lat), Double.valueOf(lng), rad, name);



            Calendar calendar1 = Calendar.getInstance(); // calendar to get starting time in millis
            calendar1.setTimeInMillis(System.currentTimeMillis());



           /* Calendar calendar2 = Calendar.getInstance(); // calendar to get ending time in millis
            calendar2.setTimeInMillis(System.currentTimeMillis());
*/

            Time stime=new Time();
            Time etime=new Time();


            Intent intent11 = new Intent(context,receiver.class);


            //Intent intent1 = new Intent(context,receiver.class);




            PendingIntent pendingIntent;

           // PendingIntent pendingIntent1;



            stime = pinableLocation.getStartTime();
           // etime = pinableLocation.getEndTime();

            int stimehour = Integer.valueOf(stime.getHour());
            int stimemin = Integer.valueOf(stime.getMinute());

          //  int etimehour = Integer.valueOf(etime.getHour());
          //  int etimemin = Integer.valueOf(etime.getMinute());

            //  intent.putExtra("stime", stime.toString());
            intent11.putExtra("pinablelocation",pinableLocation);
         //   intent11.putExtra("which","start");

         //   intent1.putExtra("pinablelocation",pinableLocation);
         //   intent1.putExtra("which","end");





            if(stimehour!=12)
            {
                calendar1.set(Calendar.HOUR, Integer.valueOf(stime.getHour()));
            }
            else
            {
                calendar1.set(Calendar.HOUR, 0);
            }

            calendar1.set(Calendar.MINUTE, Integer.valueOf(stime.getMinute()));
            calendar1.set(Calendar.SECOND,0);
            calendar1.set(Calendar.MILLISECOND,0);
            if (stime.getAm_pm().equals("am")) {
                calendar1.set(Calendar.AM_PM, Calendar.AM);
            } else {
                calendar1.set(Calendar.AM_PM, Calendar.PM);
            }


         /*   if(etimehour!=12)
            {
                calendar2.set(Calendar.HOUR, Integer.valueOf(etime.getHour()));
            }
            else
            {
                calendar2.set(Calendar.HOUR, 0);
            }

            calendar2.set(Calendar.MINUTE, Integer.valueOf(etime.getMinute()));
            calendar2.set(Calendar.SECOND,0);
            calendar2.set(Calendar.MILLISECOND,0);

            if(etime.getAm_pm().equals("am"))
            {
                calendar2.set(Calendar.AM_PM,Calendar.AM);
            }
            else
            {
                calendar2.set(Calendar.AM_PM,Calendar.PM);

            }*/

            Log.e("timeofcal1","h="+calendar1.get(Calendar.HOUR)+"m="+calendar1.get(Calendar.MINUTE)+"ampm="+calendar1.get(Calendar.AM_PM));


           // Log.e("timeofcal2","h="+calendar2.get(Calendar.HOUR)+"m="+calendar2.get(Calendar.MINUTE)+"ampm="+calendar2.get(Calendar.AM_PM));




            double idx =  pinableLocation.getLatitude()*10000;
            double idy = pinableLocation.getLongitude()*15000;

/*

            Log.e("cal1timeinMili",""+calendar1.getTimeInMillis()+"---------------------------------->");
            Log.e("cal2timeinMili",""+calendar2.getTimeInMillis()+"---------------------------------->");
            Log.e("currtimeinmili",""+System.currentTimeMillis()+"----------------------------------->");
*/


            int id = (int) (idx+idy); // ids for all starting time;

            idx = pinableLocation.getLatitude()*20000;
            idy = pinableLocation.getLongitude()*25000;



            int id2 = (int) (idx+idy);



            //intent for all starting time alarms
            pendingIntent = PendingIntent.getBroadcast(context,id, intent11,0) ;


            // intent for all ending time alarms
           // pendingIntent1 = PendingIntent.getBroadcast(context,id2,intent1,0);





            if (Build.VERSION.SDK_INT >= 19) {
                // if(currtime.getTimeInMillis()<calendar1.getTimeInMillis()) {


                // set alarm for starting time of row
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar1.getTimeInMillis(), pendingIntent);

                //set alarm for ending time of row
               // mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis(), pendingIntent1);



                Log.e("setalarm", "------------------------------------------------------------------------>");


            } else {


                mAlarmManager.set(AlarmManager.RTC_WAKEUP,calendar1.getTimeInMillis(), pendingIntent);


                //set alarm for ending time of row
               // mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent1);

            }



        }
    }

}