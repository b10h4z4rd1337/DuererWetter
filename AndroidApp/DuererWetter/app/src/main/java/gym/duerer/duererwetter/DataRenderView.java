package gym.duerer.duererwetter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class DataRenderView extends View {

    private Paint paint;
    private Path path;
    private long[] dates;
    private Rect bounds = new Rect();

    public DataRenderView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20.0f);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    private float findMax(float[] data) {
        float result = Integer.MIN_VALUE;
        for (Float f : data) {
            result = Math.max(result, f);
        }
        return result;
    }

    public void setData(float[] data, long[] dates) {
        // Make space for Scala
        Rect bounds = new Rect();
        paint.getTextBounds("0", 0, 1, bounds);
        float fontHeight = bounds.height();
        float fontSpacing = fontHeight + 50 * 2;

        this.path = new Path();
        this.dates = dates;
        float deltaX = getWidth() / data.length;
        float deltaY = (getHeight() -  fontSpacing) / findMax(data);

        // Draw Data
        int color = getContext().getResources().getColor(R.color.colorPrimary);
        paint.setColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
        path.moveTo(0, getHeight() - fontSpacing - deltaY * data[0]);
        for (int i = 1; i < data.length; i++) {
            path.lineTo(deltaX * i, getHeight() - fontSpacing - deltaY * data[i]);
        }
        path.lineTo(getWidth(), getHeight() - fontSpacing);
        path.lineTo(0, getHeight() - fontSpacing);
        path.lineTo(0, getHeight() - fontSpacing - deltaY * data[0]);
        path.close();
    }

    private int[] createDateArray(long[] dates) {
        int[] result = new int[dates.length];
        Calendar calendar = Calendar.getInstance();

        int index = 0;
        for (; index < dates.length; index++)
            if (dates[index] != 0)
                break;

        if (index < dates.length) {
            calendar.setTime(new Date(dates[index]));

            for (int i = index; i < dates.length; i++) {
                if (dates.length != 24) {
                    result[i] = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, result[i] + 1);
                } else {
                    result[i] = calendar.get(Calendar.HOUR_OF_DAY);
                    calendar.set(Calendar.HOUR_OF_DAY, result[i] + 1);
                }
            }

            calendar.setTime(new Date(dates[index]));

            for (int i = index; i >= 0; i--) {
                if (dates.length != 24) {
                    result[i] = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, result[i] - 1);
                } else {
                    result[i] = calendar.get(Calendar.HOUR_OF_DAY);
                    calendar.set(Calendar.HOUR_OF_DAY, result[i] - 1);
                }
            }
        }

        boolean empty = true;
        for (Integer i : result)
            if (i != 0)
                empty = false;

        if (empty) {
            calendar.setTime(new Date());
            if (dates.length != 24) {
                calendar.setTime(new Date());
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            } else {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
            }
            for (int i = result.length - 1; i >= 0; i--) {
                if (dates.length != 24) {
                    result[i] = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DAY_OF_MONTH, result[i] - 1);
                } else {
                    result[i] = calendar.get(Calendar.HOUR_OF_DAY);
                    calendar.set(Calendar.HOUR_OF_DAY, result[i] - 1);
                }
            }
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path != null) {
            paint.getTextBounds("0", 0, 1, bounds);
            float fontHeight = bounds.height();
            float fontSpacing = getHeight() - fontHeight - 50 * 2;

            canvas.drawPath(path, paint);

            paint.setColor(Color.BLACK);
            canvas.drawLine(0, fontSpacing, getWidth(), fontSpacing, paint);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5.0f);
            float delta = getWidth() / dates.length;
            paint.getTextBounds("0", 0, 1, bounds);
            float height = getHeight() - fontHeight - 50;

            int[] datesToDraw = createDateArray(dates);

            for (int i = 0; i < datesToDraw.length; i++) {
                String string = String.valueOf(datesToDraw[i]);
                canvas.drawText(string, 0, string.length(), i * delta, height, paint);
            }
        }
    }
}
