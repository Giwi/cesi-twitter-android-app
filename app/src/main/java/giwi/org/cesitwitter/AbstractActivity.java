package giwi.org.cesitwitter;

import android.app.ProgressDialog;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xavier on 07/12/15.
 */
public class AbstractActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    /**
     * Method to close progress dialog.
     */
    void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Display toast.
     *
     * @param body the body
     * @param v    the v
     */
    void displayToast(final String body, final View v) {
        runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(v, body, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Display progress dialog.
     */
    void displayProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("hello in progress ...");
        progressDialog.show();
    }
}
