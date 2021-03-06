package energy.adesso.adessoandroidapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import energy.adesso.adessoandroidapp.R;
import energy.adesso.adessoandroidapp.logic.controller.MainController;
import energy.adesso.adessoandroidapp.logic.model.exception.AdessoException;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Meter;
import energy.adesso.adessoandroidapp.logic.model.Pair;
import energy.adesso.adessoandroidapp.ui.adapter.MeterAdapter;

public class MainActivity extends AdessoActivity {
  final MainActivity a = this;

  final int CAMERA_REQUEST_IMAGE_BITMAP = 1;
  final int CAMERA_REQUEST_IMAGE_URI = 2;
  final int GALLERY_REQUEST_IMAGE_BITMAP = 10;

  protected List<Meter> meters;
  List<Meter> electricMeters;
  List<Meter> gasMeters;
  List<Meter> waterMeters;
  MeterAdapter listAdapter;
  Drawable[] meterIcons;

  // Events
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.mainToolbar);
    setSupportActionBar(toolbar);

    meterIcons = new Drawable[]{
        getDrawable(R.drawable.icon_electricity),
        getDrawable(R.drawable.icon_gas),
        getDrawable(R.drawable.icon_water)};

    updateMetersAsync();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.println(Log.INFO, "", "ActivityResult: " +
        Integer.toString(requestCode) + " " + Integer.toString(resultCode) + " ");

    if (requestCode == CAMERA_REQUEST_IMAGE_BITMAP && resultCode == RESULT_OK) {
      try {
        Bundle extras = data.getExtras();
        onImageReceivedAsync((Bitmap) extras.get("data"));
      } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, R.string.generic_error_message, Toast.LENGTH_LONG).show();
      }
    } else if (requestCode == GALLERY_REQUEST_IMAGE_BITMAP && resultCode == RESULT_OK) {
      try {
        final Uri imageUri = data.getData();
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        onImageReceivedAsync(selectedImage);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        Toast.makeText(this, R.string.generic_error_message, Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onBackPressed() {
    showLogoutMenu();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.issue:
        startActivity(new Intent(a, IssueActivity.class));
        return true;
      case R.id.logout:
        showLogoutMenu();
        return true;
      case R.id.choose_server:
        showChooseServerDialog();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
    if (isTopResumedActivity)
      showMeters(meters);
  }

  public void onFABClick(View view) {
    new AlertDialog.Builder(this)
        .setTitle(R.string.new_input_title)
        .setMessage(R.string.new_input_messsage)
        .setCancelable(true)
        .setPositiveButton(R.string.take_photo, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            onPhotoButtonClick();
          }
        })
        .setNegativeButton(R.string.select_from_gallery, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            onGalleryButtonClick();
          }
        })
        .setIcon(R.drawable.logo_drop)
        .show();
  }

  void onPhotoButtonClick() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, CAMERA_REQUEST_IMAGE_BITMAP);
    }
  }

  void onGalleryButtonClick() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
        GALLERY_REQUEST_IMAGE_BITMAP);
  }

  @SuppressLint("StaticFieldLeak")
  void onImageReceivedAsync(Bitmap b) {
    showLoadingPopup();
    new AsyncTask<Bitmap, Void, Pair<Meter, String>>() {
      @Override
      protected Pair<Meter, String> doInBackground(Bitmap... bs) {
        for (Bitmap b : bs) {
          try {
            return MainController.azureAnalyze(b);
          } catch (AdessoException e) {
            e.printStackTrace();
          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(Pair<Meter, String> res) {
        if (res == null || res.first == null || res.second == null) {
          hideLoadingPopup();
          Toast.makeText(a, R.string.generic_error_message,
                  Toast.LENGTH_SHORT).show();
          return;
        }

        LinearLayout l = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_reading_check,
                null);
        ((TextView) l.findViewById(R.id.number)).setText(res.first.getMeterNumber());
        ((TextView) l.findViewById(R.id.usage)).setText(res.second);

        hideLoadingPopup();

        new AlertDialog.Builder(a)
            .setTitle(R.string.check_image)
            .setCancelable(true)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
              }
            })
            .setNegativeButton(R.string.cancel, null)
            .setIcon(R.drawable.logo_drop)
            .setView(l)
            .show();
      }
    }.execute(b);
  }

  final AdapterView.OnItemClickListener onAdapterElecElementClick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      startActivity(new Intent(a, DetailActivity.class).
          putExtra("meter", electricMeters.get(position)));
    }
  };
  final AdapterView.OnItemClickListener onAdapterGasElementClick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      startActivity(new Intent(a, DetailActivity.class).
          putExtra("meter", gasMeters.get(position)));
    }
  };
  final AdapterView.OnItemClickListener onAdapterWaterElementClick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      startActivity(new Intent(a, DetailActivity.class).
          putExtra("meter", waterMeters.get(position)));
    }
  };

  void showLogoutMenu() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.logout_title)
        .setMessage(R.string.logout_text)
        .setCancelable(true)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            try {
              MainController.logOut();
            } catch (AdessoException e) {
            }
            finish();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
          }
        })
        .setIcon(R.drawable.logo_drop)
        .show();
  }

  void showMeters(List<Meter> meters) {
    electricMeters = new ArrayList<Meter>();
    gasMeters = new ArrayList<Meter>();
    waterMeters = new ArrayList<Meter>();

    for (Meter m : meters) {
      if (m.getType().toUpperCase().equals("ELECTRIC"))
        electricMeters.add(m);
      else if (m.getType().toUpperCase().equals("GAS"))
        gasMeters.add(m);
      else if (m.getType().toUpperCase().equals("WATER"))
        waterMeters.add(m);
    }

    // Electricity
    listAdapter = new MeterAdapter(this.getBaseContext(), electricMeters, meterIcons);
    ListView elecList = findViewById(R.id.elecList);
    elecList.setAdapter(listAdapter);
    elecList.setOnItemClickListener(onAdapterElecElementClick);

    // Gas
    listAdapter = new MeterAdapter(this.getBaseContext(), gasMeters, meterIcons);
    ListView gasList = findViewById(R.id.GasList);
    gasList.setAdapter(listAdapter);
    gasList.setOnItemClickListener(onAdapterGasElementClick);

    // Water
    listAdapter = new MeterAdapter(this.getBaseContext(), waterMeters, meterIcons);
    ListView waterList = findViewById(R.id.WaterList);
    waterList.setAdapter(listAdapter);
    waterList.setOnItemClickListener(onAdapterWaterElementClick);
  }

  void updateMetersAsync() {
    showLoadingPopup();
    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, List<Meter>> execute = new AsyncTask<Void, Void, List<Meter>>() {
      @Override
      protected List<Meter> doInBackground(Void... voids) {
        try {
          if (!MainController.isLoggedIn()) {
            // If we got here and are not logged in then we must be in a test so we have to log in using mock data
            MainController.setUsePersistence(false);
            MainController.loadSharedPreferences(a.getPreferences(Context.MODE_PRIVATE));
            MainController.login(getString(R.string.mock_username), getString(R.string.mock_password));
            // Please pretend like this doesn't exist
          }
          List<Meter> m = MainController.getOverview();
          return m;
        } catch (AdessoException e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override
      protected void onPostExecute(List<Meter> ms) {
        if (ms == null)
        {
          Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_LONG).show();
          hideLoadingPopup();
          a.finish();
        }
        else {
          a.meters = ms;
          showMeters(meters);
          hideLoadingPopup();
        }
      }
    }.execute();
  }
}