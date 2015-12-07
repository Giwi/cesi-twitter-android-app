package giwi.org.cesitwitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import giwi.org.cesitwitter.fragments.AbstractFragment;
import giwi.org.cesitwitter.fragments.LoginFragment;
import giwi.org.cesitwitter.fragments.SignupFragment;

/**
 * The type Main activity.
 */
public class MainActivity extends AbstractActivity implements AbstractFragment.OnFragmentInteractionListener {
    private Fragment fragment;

    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public void onFragmentInteraction(String uri) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment).commit();
        }
        if ("login".equals(uri)) {
            fragment = LoginFragment.newInstance();
        } else if ("signup".equals(uri)) {
            fragment = SignupFragment.newInstance();
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }

}
