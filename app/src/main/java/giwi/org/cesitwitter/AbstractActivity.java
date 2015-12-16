package giwi.org.cesitwitter;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by xavier on 07/12/15.
 */
public class AbstractActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    /**
     * Method to close progress dialog.
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Display toast.
     *
     * @param body the body
     */
    public void displayToast(final String body) {
        runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(findViewById(R.id.layout), body, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Display progress dialog.
     */
    public void displayProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("In progress ...");
        progressDialog.show();
    }


}
