package gym.duerer.duererwetter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PastFragment extends Fragment {

    private int time;

    public void setTime(final int time) {
        this.time = time;
    }

    private void execute() {
        final AppCompatActivity context = (AppCompatActivity) getActivity();

        String subTitle;
        Resources res = context.getResources();

        switch (time) {
            case 1:
                subTitle = res.getString(R.string.drawer24);
                break;
            case 7:
                subTitle = res.getString(R.string.drawerWeek);
                break;
            case 14:
                subTitle = res.getString(R.string.drawerWeek2);
                break;
            case 30:
                subTitle = res.getString(R.string.drawerMonth);
                break;
            default:
                subTitle = "";
                break;
        }

        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null)
            actionBar.setSubtitle(subTitle);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Data.WeatherObject[] data = Data.parseJSONArray(Data.getData(time));
                    final float[] extract = new float[data.length];
                    final long[] dates = new long[data.length];
                    for (int i = 0; i < data.length; i++) {
                        extract[i] = data[i].temperature;
                        dates[i] = data[i].date;
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DataRenderView dataRenderView = (DataRenderView) getView();
                            if (dataRenderView != null) {
                                dataRenderView.setData(extract, dates);
                                dataRenderView.invalidate();
                            }
                        }
                    });
                } catch (final Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Konnte keine Daten abrufen! (" + e.getMessage() + ")", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        execute();
        DataRenderView dataRenderView = new DataRenderView(getActivity());
        dataRenderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return dataRenderView;
    }


}
