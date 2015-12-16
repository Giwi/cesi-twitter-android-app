package giwi.org.cesitwitter.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import giwi.org.cesitwitter.AbstractActivity;
import giwi.org.cesitwitter.LoggedActivity;
import giwi.org.cesitwitter.R;
import giwi.org.cesitwitter.adapter.MessagesAdapter;
import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.PreferenceHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;
import giwi.org.cesitwitter.model.Message;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MessageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageListFragment extends Fragment {

    private AbstractFragment.OnFragmentInteractionListener mListener;

    ListView listView;
    MessagesAdapter adapter;
    TwitterDAO twitterDAO = new TwitterDAO();
    String token;
    SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Instantiates a new Message list fragment.
     */
    public MessageListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MessageListFragment.
     */
    public static MessageListFragment newInstance() {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * On create view view.
     *
     * @param inflater           the inflater
     * @param container          the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message_list, container, false);
        token = PreferenceHelper.getValue(getActivity(), PreferenceHelper.TOKEN);
        listView = (ListView) v.findViewById(R.id.listView);
        adapter = new MessagesAdapter(getActivity());

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        listView.setAdapter(adapter);
        refresh();
        return v;
    }

    private void refresh() {
        ((AbstractActivity)getActivity()).displayProgressDialog();
        new MessageAsyncTask(getActivity()).execute(token);
    }

    /**
     * On attach.
     *
     * @param context the context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AbstractFragment.OnFragmentInteractionListener) {
            mListener = (AbstractFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * On detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                    ((LoggedActivity) getActivity()).hideProgressDialog();
                    ((LoggedActivity) getActivity()).displayToast(new JSONObject(r.getBody()).getString("message"));
                    return null;
                }

                return new JSONArray(r.getBody());
            } catch (JSONException e) {
                ((LoggedActivity) getActivity()).hideProgressDialog();
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
            ((LoggedActivity) getActivity()).hideProgressDialog();
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
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }
}
