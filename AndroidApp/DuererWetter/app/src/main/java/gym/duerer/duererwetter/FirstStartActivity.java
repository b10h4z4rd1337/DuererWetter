package gym.duerer.duererwetter;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class FirstStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);
    }

    public void writeCompleted() {
        SharedPreferences.Editor editor = getSharedPreferences("default", 0).edit();
        editor.putBoolean("firstCompleted", true);
        editor.apply();
    }

    public void yesButtonClicked(View v) {
        final SharedPreferences.Editor editor = getSharedPreferences("default", 0).edit();
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                editor.putInt("hour", hour).putInt("minute", minute).apply();
                editor.putBoolean("useService", true);
                startService(new Intent(FirstStartActivity.this, SyncService.class));

                writeCompleted();
                FirstStartActivity.this.finish();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Wann?");
        timePickerDialog.show();
    }

    public void noButtonClicked(View v) {
        getSharedPreferences("default", 0).edit().putBoolean("useService", false).apply();
        writeCompleted();
        this.finish();
    }
}
