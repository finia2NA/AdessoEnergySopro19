// TODO: throws and param annotation

package energy.adesso.adessoandroidapp.logic.controller;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

import energy.adesso.adessoandroidapp.logic.model.identifiable.Meter;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Reading;
import energy.adesso.adessoandroidapp.logic.model.exception.*;


public class MainController {
  private static MainController instance;
  private String ip;
  private String token;
  private SharedPreferences prefs;

  private MainController() {

  }

  public void init(SharedPreferences prefs) {
    this.prefs = prefs;
  }

  /**
   * Singleton-getInstance.
   *
   * @return
   */
  public static MainController getInstance() {
    if (instance != null)

      return instance;
    return new MainController();
  }

  /**
   * gets all readings associated with the meter with the given ID
   *
   * @param meterId the meter to get readings for
   * @return the list of readings
   */
  public List<Reading> getDetails(String meterId) {
    return null;
  }


  /**
   * tries to log in with the given username and password.
   * @param username
   * @param password
   * @return
   * @throws NetworkException
   */
  public boolean login(String username, String password) throws NetworkException {
    return false;
  }

  /**
   * analyzes an image for readerNumber and current reading
   *
   * @param image the image to analyze
   * @return a Tuple of number, reading
   */
  public Pair<Integer, Integer> azureAnalyze(Bitmap image) {
    return null;
  }

  /**
   * Sets the ip to send requests to
   *
   * @param ip the ip tp connect to
   * @throws NetworkException when the Network is faulty
   */
  public void setServer(String ip) throws NetworkException {

  }

  public Reading createReading(String mid, String reading) throws NetworkException, CredentialException {
    return null;
  }

  public String doStuff() {
    return "zufjzcgh";
  }

  /**
   * Fetches all Meters associated with the logged in User.
   * These Meters are shallow, which means their Readings-List is still empty.
   *
   * @return A List of all Meters associated with the User.
   * @throws NetworkException    when the Network is faulty
   * @throws CredentialException when the User is not logged in
   */
  public List<Meter> getOverview() throws NetworkException, CredentialException {
    return null;
  }
}
