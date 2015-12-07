package giwi.org.cesitwitter.fragments;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import giwi.org.cesitwitter.R;

/**
 * Created by xavier on 07/12/15.
 */
public class AbstractFragment extends Fragment {
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
     */
    void displayToast(final String body) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Snackbar.make(getActivity().findViewById(R.id.layout), body, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Display progress dialog.
     */
    void displayProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("hello in progress ...");
        progressDialog.show();
    }

    /**
     * The interface On fragment interaction listener.
     */
    public interface OnFragmentInteractionListener {
        /**
         * On fragment interaction.
         *
         * @param uri the uri
         */
        void onFragmentInteraction(String uri);
    }
}
