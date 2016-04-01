package omrkhld.com.ringmdweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private RelativeLayout mainView;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private AsyncTask task;
    private CardAdapter ca;
    private String[] cities = {"Singapore", "Madrid", "New+York", "London", "Montreal", "Johannesburg", "Hong+Kong", "Moscow", "Brussels", "Tokyo",
                                "Seoul", "Mexico+City", "Manila", "Jakarta", "Mumbai", "Los+Angeles", "Cairo", "Karachi", "Paris", "Buenos+Aires",
                                "Beijing", "Istanbul", "Chicago", "Bogota", "Berlin", "Tel+Aviv", "Manchester", "Sydney", "Melbourne", "Taipei"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.card_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasConnection()) {
                    task = new UpdateWeather().execute(cities);
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    Snackbar noInternetBar = Snackbar.make(mainView, "Sync your Fitbit now", Snackbar.LENGTH_LONG);
                    noInternetBar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeRefresh.setRefreshing(true);
                            task = new UpdateWeather().execute(cities);
                        }
                    });
                    noInternetBar.show();
                }
            }
        });

        //Start the refresh animation on first run
        if (hasConnection()) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
        } else {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasConnection()) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                    task = new UpdateWeather().execute(cities);
                }
            });
        } else {
            mSwipeRefresh.setRefreshing(false);
            Snackbar noInternetBar = Snackbar.make(mainView, "No Internet Connection!", Snackbar.LENGTH_LONG);
            noInternetBar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefresh.setRefreshing(true);
                    task = new UpdateWeather().execute(cities);
                }
            });
            noInternetBar.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            // Stop the refreshing indicator
            mSwipeRefresh.setRefreshing(false);
            // Stop the task
            task.cancel(true);
        }
    }

    //Checks if there's internet connection
    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    class UpdateWeather extends AsyncTask<String[],Void,HashMap<String, CardInfo>> {
        JSONParser parser = new JSONParser();

        @Override
        protected HashMap<String, CardInfo> doInBackground(String[]... arg) {
            String baseURL = "http://api.worldweatheronline.com/premium/v1/weather.ashx";
            String keyAPI = "43927746187841c98d0113644163103";
            HashMap<String, String> params = new HashMap<>();
            HashMap<String, CardInfo> cards = new HashMap<String, CardInfo>();
            String cityName = "";
            String dateTime = "";
            String temp = "";
            String feelsLike = "";
            String weather = "";
            String imgUrl = "";
            Bitmap img = null;

            //Iterates through all the 30 hardcoded cities.
            for (int i = 0; i < cities.length; i++) {
                try {
                    //parameters for the API call
                    params.put("key", keyAPI);
                    params.put("q", arg[0][i]);
                    params.put("format", "json");
                    params.put("num_of_days", "1");
                    params.put("fx", "no");
                    params.put("mca", "no");
                    params.put("showlocaltime", "yes");
                    JSONObject json = parser.makeHttpRequest(baseURL, "GET", params);

                    try {
                        //Read the json data returned by the GET request
                        json = json.getJSONObject("data");
                        cityName = json.getJSONArray("request").getJSONObject(0).getString("query").replace("\"", "");
                        String[] cityNameSplit = cityName.split(",");
                        cityName = cityNameSplit[0];

                        JSONObject timeZone = json.getJSONArray("time_zone").getJSONObject(0);
                        dateTime = timeZone.getString("localtime");

                        JSONObject currentCondition = json.getJSONArray("current_condition").getJSONObject(0);
                        temp = currentCondition.getString("temp_C").replace("\"", "");
                        feelsLike = currentCondition.getString("FeelsLikeC").replace("\"", "");
                        weather = currentCondition.getJSONArray("weatherDesc").getJSONObject(0).getString("value").replace("\"", "");
                        imgUrl = currentCondition.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value").replace("\"", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                try {
                    //Get the weather image from the URL in the JSON data
                    img = BitmapFactory.decodeStream((InputStream) new URL(imgUrl).getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                CardInfo card = initCard(cityName, dateTime, temp, feelsLike, weather, img);
                cards.put(cityName, card);
            }
            return cards;
        }

        @Override
        protected void onPostExecute(HashMap<String, CardInfo> cards) {
            ca = updateAdapter(cards);
            if (ca != null) {
                mRecyclerView.setAdapter(ca);
            }
        }
    }

    private CardInfo initCard(String cn, String dt, String t, String fl, String w, Bitmap img) {
        //Create a new card with the information provided
        CardInfo card = new CardInfo(cn, dt, t, fl, w, img);
        return card;
    }

    private CardAdapter updateAdapter(HashMap<String, CardInfo> cards) {
        //Updates adapter for the recycler view with the new hashmap data
        if (cards != null) {
            ca = new CardAdapter(cards);
        }
        mSwipeRefresh.setRefreshing(false);
        return ca;
    }
}
