package charanjit.lufthansa.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

public class Departure {
    public static final Departure EMPTY = new Departure(null);

    @NonNull
    public final JSONObject json;
    @NonNull public final String airportCode;
    @NonNull public final String localSchedueledTime;
    @NonNull public final String schedueledTime;
    @NonNull public final String actualTimeLocal;
    @NonNull public final String timeStatus;
    @NonNull public final String terminal;



    public Departure(@Nullable JSONObject json) {
        this.json = json != null ? json : new JSONObject();

        airportCode = this.json.optString("AirportCode", "");

        // local schedule time
        JSONObject localScheduleJson = this.json.optJSONObject("ScheduledTimeLocal");
        if (localScheduleJson != null) {
            localSchedueledTime = localScheduleJson.optString("DateTime", "");
        }
        else {
            localSchedueledTime = "";
        }

        JSONObject scheduleTimeUTCJson = this.json.optJSONObject("ScheduledTimeUTC");
        if (scheduleTimeUTCJson != null) {
            schedueledTime = scheduleTimeUTCJson.optString("DateTime", "");
        }
        else {
            schedueledTime = "";
        }

        JSONObject actualTimeLocalJson = this.json.optJSONObject("ActualTimeLocal");
        if (actualTimeLocalJson != null) {
            actualTimeLocal = actualTimeLocalJson.optString("DateTime", "");
        }
        else {
            actualTimeLocal = "";
        }
        //Time Status
        JSONObject timeStatusJson = this.json.optJSONObject("TimeStatus");
        if (timeStatusJson != null) {
            timeStatus = timeStatusJson.optString("Definition", "");
        }
        else {
            timeStatus = "";
        }
        JSONObject terminalJson = this.json.optJSONObject("Terminal");
        if (terminalJson != null) {
            terminal = terminalJson.optString("Gate", "");
        }
        else {
            terminal = "";
        }






    }
}