package com.fiv.app.profileswitcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by user on 12/7/2016.
 */
public class receiver2 extends BroadcastReceiver {

    Context c;
    LocationUpdateService locationService;
    ServiceConnection serviceConnection;

    AlarmManager mAlarmManager;



    @Override
    public void onReceive(Context context, Intent intent) {

        c=context;

        mAlarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        Log.e("runreceiver", "------------------------------------>");




        Calendar c1 = Calendar.getInstance();

        int currhour=c1.get(Calendar.HOUR_OF_DAY);
        int currmin=c1.get(Calendar.MINUTE);
        String curr_ampm;


        Time t=MapsActivity.convert24hourtoAm_Pm(currhour,currmin);
        currhour = Integer.valueOf(t.getHour());
        currmin = Integer.valueOf(t.getMinute());
        curr_ampm = t.getAm_pm();

        Intent intent1  = new Intent(context,LocationUpdateService.class);
        PinableLocation pinablelocation = (PinableLocation) intent.getExtras().get("pinablelocation");
        if (pinablelocation!=null) {
            int locendtimehour = Integer.valueOf(pinablelocation.getEndTime().hour);
            int locendtimemin = Integer.valueOf(pinablelocation.getEndTime().minute);
            if ((currhour == locendtimehour) && (currmin == locendtimemin) && (curr_ampm.equals(pinablelocation.getEndTime().getAm_pm()))) {
                if (pinablelocation != null) {
                    AudioManager am = (AudioManager) context.getSystemService(c.AUDIO_SERVICE);
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            } else {
                Toast.makeText(context, "setfornextDay2", Toast.LENGTH_SHORT).show();
                setEndAlarmInCaseOfOldTime(pinablelocation);
            }
        }
    }



    void setRepeatingEndingAlarm(PinableLocation pinableLocation)
    {

        Calendar calendar2 = Calendar.getInstance(); // calendar to get ending time in millis
        calendar2.setTimeInMillis(System.currentTimeMillis());


        Time etime=new Time();


        Intent intent1 = new Intent(c,receiver2.class);






        PendingIntent pendingIntent1;


        etime = pinableLocation.getEndTime();
        int etimehour = Integer.valueOf(etime.getHour());
        int etimemin = Integer.valueOf(etime.getMinute());






        //  intent.putExtra("stime", stime.toString());
        intent1.putExtra("pinablelocation",pinableLocation);






        if(etimehour!=12)
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
            calendar2.set(Calendar.AM_PM, Calendar.AM);
        }
        else
        {
            calendar2.set(Calendar.AM_PM, Calendar.PM);
        }




        double idx = pinableLocation.getLatitude()*20000;
        double idy = pinableLocation.getLongitude()*25000;

        int id2 = (int) (idx+idy);


        // intent for all ending time alarms
        pendingIntent1 = PendingIntent.getBroadcast(c,id2,intent1,0);

        mAlarmManager.cancel(pendingIntent1);


        if (Build.VERSION.SDK_INT >= 19) {
            // if(currtime.getTimeInMillis()<calendar1.getTimeInMillis()) {


            //set alarm for ending time of row
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ AlarmManager.INTERVAL_DAY, pendingIntent1);



            Log.e("setAlarmEndInReceiver", "------------------------------------------------------------------------>");


        } else {


            //set alarm for ending time of row
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ AlarmManager.INTERVAL_DAY, pendingIntent1);

        }


    }

    void setEndAlarmInCaseOfOldTime(PinableLocation pinableLocation)
    {
        Calendar calendar2 = Calendar.getInstance(); // calendar to get ending time in millis
        calendar2.setTimeInMillis(System.currentTimeMillis());


        Time etime=new Time();


        Intent intent1 = new Intent(c,receiver2.class);






        PendingIntent pendingIntent1;


        etime = pinableLocation.getEndTime();
        int etimehour = Integer.valueOf(etime.getHour());
        int etimemin = Integer.valueOf(etime.getMinute());






        //  intent.putExtra("stime", stime.toString());
        intent1.putExtra("pinablelocation",pinableLocation);




        if(etimehour!=12)
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
            calendar2.set(Calendar.AM_PM, Calendar.AM);
        }
        else
        {
            calendar2.set(Calendar.AM_PM, Calendar.PM);
        }




        double idx = pinableLocation.getLatitude()*20000;
        double idy = pinableLocation.getLongitude()*25000;

        int id2 = (int) (idx+idy);


        // intent for all ending time alarms
        pendingIntent1 = PendingIntent.getBroadcast(c,id2,intent1,0);

        mAlarmManager.cancel(pendingIntent1);


        if (Build.VERSION.SDK_INT >= 19) {
            // if(currtime.getTimeInMillis()<calendar1.getTimeInMillis()) {


            //set alarm for ending time of row
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis()+ AlarmManager.INTERVAL_DAY, pendingIntent1);



            Log.e("setAlarmEndInReceiver", "------------------------------------------------------------------------>");


        } else {


            //set alarm for ending time of row
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis()+ AlarmManager.INTERVAL_DAY, pendingIntent1);

        }


    }


}
