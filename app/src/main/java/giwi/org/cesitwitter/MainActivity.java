package giwi.org.cesitwitter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;

/**
 * The type Main activity.
 */
public class MainActivity extends AbstractActivity {
    TwitterDAO twitterDAO = new TwitterDAO();
    EditText login;
    EditText password;

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (EditText) findViewById(R.id.input_login);
        password = (EditText) findViewById(R.id.input_password);
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgressDialog();
                new LoginAsyncTask(v.getContext()).execute(login.getText().toString(), password.getText().toString());
            }
        });
    }


    /**
     * The type Login async task.
     */
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
                    displayToast(new JSONObject(r.getBody()).getString("message"), findViewById(R.id.layout));
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
                Intent i = new Intent(context, MessagesActivity.class);
                i.putExtra(Constants.INTENT_TOKEN, token);
                startActivity(i);
            }
        }
    }

}
