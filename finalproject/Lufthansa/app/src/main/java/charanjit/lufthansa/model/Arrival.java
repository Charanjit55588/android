package charanjit.lufthansa.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

public class Arrival {
    public static final Arrival EMPTY = new Arrival(null);

    @NonNull public final JSONObject json;
    @NonNull public final String aairportCode;
    @NonNull public final String aschedueledTimeLocal;
    @NonNull public final String aactualTimeLocal;
    @NonNull public final String atimeStatus;
    @NonNull public final String aterminal;





    public Arrival(@Nullable JSONObject json) {
        this.json = json != null ? json : new JSONObject();

        aairportCode = this.json.optString("AirportCode", "");






        // local schedule time
        JSONObject scheduledTimeLocalJson = this.json.optJSONObject("ScheduledTimeLocal");
        if (scheduledTimeLocalJson != null) {
            aschedueledTimeLocal = scheduledTimeLocalJson.optString("DateTime", "");
        }
        else {
            aschedueledTimeLocal = "";
        }


        JSONObject aactualTimeLocalJson = this.json.optJSONObject("ActualTimeLocal");
        if (scheduledTimeLocalJson != null) {
            aactualTimeLocal = aactualTimeLocalJson.optString("DateTime", "");
        }
        else {
            aactualTimeLocal = "";
        }

        JSONObject atimeStatusJson = this.json.optJSONObject("TimeStatus");
        if (atimeStatusJson != null) {
            atimeStatus = atimeStatusJson.optString("Definition", "");
        }
        else {
            atimeStatus = "";
        }

        JSONObject aterminalJson = this.json.optJSONObject("Terminal");
        if (atimeStatusJson != null) {
            aterminal = aterminalJson.optString("Gate", "");
        }
        else {
            aterminal = "";
        }


    }
}
