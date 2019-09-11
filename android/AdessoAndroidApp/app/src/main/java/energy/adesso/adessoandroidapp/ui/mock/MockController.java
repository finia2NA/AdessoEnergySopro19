package energy.adesso.adessoandroidapp.ui.mock;

import android.graphics.Bitmap;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import energy.adesso.adessoandroidapp.logic.controller.MainController;
import energy.adesso.adessoandroidapp.logic.model.MeterKind;
import energy.adesso.adessoandroidapp.logic.model.Pair;
import energy.adesso.adessoandroidapp.logic.model.exception.AdessoException;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Issue;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Meter;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Reading;

public class MockController {
    static DateTime time = DateTime.now();
    static Reading lastReading = new Reading("1243", "98 762 244", "einowner", "12345,754");

    public static void login(String username, String password) throws AdessoException {
        Log.println(Log.INFO, "", "Login with " + username + ", "  + password);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (username.equals("1234") && password.equals("."))
            return;
        else
            throw new AdessoException();
    }
    public static List<Meter> getOverview() {
        return Arrays.asList(new Meter[] {
                new MockMeter("id",time, time, time,
                        "Hauptsitz", "98 762 244", MeterKind.ELECTRIC, "einowner", lastReading),
                new MockMeter("id",time, time, time,
                        "Hauptsitz", "98 762 245", MeterKind.GAS, "einowner", lastReading),
                new MockMeter("id",time, time, time,
                        "Hauptsitz", "98 762 246", MeterKind.WATER, "einowner", lastReading),
                new MockMeter("id",time, time, time,
                        "Hauptsitz2", "98 762 247", MeterKind.ELECTRIC, "einowner", lastReading),
                new MockMeter("id",time, time, time,
                        "Hauptsitz2", "98 762 248", MeterKind.GAS, "einowner", lastReading),
                new MockMeter("id",time, time, time,
                        "Hauptsitz2", "98 762 249", MeterKind.WATER, "einowner", lastReading),
        });
    }

    public static DateTime getTime() {
        time = time.plusDays(7);
        return time;
    }

    public static void logOut() throws AdessoException {

    }
    public static void setServer(String toString) {

    }
    public static Pair<Meter, String> azureAnalyze(Bitmap b) throws AdessoException {
        return new Pair<>(new MockMeter("Name1", "12345", MeterKind.ELECTRIC, lastReading).toMeter(), "Mocked Man");
    }
    public static boolean isLoggedIn() {
        return false;
    }
    public static void sendIssue(Issue issue) {

    }
}
