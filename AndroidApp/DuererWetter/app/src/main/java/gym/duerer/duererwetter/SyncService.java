package gym.duerer.duererwetter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service {

    private Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleTimer();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            try {
                handleResult(Data.getAndParseData());
                scheduleTimer();
            } catch (Exception e) {
                Toast.makeText(SyncService.this, "Dürer-Wetter: Konnte keine Daten abrufen!", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void scheduleTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("default", 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, sharedPreferences.getInt("minute", 0));
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("hour", 7));

        if (calendar.getTime().getTime() < new Date().getTime()) {
            calendar.add(Calendar.DATE, 1);
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek < Calendar.MONDAY || Calendar.FRIDAY < dayOfWeek) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.add(Calendar.DATE, 3);
        }

        timer = new Timer();
        timer.schedule(timerTask, calendar.getTime());
    }

    private void handleResult(Map<String, String> map) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.title));

        switch (Data.interpretValues(map, this)) {
            case Data.COLD:
                notificationBuilder.setContentText(getString(R.string.kalt));
                notificationBuilder.setSmallIcon(R.drawable.schnee_flocke);
                break;
            case Data.WARM:
                notificationBuilder.setContentText(getString(R.string.warm));
                notificationBuilder.setSmallIcon(R.drawable.sonne);
                break;
            case Data.RAIN:
                notificationBuilder.setContentText(getString(R.string.regen));
                notificationBuilder.setSmallIcon(R.drawable.regen);
                break;
            default:
                return;
        }

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public class Binder extends android.os.Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }

    private final Binder binder = new Binder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
