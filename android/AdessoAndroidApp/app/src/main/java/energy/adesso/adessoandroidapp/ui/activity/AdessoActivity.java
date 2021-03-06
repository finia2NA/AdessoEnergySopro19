package energy.adesso.adessoandroidapp.ui.activity;

import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.IllegalFormatException;

import energy.adesso.adessoandroidapp.R;
import energy.adesso.adessoandroidapp.logic.controller.MainController;

public abstract class AdessoActivity extends AppCompatActivity {
  protected AdessoActivity a = this;

  // Loading
  private AlertDialog loadingPopup = null;

  protected void showLoadingPopup() {
    loadingPopup = new AlertDialog.Builder(this).
        setView(getLayoutInflater().
            inflate(R.layout.loading, null)).
        setCancelable(false).
        show();
  }

  protected void hideLoadingPopup() {
    if (loadingPopup != null)
      loadingPopup.dismiss();
  }

  // Dialogs
  protected void showEditTextDialog(DialogInterface.OnClickListener onPositive,
                                                   DialogInterface.OnClickListener onNegative,
                                                   String title, String bottomText, String startEdittextText) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);

    LinearLayout l = (LinearLayout) getLayoutInflater().
        inflate(R.layout.dialog_edit, null);
    EditText input = l.findViewById(R.id.dialogEditTextBox);
    input.setText(startEdittextText);
    TextView bottom = l.findViewById(R.id.dialogBottomText);
    bottom.setText(bottomText);
    if (bottomText == null || bottomText.equals(""))
      bottom.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

    builder.setView(l);

    builder.setPositiveButton(R.string.ok, onPositive);
    builder.setNegativeButton(R.string.cancel, onNegative);

    builder.show();
  }

  protected EditText getEditTextDialogTextbox(DialogInterface dialog) {
    AlertDialog dia = (AlertDialog)dialog;
    EditText e = dia.findViewById(R.id.dialogEditTextBox);
    return e;
  }

  void showChooseServerDialog() {
    showEditTextDialog(new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          MainController.setServer(getEditTextDialogTextbox(dialog).getText().toString());
        } catch (IllegalFormatException e) {
          Toast.makeText(a, R.string.generic_error_message, Toast.LENGTH_SHORT).show();
        }
      }
    }, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    }, getString(R.string.server_choose_text), "", "");
  }
}
