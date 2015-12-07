package giwi.org.cesitwitter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import giwi.org.cesitwitter.adapter.MessagesAdapter;
import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;
import giwi.org.cesitwitter.model.Message;

/**
 * The type Messages activity.
 */
public class LoggedActivity extends AbstractActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    MessagesAdapter adapter;
    TwitterDAO twitterDAO = new TwitterDAO();
    String token;
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        token = getIntent().getStringExtra(Constants.INTENT_TOKEN);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MessagesAdapter(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView.setAdapter(adapter);
        refresh();
    }

    private void refresh() {
        new MessageAsyncTask(this).execute(token);
    }

    /**
     * On back pressed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * On create options menu boolean.
     *
     * @param menu the menu
     * @return the boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }

    /**
     * On options item selected boolean.
     *
     * @param item the item
     * @return the boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On navigation item selected boolean.
     *
     * @param item the item
     * @return the boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_messages) {
            Intent i = new Intent(this, LoggedActivity.class);
            i.putExtra(Constants.INTENT_TOKEN, token);
            startActivity(i);
        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * The type Login async task.
     */
    public class MessageAsyncTask extends AsyncTask<String, Void, JSONArray> {

        Context context;

        /**
         * Instantiates a new Login async task.
         *
         * @param context the context
         */
        public MessageAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background string.
         *
         * @param params the params
         * @return the JSONArray
         */
        @Override
        protected JSONArray doInBackground(String... params) {
            try {

                if (!HTTPHelper.isInternetAvailable(context)) {
                    return new JSONArray();
                }
                RestResponse r = twitterDAO.getMessages(params[0]);
                if (r.isError()) {
                    displayToast(new JSONObject(r.getBody()).getString("message"));
                    return null;
                }

                return new JSONArray(r.getBody());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * On post execute.
         *
         * @param res the res
         */
        @Override
        protected void onPostExecute(final JSONArray res) {
            hideProgressDialog();
            List<Message> msgs = new ArrayList<>();
            if (res != null) {
                for (int i = 0; i < res.length(); i++) {
                    try {
                        JSONObject m = res.getJSONObject(i);
                        msgs.add(new Message(m.getJSONObject("user").getString("username"),
                                m.getString("message"),
                                m.getString("date"),
                                m.getJSONObject("user").optString("urlPhoto")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.reverse(msgs);
                adapter.addMessage(msgs);
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }
}
