package omrkhld.com.ringmdweather;

import android.graphics.Bitmap;

/**
 * Created by Omar on 29/3/2016.
 */
public class CardInfo {
    protected String cityName, dateTime, temp, feelsLike, weather;
    protected Bitmap weatherImg;

    public CardInfo(String cn, String dt, String t, String fl, String w, Bitmap img) {
        this.cityName = cn;
        this.dateTime = dt;
        this.temp = t;
        this.feelsLike = fl;
        this.weather = w;
        this.weatherImg = img;
    }
}
