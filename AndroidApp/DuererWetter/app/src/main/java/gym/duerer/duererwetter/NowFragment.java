package gym.duerer.duererwetter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class NowFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AppCompatActivity context = (AppCompatActivity) getActivity();

        SharedPreferences sharedPreferences = context.getSharedPreferences("default", 0);

        if (sharedPreferences.getBoolean("useService", false)) {
            context.startService(new Intent(context, SyncService.class));
        }

        if (!sharedPreferences.getBoolean("firstCompleted", false)) {
            startActivity(new Intent(context, FirstStartActivity.class));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> temp = Data.getAndParseData();
                    Date date = new Date(Long.parseLong(temp.get("date")) * 1000);
                    final String dateString = SimpleDateFormat.getDateTimeInstance().format(date);

                    final Map<String, String> map = Data.renameEntries(temp);
                    final int picture;

                    switch (Data.interpretValues(temp, context)) {
                        case Data.COLD:
                            picture = R.drawable.snow;
                            break;
                        case Data.WARM:
                            picture = R.drawable.sonne_symbol;
                            break;
                        case Data.RAIN:
                            picture = R.drawable.rain;
                            break;
                        default:
                            picture = R.drawable.normal;
                    }

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageView imageView = (ImageView) context.findViewById(R.id.wheaterImageView);
                            imageView.setImageResource(picture);

                            ActionBar actionBar = context.getSupportActionBar();
                            if (actionBar != null)
                                actionBar.setSubtitle("Aktuell (" + dateString + ")");

                            TextView temperatureText = (TextView) context.findViewById(R.id.tempTextView);
                            temperatureText.setText(map.get("Temperatur") + "°C");
                            map.remove("Temperatur");

                            GridView gridview = (GridView) context.findViewById(R.id.gridView);
                            gridview.setAdapter(new InformationAdapter(map));
                        }
                    });
                } catch (final Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Dürer-Wetter: Konnte keine Daten abrufen! (" + e.getMessage() + ")", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private class InformationAdapter extends BaseAdapter {

        private String[] descriptors;
        private String[] values;

        public InformationAdapter(Map<String, String> map) {
            this.descriptors = map.keySet().toArray(new String[map.size()]);
            this.values = map.values().toArray(new String[map.size()]);
        }

        @Override
        public int getCount() {
            return values.length * 2;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }

            final TextView text = (TextView) view.findViewById(android.R.id.text1);

            if (i % 2 == 1) {
                text.setText(values[i / 2]);
            } else {
                text.setText(descriptors[i / 2]);
            }

            return view;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now, container, false);
    }


}
