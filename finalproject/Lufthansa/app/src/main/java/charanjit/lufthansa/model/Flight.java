package charanjit.lufthansa.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import charanjit.lufthansa.model.Departure;
import charanjit.lufthansa.model.Arrival;

import org.json.JSONObject;

public class Flight {
    public static final Flight EMPTY = new Flight(null);

    @NonNull public final JSONObject json;
    @NonNull public final Arrival arrival;
    @NonNull public final Departure departure;



    public Flight(@Nullable JSONObject json) {
        this.json = json != null ? json : new JSONObject();

        JSONObject arrivalJson = this.json.optJSONObject("Arrival");
        arrival = new Arrival(arrivalJson);

        JSONObject departureJson = this.json.optJSONObject("Departure");
        departure = new Departure(departureJson);









    }
}
