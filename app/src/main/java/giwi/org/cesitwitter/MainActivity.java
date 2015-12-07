package giwi.org.cesitwitter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;

public class MainActivity extends AbstractActivity {
    TwitterDAO twitterDAO = new TwitterDAO();
    EditText login;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (EditText) findViewById(R.id.input_login);
        password = (EditText) findViewById(R.id.input_password);
        ((Button) findViewById(R.id.loginButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProgressDialog();
                new LoginAsyncTask(v.getContext()).execute(login.getText().toString(), password.getText().toString());
            }
        });
    }


    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

        Context context;

        public LoginAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                if (!HTTPHelper.isInternetAvailable(context)) {
                    return "Internet not available";
                }
                RestResponse r = twitterDAO.login(params[0], params[1]);
                if (r.isError()) {
                    displayToast(r.getBody());
                }

                return new JSONObject(r.getBody()).getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            hideProgressDialog();
        }
    }

}
