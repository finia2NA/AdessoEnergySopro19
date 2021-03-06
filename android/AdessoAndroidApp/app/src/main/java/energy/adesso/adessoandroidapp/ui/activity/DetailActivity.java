package energy.adesso.adessoandroidapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import energy.adesso.adessoandroidapp.R;
import energy.adesso.adessoandroidapp.logic.model.MeterKind;
import energy.adesso.adessoandroidapp.logic.model.Pair;
import energy.adesso.adessoandroidapp.logic.model.exception.AdessoException;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Meter;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Reading;
import energy.adesso.adessoandroidapp.ui.adapter.ReadingAdapter;

public class DetailActivity extends AdessoActivity {
  DetailActivity a = this;
  ReadingAdapter listAdapter;
  protected List<Reading> readings;
  Meter m;

  String meterKey = "METER_KEY";

  Drawable icon;
  final AdapterView.OnItemClickListener onAdapterElementClick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
      showCorrectDialog(position);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null)
      m = (Meter) savedInstanceState.getSerializable(meterKey);
    setContentView(R.layout.activity_detail);

    // Set up toolbar
    Toolbar toolbar = findViewById(R.id.detail_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Setting the onClickEvent in XML results in an error
    CardView cardButton = ((CardView) findViewById(R.id.cardButton));
    cardButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onNewEntryClick(view);
      }
    });

    if (getIntent().hasExtra("meter"))
      m = (Meter) getIntent().getSerializableExtra("meter");
    updateTitleInfo();

    listReadings();
  }

  public void onNewEntryClick(View view) {
    showEditTextDialog(new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                           String entry = getEditTextDialogTextbox(dialog).getText().toString();
                           entry = entry.replace(",", "");
                           newEntryAsync(entry);
                         }
                       }, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                           dialog.cancel();
                         }
                       }, getString(R.string.new_input_title), getString(R.string.new_input_messsage),
        "");
  }

  public void onEditClick(View view) {
    showEditTextDialog(new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                           setNameAsync(getEditTextDialogTextbox(dialog).getText().toString());
                         }
                       }, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                           dialog.cancel();
                         }
                       }, getString(R.string.detail_edit_name_title), "", m.getName());
  }

  public void onGraphClick(View view) {
    String unit = null;
    if (m.getKind().equals(MeterKind.ELECTRIC))
      unit = getString(R.string.elecUnit);
    else if (m.getKind().equals(MeterKind.GAS))
      unit = getString(R.string.gasUnit);
    else if (m.getKind().equals(MeterKind.WATER))
      unit = getString(R.string.waterUnit);

    if (readings.size() < 2) {
      Toast.makeText(this, R.string.graph_on_empty_readings, Toast.LENGTH_SHORT).show();
      return;
    }

    startActivity(new Intent(this, GraphActivity.class).
        putExtra("readings", readings.toArray()).
        putExtra("meter", m).
        putExtra("unit", unit));
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putSerializable(meterKey, m);
    super.onSaveInstanceState(outState);
  }


  void listReadings() {
    // Get the icon and set the unit TextView
    icon = null;
    TextView unit = ((TextView) findViewById(R.id.listElementRightText));
    if (m.getKind().equals(MeterKind.ELECTRIC)) {
      icon = getDrawable(R.drawable.icon_electricity);
      unit.setText(R.string.elecUnit);
    } else if (m.getKind().equals(MeterKind.GAS)) {
      icon = getDrawable(R.drawable.icon_gas);
      unit.setText(R.string.gasUnit);
    } else if (m.getKind().equals(MeterKind.WATER)) {
      icon = getDrawable(R.drawable.icon_water);
      unit.setText(R.string.waterUnit);
    }

    // Init the adapter and the list
    updateReadingsAsync(onAdapterElementClick, icon);
  }

  void showCorrectDialog(final int position) {
    showEditTextDialog(new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        correctReadingAsync(new Pair<>(position,
            getEditTextDialogTextbox(dialog).getText().toString()));
      }
    }, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    }, getString(R.string.detail_correct_dialog_title),
        getString(R.string.detail_correct_dialog_desc),
        readings.get(position).getValue());
  }

  void updateTitleInfo() {
    ((TextView) findViewById(R.id.name)).setText(m.getName());
    ((TextView) findViewById(R.id.number)).setText(m.getMeterNumber());
    if (m.getKind().equals(MeterKind.ELECTRIC))
      ((TextView) findViewById(R.id.usage)).setText(m.getLastReading().getValue() + " " + getString(R.string.elecUnit));
    else if (m.getKind().equals(MeterKind.GAS))
      ((TextView) findViewById(R.id.usage)).setText(m.getLastReading().getValue() + " " + getString(R.string.gasUnit));
    else if (m.getKind().equals(MeterKind.WATER))
      ((TextView) findViewById(R.id.usage)).setText(m.getLastReading().getValue() + " " + getString(R.string.waterUnit));
  }

  void updateReadingsAsync(final AdapterView.OnItemClickListener onAdapterElementClick, final Drawable icon) {
    showLoadingPopup();
    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, List<Reading>> execute = new AsyncTask<Void, Void, List<Reading>>() {
      @Override
      protected List<Reading> doInBackground(Void... voids) {
        try {
          List<Reading> rs = m.getReadings();
          if (rs == null)
            return new ArrayList<Reading>();
          return rs;
        } catch (AdessoException e) {
          e.printStackTrace();
          return new ArrayList<Reading>();
        }
      }

      @Override
      protected void onPostExecute(List<Reading> readings) {
        a.readings = readings;

        listAdapter = new ReadingAdapter(a.getBaseContext(), readings, icon);
        ListView detailList = findViewById(R.id.detail_list);
        detailList.setAdapter(listAdapter);
        detailList.setOnItemClickListener(onAdapterElementClick);
        detailList.scrollTo(0, 0);

        hideLoadingPopup();
      }
    }.execute();
  }

  void setNameAsync(String s) {
    showLoadingPopup();
    @SuppressLint("StaticFieldLeak") final AsyncTask<String, Void, AdessoException> execute = new AsyncTask<String, Void, AdessoException>() {
      @Override
      protected AdessoException doInBackground(String... strs) {
        for (String s : strs) {
          try {
            m.setName(s);
          } catch (AdessoException e) {
            return e;
          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(AdessoException e) {
        if (e != null) {
          e.printStackTrace();
          Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        }
        updateTitleInfo();
        hideLoadingPopup();
      }
    }.execute(s);
  }

  void correctReadingAsync(Pair<Integer, String> p) {
    showLoadingPopup();
    @SuppressLint("StaticFieldLeak") AsyncTask<Pair<Integer, String>, Void, AdessoException> execute = new AsyncTask<Pair<Integer, String>, Void, AdessoException>() {
      @Override
      protected AdessoException doInBackground(Pair<Integer, String>... ps) {
        for (Pair<Integer, String> p : ps) {
          try {
            readings.get(p.first).correct(p.second);
          } catch (AdessoException e) {
            return e;
          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(AdessoException e) {
        if (e == null)
          listReadings();
        else {
          e.printStackTrace();
          Toast.makeText(a, R.string.generic_error_message,
              Toast.LENGTH_SHORT).show();
        }
        hideLoadingPopup();
      }
    }.execute(p);
  }

  void newEntryAsync(String entry) {
    showLoadingPopup();
    @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, AdessoException> execute = new AsyncTask<String, Void, AdessoException>() {
      @Override
      protected AdessoException doInBackground(String... strs) {
        for (String newReading : strs) {
          try {
            m.createReading(newReading);
          } catch (AdessoException e) {
            return e;
          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(AdessoException e) {
        if (e != null) {
          e.printStackTrace();
          Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        } else {
          hideLoadingPopup();
          updateReadingsAsync(onAdapterElementClick, icon);
        }
        hideLoadingPopup();
      }
    }.execute(entry);
  }
}
