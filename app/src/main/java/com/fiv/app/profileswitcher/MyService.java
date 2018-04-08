package com.fiv.app.profileswitcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(),"Service Started", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras()!=null) {
            final ArrayList<PinableLocation> pinableLocations = (ArrayList<PinableLocation>) intent.getExtras().get("pinablelocations");
            final Handler[] handlers = new Handler[pinableLocations.size()];
            final Runnable[] r = new Runnable[pinableLocations.size()];
            for (int i = 0; i < pinableLocations.size(); i++) {
                handlers[i] = new Handler();
                final int finalI = i;
                r[i] = new Runnable() {
                    public void run() {
                        final Intent in = new Intent(getApplication(), receiver.class);
                        in.putExtra("pinablelocation", pinableLocations.get(finalI));
                        getApplicationContext().sendBroadcast(in);
                    }
                };
                handlers[i].postDelayed(r[i], getStartTimeRemaining(pinableLocations.get(i)));
            }
            final Handler[] endHandlers = new Handler[pinableLocations.size()];
            final Runnable[] endR = new Runnable[pinableLocations.size()];
            for (int i = 0; i < pinableLocations.size(); i++) {
                endHandlers[i] = new Handler();
                final int finalI = i;
                endR[i] = new Runnable() {
                    public void run() {
                        final Intent in = new Intent(getApplication(), receiver2.class);
                        in.putExtra("pinablelocation", pinableLocations.get(finalI));
                        getApplicationContext().sendBroadcast(in);
                    }
                };
                endHandlers[i].postDelayed(endR[i], getEndTimeRemaining(pinableLocations.get(i)));
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    long getStartTimeRemaining(PinableLocation pinableLocation){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Time time;
        time = pinableLocation.getStartTime();
        int stimehour = Integer.valueOf(time.getHour());
        if(stimehour!=12)
        {
            cal.set(Calendar.HOUR, Integer.valueOf(time.getHour()));
        }
        else
        {
            cal.set(Calendar.HOUR, 0);
        }
        cal.set(Calendar.MINUTE, Integer.valueOf(time.getMinute()));
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        if (time.getAm_pm().equals("am")) {
            cal.set(Calendar.AM_PM, Calendar.AM);
        } else {
            cal.set(Calendar.AM_PM, Calendar.PM);
        }
        Log.e("timeofcal1","h="+cal.get(Calendar.HOUR)+"m="+cal.get(Calendar.MINUTE)+"ampm="+cal.get(Calendar.AM_PM));
        long f = cal.getTimeInMillis();
        long h= System.currentTimeMillis();
        if (f>=h)
            return f-h;
        else {
            cal.add(Calendar.DATE,1);
            f=cal.getTimeInMillis();
            return f-h;
        }
    }
    long getEndTimeRemaining(PinableLocation pinableLocation){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Time time;
        time = pinableLocation.getEndTime();
        int stimehour = Integer.valueOf(time.getHour());
        if(stimehour!=12)
        {
            cal.set(Calendar.HOUR, Integer.valueOf(time.getHour()));
        }
        else
        {
            cal.set(Calendar.HOUR, 0);
        }
        cal.set(Calendar.MINUTE, Integer.valueOf(time.getMinute()));
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        if (time.getAm_pm().equals("am")) {
            cal.set(Calendar.AM_PM, Calendar.AM);
        } else {
            cal.set(Calendar.AM_PM, Calendar.PM);
        }
        Log.e("timeofcal1","h="+cal.get(Calendar.HOUR)+"m="+cal.get(Calendar.MINUTE)+"ampm="+cal.get(Calendar.AM_PM));
        long f = cal.getTimeInMillis();
        long h= System.currentTimeMillis();
        if (f>=h)
            return f-h;
        else {
            cal.add(Calendar.DATE,1);
            f=cal.getTimeInMillis();
            return f-h;
        }
    }

    void turnToNormal(final PinableLocation pinableLocation){
        Calendar calendar1 = Calendar.getInstance(); // calendar to get starting time in millis
        calendar1.setTimeInMillis(System.currentTimeMillis());
        Time stime;
        stime = pinableLocation.getEndTime();
        int stimehour = Integer.valueOf(stime.getHour());
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
        Log.e("timeofcal1","h="+calendar1.get(Calendar.HOUR)+"m="+calendar1.get(Calendar.MINUTE)+"ampm="+calendar1.get(Calendar.AM_PM));
        Toast.makeText(getApplicationContext(), "setstopalarm", Toast.LENGTH_SHORT).show();
        long f = calendar1.getTimeInMillis();
        long h= System.currentTimeMillis();
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                Intent in=new Intent(getApplication(),receiver2.class);
                in.putExtra("pinablelocation",pinableLocation);
                getApplicationContext().sendBroadcast(in);
                stopForeground(false);

            }
        };
        handler.postAtTime(r,h-f);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
