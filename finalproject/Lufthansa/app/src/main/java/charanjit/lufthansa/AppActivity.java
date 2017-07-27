package charanjit.lufthansa;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;

import charanjit.lufthansa.model.Flight;


public class AppActivity extends AppCompatActivity {
    public static final String NAME = AppCompatActivity.class.getSimpleName();

    private static final String CACHED_GAME_TIME_KEY = "cachedGameTime";
    private static final String GAME_KEY = "game";

    private static final class DownloadJsonTask extends AsyncTask<String, Void, Flight> {
        @NonNull
        private final WeakReference<AppActivity> appActivityWeakReference;

        private DownloadJsonTask(@NonNull AppActivity appActivity) {
            appActivityWeakReference = new WeakReference<>(appActivity);
        }

        @Override
        protected Flight doInBackground(final String... urls) {
            // read the first url
            String urlString = urls != null && urls.length > 0 ? urls[0] : "";

            if (TextUtils.isEmpty(urlString)) {
                // supplied url is empty or null
                return null;
            }

            InputStream inputStream;
            Flight flight = Flight.EMPTY;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.addRequestProperty("Accept", "application/json");
                connection.addRequestProperty("X-Originating-Ip", "66.46.18.162");
                connection.setRequestProperty("Authorization", "Bearer nfk9zxz562ncwcfv52nsje5c");
                connection.connect();

                int statusCode = connection.getResponseCode();
                String statusMessage = connection.getResponseMessage();

                Log.w("DownloadWebsiteTask2", "statusCode=" + statusCode + " and message=" + statusMessage);

                // get stream from the remote place
                inputStream = connection.getInputStream();

                // convert steram to text
                String jsonString = new Scanner(inputStream, "UTF-8")
                        .useDelimiter("\\A")
                        .next();

                Log.w("text", "jsonString" + jsonString);

                // close the stream
                inputStream.close();

                // finally disconnect
                connection.disconnect();

                JSONObject json = new JSONObject(jsonString);
                JSONObject flightStatusJson = json.optJSONObject("FlightStatusResource");
                if (flightStatusJson != null) {
                    JSONObject flightsJson = flightStatusJson.optJSONObject("Flights");
                    if (flightsJson != null) {
                        JSONObject flightJson = flightsJson.optJSONObject("Flight");
                        flight = new Flight(flightJson);
                        // thats were we start fectching data
                    }
                }
            } catch (IOException | JSONException e) {
                flight = Flight.EMPTY;
            }

            return flight;
        }

