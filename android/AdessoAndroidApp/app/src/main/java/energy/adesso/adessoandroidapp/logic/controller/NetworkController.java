package energy.adesso.adessoandroidapp.logic.controller;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import energy.adesso.adessoandroidapp.logic.model.exception.NetworkException;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class NetworkController {
  private final static String defaultURL = "124.245.1.240:3001";
  private static String baseURL = defaultURL;

  private static String username;
  private static String password;

  private static OkHttpClient ok = new OkHttpClient();


  private static NetworkController instance;

  public static final MediaType JSON
          = MediaType.get("application/json; charset=utf-8");


  private NetworkController() {

  }

  public static String get(String path, boolean useCredentials) throws NetworkException {
    //TODO accept token
    Request request;
    if (useCredentials)
      request = new Request.Builder()
              .addHeader("Authorization", Credentials.basic(username, password))
              .addHeader("Host",baseURL)
              .url(baseURL + path)
              .build();
    else
      request = new Request.Builder()
              .addHeader("Host",baseURL)
              .url(baseURL + path)
              .build();
    try {
      Response response = ok.newCall(request).execute();
      return response.body().string();
    } catch (IOException | NullPointerException e) {
      throw new NetworkException();
    }
  }

  public static String post(String path, String json, boolean useCredentials) throws NetworkException {
    //TODO accept token
    RequestBody body = RequestBody.create(JSON, json);
    Request request;
    if (useCredentials)
      request = new Request.Builder()
              .addHeader("Authorization", Credentials.basic(username, password))
              .addHeader("Host",baseURL)
              .url(baseURL + path)
              .post(body)
              .build();
    else
      request = new Request.Builder()
              .addHeader("Host",baseURL)
              .url(baseURL + path)
              .post(body)
              .build();
    try {
      Response response = ok.newCall(request).execute();
      return response.body().string();
    } catch (IOException | NullPointerException e) {
      throw new NetworkException();
    }
  }

  public static String put(String path, String json, boolean useCredentials) throws NetworkException {
    //TODO accept token
    RequestBody body = RequestBody.create(JSON, json);
    Request request;
    if (useCredentials)
    request = new Request.Builder()
            .addHeader("Authorization", Credentials.basic(username, password))
            .addHeader("Host",baseURL)
            .url(baseURL + path)
            .put(body)
            .build();
    else
      request = new Request.Builder()
              .addHeader("Host",baseURL)
              .url(baseURL + path)
              .put(body)
              .build();

    try {
      Response response = ok.newCall(request).execute();
      return response.body().string();
    } catch (IOException | NullPointerException e) {
      throw new NetworkException();
    }
  }


  public static void setAddress(String address) {
    if (address != null)
      baseURL = address;
    else
      baseURL = defaultURL;
  }

  public static void setCredentials(String usr, String pw) {
    username = usr;
    password = pw;
  }


}
