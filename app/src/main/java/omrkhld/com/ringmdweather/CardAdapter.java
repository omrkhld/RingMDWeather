package omrkhld.com.ringmdweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Omar on 29/3/2016.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private HashMap<String, CardInfo> cardList;
    private String[] cities = {"Singapore", "Madrid", "New York", "London", "Montreal", "Johannesburg", "Hong Kong", "Moscow", "Brussels", "Tokyo",
            "Seoul", "Mexico City", "Manila", "Jakarta", "Mumbai", "Los Angeles", "Cairo", "Karachi", "Paris", "Buenos Aires",
            "Beijing", "Istanbul", "Chicago", "Bogota", "Berlin", "Tel Aviv", "Manchester", "Sydney", "Melbourne", "Taipei"};

    public CardAdapter(HashMap<String, CardInfo> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
        final CardInfo ci = cardList.get(cities[i]);
        cardViewHolder.vCityName.setText(ci.cityName);
        cardViewHolder.vDateTime.setText(ci.dateTime);
        cardViewHolder.vTemp.setText(ci.temp);
        cardViewHolder.vFeelsLike.setText("Feels like " + ci.feelsLike);
        cardViewHolder.vWeather.setText(ci.weather);
        cardViewHolder.vWeatherImg.setImageBitmap(ci.weatherImg);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_city, viewGroup, false);

        CardViewHolder viewHolder = new CardViewHolder(itemView);
        return viewHolder;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardView;
        protected TextView vCityName;
        protected TextView vDateTime;
        protected TextView vTemp;
        protected TextView vFeelsLike;
        protected TextView vWeather;
        protected ImageView vWeatherImg;

        public CardViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            vCityName = (TextView) v.findViewById(R.id.city_name);
            vDateTime = (TextView) v.findViewById(R.id.date_time);
            vTemp = (TextView) v.findViewById(R.id.temperature);
            vFeelsLike = (TextView) v.findViewById(R.id.feels_like);
            vWeather = (TextView) v.findViewById(R.id.weather);
            vWeatherImg = (ImageView) v.findViewById(R.id.weather_img);
        }
    }
}
