package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by kathleenbenavides on 2/18/16.
 */
public class FootballAppWidgetProvider extends AppWidgetProvider {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int NUMDAYS = 5;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName widget = new ComponentName(context, FootballAppWidgetProvider.class);
        int[] allIds = appWidgetManager.getAppWidgetIds(widget);

        for(int widgetID : allIds) {
            //Create list to add views
            List<String> masterGames = new ArrayList<>();

            //Create intent to launch Activity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            //Get layout for app widget and attach on click listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

            //Grab data for 5 days and add to master list
            for(int day = 0; day < NUMDAYS; day++) {
                Date date = new Date(System.currentTimeMillis() - ((day-2) * 86400000));
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.simple_date_format));
                String[] dateString = new String[1];
                dateString[0] = sdf.format(date);

                //Make call to get data from db
                CursorLoader loader = new CursorLoader(context, DatabaseContract.scores_table.buildScoreWithDate(),
                        null,null,dateString,null);
                Cursor data = loader.loadInBackground();

                //Add data to master list for display
                if(data != null && data.getCount() > 0 ) {
                    data.moveToFirst();
                    for(int i = 0; i < data.getCount(); i++) {
                        if(Integer.valueOf(data.getString(COL_AWAY_GOALS)) == -1 && Integer.valueOf(data.getString(COL_HOME_GOALS)) == -1) {
                            masterGames.add(data.getString(COL_DATE) + " - " + data.getString(COL_MATCHTIME) + " -- " + data.getString(COL_HOME) + " vs " +
                                    data.getString(COL_AWAY));
                        } else {
                            masterGames.add(data.getString(COL_HOME) + " -- " + data.getString(COL_HOME_GOALS) + " vs " +
                                    data.getString(COL_AWAY) + " -- " + data.getString(COL_AWAY_GOALS));
                        }
                        data.moveToNext();
                    }
                    data.close();
                }
            }

            //Set the data for the view
            views.setTextViewText(R.id.game1, masterGames.size()>0 ? masterGames.get(0) : "");
            views.setTextViewText(R.id.game2, masterGames.size()>1 ? masterGames.get(1) : "");
            views.setTextViewText(R.id.game3, masterGames.size()>2 ? masterGames.get(2) : "");
            views.setTextViewText(R.id.game4, masterGames.size()>3 ? masterGames.get(3) : "");
            views.setTextViewText(R.id.game5, masterGames.size()>4 ? masterGames.get(4) : "");

            //AppWidgetManager performs update on current widget
            appWidgetManager.updateAppWidget(widgetID, views);
        }


    }


}


