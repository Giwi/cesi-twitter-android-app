package giwi.org.cesitwitter.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import giwi.org.cesitwitter.Constants;
import giwi.org.cesitwitter.LoggedActivity;
import giwi.org.cesitwitter.R;
import giwi.org.cesitwitter.helper.HTTPHelper;
import giwi.org.cesitwitter.helper.PreferenceHelper;
import giwi.org.cesitwitter.helper.RestResponse;
import giwi.org.cesitwitter.helper.TwitterDAO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends AbstractFragment {
    TwitterDAO twitterDAO = new TwitterDAO();
    ImageView avatar;
    Button cameraButton;
    private static final int PHOTO_RESULT = 12;
    /**
     * New instance profile fragment.
     *
     * @return the profile fragment
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        avatar = (ImageView) v.findViewById(R.id.avatar);
        cameraButton = (Button) v.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PHOTO_RESULT);
            }
        });
        new GetProfileAsyncTask(v.getContext()).execute();
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PHOTO_RESULT  ){
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            avatar.setImageBitmap(image);
        }
    }

    /**
     * The type Get profile async task.
     */
    public class GetProfileAsyncTask extends AsyncTask<String, Void, String> {

        Context context;

        /**
         * Instantiates a new Login async task.
         *
         * @param context the context
         */
        public GetProfileAsyncTask(final Context context) {
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
                RestResponse r = twitterDAO.getConnectedUser(PreferenceHelper.getValue(getActivity(), PreferenceHelper.TOKEN));
                if (r.isError()) {
                    displayToast(new JSONObject(r.getBody()).getString("message"));
                    return null;
                }

                return r.getBody();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * On post execute.
         *
         * @param user the user
         */
        @Override
        protected void onPostExecute(final String user) {
            hideProgressDialog();
            if (user != null) {
                JSONObject juser = null;
                try {
                    juser = new JSONObject(user);
                    displayUser(juser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void displayUser(JSONObject juser) {

        try {
            File outFile;
            Context context = getActivity().getApplicationContext();
            outFile = File.createTempFile("xyz", null, context.getCacheDir());
            OutputStream out = new FileOutputStream(outFile);
            out.write(Base64.decode(juser.getJSONObject("avatar").getString("data").getBytes(), Base64.DEFAULT));
            out.flush();
            Log.i("image", outFile.getAbsolutePath());
            avatar.setImageBitmap(BitmapFactory.decodeFile(outFile.getAbsolutePath()));
       //     outFile.delete();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