        @Override
        protected void onPostExecute(@NonNull Flight flight) {
            if (appActivityWeakReference.get() != null && appActivityWeakReference.get().isValid()) {
                // activity is fine, call the UI update method
                appActivityWeakReference.get().updateCache(flight);
            }
        }
    }

    private TextView label;
    private TextView departure;
    private TextView airportCode;
    private TextView ScheduledTimeLocal;
    private TextView ActualTimeLocal;
    private TextView TimeStatus;
    private TextView Terminal;

    private TextView arrival;
    private TextView aairportCode;
    private TextView aScheduledTimeLocal;
    private TextView aActualTimeLocal;
    private TextView aTimeStatus;
    private TextView aTerminal;


    private ImageView locationImage;
    private ImageView locationURL;
    private TextView locationText;
    private TextView type;

    private TextView visitingScore;
    private ImageView visitingImage;

    private TextView visitingStats;

    private TextView homeScore;
    private ImageView homeImage;
    private TextView homeName;
    private TextView homeStats;
    private TextView status;
    private TextView message;
    private TextView limit;
    private EditText firstName;


    private DownloadJsonTask downloadJsonTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        label = (TextView) findViewById(R.id.label);
        departure = (TextView) findViewById(R.id.departure);

        airportCode = (TextView) findViewById(R.id.AirportCode);
        ScheduledTimeLocal = (TextView) findViewById(R.id.ScheduledTimeLocal);
        ActualTimeLocal = (TextView) findViewById(R.id.ActualTimeLocal);
        TimeStatus = (TextView) findViewById(R.id.TimeStatus);
        Terminal = (TextView) findViewById(R.id.Terminal);

        arrival = (TextView) findViewById(R.id.arrival);

        aairportCode = (TextView) findViewById(R.id.aAirportCode);
        aScheduledTimeLocal = (TextView) findViewById(R.id.aScheduledTimeLocal);
        aActualTimeLocal = (TextView) findViewById(R.id.aActualTimeLocal);
        aTimeStatus = (TextView) findViewById(R.id.aTimeStatus);
        aTerminal = (TextView) findViewById(R.id.aTerminal);
        locationImage = (ImageView) findViewById(R.id.locationImage);


        //firstName = (EditText) findViewById(R.id.firstName);
        //String t = firstName.getText().toString();


        //Log.w("DownloadWebsiteTask3", "statusCode=" + t);

		/*
		locationImage = (ImageView) findViewById(R.id.locationImage);
		locationText = (TextView) findViewById(R.id.locationText);
		type = (TextView) findViewById(R.id.type);

		visitingScore = (TextView) findViewById(R.id.visitingScore);
		visitingImage = (ImageView) findViewById(R.id.visitingImage);

		visitingStats = (TextView) findViewById(R.id.visitingStats);

		homeScore = (TextView) findViewById(R.id.homeScore);
		homeImage = (ImageView) findViewById(R.id.homeImage);
		homeName = (TextView) findViewById(R.id.homeName);
		homeStats = (TextView) findViewById(R.id.homeStats);
		status = (TextView) findViewById(R.id.status);
		message = (TextView) findViewById(R.id.message);
		limit = (TextView) findViewById(R.id.limit);
		*/

        String dataJsonUrl = "https://api.lufthansa.com/v1/operations/flightstatus/LH200/2017-07-17";
        //LH600
        //LH200
        int cacheExpiration = 60000;

        SharedPreferences sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        long now = System.currentTimeMillis();
        long cachedTime = sharedPreferences.getLong(CACHED_GAME_TIME_KEY, 0L);

        Flight cachedFlight;

        try {
            String gameString = sharedPreferences.getString(GAME_KEY, "{}");
            JSONObject flightJson = new JSONObject(gameString);
            cachedFlight = new Flight(flightJson);
        } catch (JSONException e) {
            // not interested
            e.printStackTrace();
            cachedFlight = null;
        }

        //&& !cachedFlight.isEmpty I removed from the line below

        if (cachedFlight != null
                && now - cachedTime < cacheExpiration) {
            // we have a safe cache, let's use it
            updateUi(cachedFlight, true);
        } else {
            downloadJsonTask = new DownloadJsonTask(this);
            downloadJsonTask.execute(dataJsonUrl);
        }
    }

    @Override
    protected void onDestroy() {
        if (downloadJsonTask != null) {
            downloadJsonTask.cancel(true);
            downloadJsonTask = null;
        }

        super.onDestroy();
    }

    /**
     * Called from the second thread
     *
     * @param flight flight
     */
    private void updateCache(@NonNull Flight flight) {
        if (flight.json.length() > 0) {
            // valid game - sotre it into shared preferences
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putLong(CACHED_GAME_TIME_KEY, System.currentTimeMillis())
                    .putString(GAME_KEY, flight.json.toString())
                    .apply();
        } else {
            // not a valid game - check do we have something old from the cache
            String flightString
                    = getSharedPreferences(NAME, MODE_PRIVATE).getString(GAME_KEY, "{}");
            try {
                JSONObject flightJson = new JSONObject(flightString);
                flight = new Flight(flightJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateUi(flight, false);
    }

    private void updateUi(@Nullable Flight flight, boolean isFromCache) {
        String labelValue = isFromCache ? "Loaded from cache" : "Loaded from network";
        label.setText("Lufthansa Online Flight Status " + labelValue);

        if (flight != null) {
            // valid game, update UI

            //message.setText(flight.);
            departure.setText("DEPARTURE");
            airportCode.setText("Airport Code: " + flight.departure.airportCode);
            ScheduledTimeLocal.setText("Scheduled Time Local: " + flight.departure.localSchedueledTime);
            ActualTimeLocal.setText("Actual Time Local: " + flight.departure.actualTimeLocal);
            TimeStatus.setText("Time Status: " + flight.departure.timeStatus);
            Terminal.setText("Terminal: " + flight.departure.terminal);

            arrival.setText("ARRIVAL");
            aairportCode.setText("Airport Code: " + flight.arrival.aairportCode);
            aScheduledTimeLocal.setText("Scheduled Time Local: " + flight.arrival.aschedueledTimeLocal);
            aActualTimeLocal.setText("Actual Time Local: " + flight.arrival.aactualTimeLocal);
            aTimeStatus.setText("Time Status: " + flight.arrival.atimeStatus);
            aTerminal.setText("Terminal: " + flight.arrival.aterminal);


            final String imageUrl = "http://www.lufthansa.com/mediapool/jpg/45/media_789050645.jpg";
            //final String imageUrl = getString(R.string.imageurl1);

            Glide.with(AppActivity.this)
                    .load(imageUrl)
                    .into(locationImage);


            //type.getText(); EXAMPLE OF HOW YOU CAN GET THE TEXT FROM UI
            //type.setText(flight.type);
			/*
			Glide.with(this)
				.load(flight.locationImageUrl)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into(locationImage);

			visitingScore.setText("" + game.visitingScore);
			visitingName.setText(game.visitingName);
			visitingStats.setText(game.visitingLeagueRank + " " + game.visitingConferenceName);
			Glide.with(this)
				.load(game.visitingImageUrl)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into(visitingImage);

			homeScore.setText("" + game.homeScore);

			homeStats.setText(game.homeLeagueRank + " " + game.homeConferenceName);
			Glide.with(this)
				.load(game.homeImageUrl)
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.into(homeImage);
				*/
            //status.setText(flight.status);
        } else {
            // bad game, show a toast and reset any UI
            //locationImage.setImageBitmap(null);
            airportCode.setText("");
            ScheduledTimeLocal.setText("");
            ActualTimeLocal.setText("");
            TimeStatus.setText("");
            Terminal.setText("");
            departure.setText("");

            arrival.setText("");
            airportCode.setText("");
            aScheduledTimeLocal.setText("");
            aActualTimeLocal.setText("");
            aTimeStatus.setText("");
            aTerminal.setText("");


            //type.setText("");
            //status.setText("");
			/*

			visitingScore.setText("");
			visitingImage.setImageBitmap(null);
			visitingName.setText("");
			visitingStats.setText("");

			homeScore.setText("");
			homeImage.setImageBitmap(null);

			homeStats.setText("");
			*/
        }
    }

    private boolean isValid() {
        return !isDestroyed() && !isFinishing();
    }
}