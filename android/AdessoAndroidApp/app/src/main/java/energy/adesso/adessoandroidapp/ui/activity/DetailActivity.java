package energy.adesso.adessoandroidapp.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import energy.adesso.adessoandroidapp.R;
import energy.adesso.adessoandroidapp.logic.model.MeterKind;
import energy.adesso.adessoandroidapp.logic.model.exception.AdessoException;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Meter;
import energy.adesso.adessoandroidapp.logic.model.identifiable.Reading;
import energy.adesso.adessoandroidapp.ui.adapter.MeterAdapter;
import energy.adesso.adessoandroidapp.ui.adapter.ReadingAdapter;

public class DetailActivity extends AppCompatActivity {
    Activity a = this;
    ReadingAdapter listAdapter;
    Meter m;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setting the onClickEvent in XML results in an error
        CardView cardButton = ((CardView)findViewById(R.id.cardButton));
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewEntryClick(view);
            }
        });

        m = (Meter)getIntent().getSerializableExtra("meter");
        updateTitleInfo();

        listReadings();
    }
    public void onNewEntryClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_input_title);

        // Set up textbox
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(24,24,24,24);
        input.layout(24,24,24,24);
        builder.setView(input);

        // Set up events
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newReading = input.getText().toString();
                try { m.createReading(newReading); }
                catch (AdessoException e) {
                    Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
                }
                listReadings();

                // TODO: Soll: Thread network calls?
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void onEditClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.detail_edit_name_title);
        //builder.setMessage(R.string.detail_edit_name_text);

        // Set up textbox
        LinearLayout l = (LinearLayout)getLayoutInflater().
                inflate(R.layout.dialog_edit, null);
        final EditText input = (EditText)l.findViewById(R.id.name);
        input.setText(m.getName());
        builder.setView(l);

        // Set up events
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    m.setName(input.getText().toString());
                } catch (AdessoException e) {
                    Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
                }
                updateTitleInfo();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void listReadings() {
        // Get the icon and set the unit TextView
        Drawable icon = null;
        TextView unit = ((TextView)findViewById(R.id.unit));
        if (m.getKind().equals(MeterKind.ELECTRIC)) {
            icon = getDrawable(R.drawable.icon_electricity);
            unit.setText(R.string.elecUnit);
        }
        else if (m.getKind().equals(MeterKind.GAS)) {
            icon = getDrawable(R.drawable.icon_gas);
            unit.setText(R.string.gasUnit);
        }
        else if (m.getKind().equals(MeterKind.WATER)) {
            icon = getDrawable(R.drawable.icon_water);
            unit.setText(R.string.gasUnit);
        }

        try {
            // Init the adapter
            listAdapter = new ReadingAdapter(this.getBaseContext(), m.getReadings(), icon);
            ListView detailList = findViewById(R.id.detail_list);
            detailList.setAdapter(listAdapter);
        } catch (AdessoException e) {
            Toast.makeText(this, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        }
    }
    void updateTitleInfo() {
        ((TextView)findViewById(R.id.name)).setText(m.getName());
        ((TextView)findViewById(R.id.number)).setText(m.getMeterNumber());
        ((TextView)findViewById(R.id.usage)).setText(m.getLastReading().getValue() +  " kWh");
    }
}
