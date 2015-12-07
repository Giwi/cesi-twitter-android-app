package giwi.org.cesitwitter.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import giwi.org.cesitwitter.Constants;
import giwi.org.cesitwitter.LoggedActivity;
import giwi.org.cesitwitter.R;
import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends AbstractFragment {
    TwitterDAO twitterDAO = new TwitterDAO();
    EditText login;
    EditText password;
    View v;
    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false);
        login = (EditText) v.findViewById(R.id.input_login);
        password = (EditText) v.findViewById(R.id.input_password);
        v.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgressDialog();
                new LoginAsyncTask(v.getContext()).execute(login.getText().toString(), password.getText().toString());
            }
        });
        v.findViewById(R.id.signupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction("signup");
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

        Context context;

        /**
         * Instantiates a new Login async task.
         *
         * @param context the context
         */
        public LoginAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background string.
         *
         * @param params the params
         * @return the string
         */
        @Override
        protected String doInBackground(String... params) {
            try {

                if (!HTTPHelper.isInternetAvailable(context)) {
                    return "Internet not available";
                }
                RestResponse r = twitterDAO.login(params[0], params[1]);
                if (r.isError()) {
                    displayToast(new JSONObject(r.getBody()).getString("message"));
                    return null;
                }

                return new JSONObject(r.getBody()).getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * On post execute.
         *
         * @param token the token
         */
        @Override
        protected void onPostExecute(final String token) {
            hideProgressDialog();
            if (token != null) {
                Intent i = new Intent(context, LoggedActivity.class);
                i.putExtra(Constants.INTENT_TOKEN, token);
                startActivity(i);
            }
        }
    }
}
