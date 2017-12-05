package com.techmata.transcomfy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.techmata.transcomfy.app.auth.AccountActivity;
import com.techmata.transcomfy.app.utils.PreferenceHelper;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_drawer) ;


        /**
         * Setup Drawer Toggle of the Toolbar
         */

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        //setSupportActionBar(toolbar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new ChooseRouteFragment(),"FRAG_MAIN").commit();
        toolbar.setTitle(getString(R.string.title_section1));

        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

                if (menuItem.getItemId() == R.id.nav_item_request) {
                    fragmentTransaction.replace(R.id.containerView,new MainFragment()).commit();
                    toolbar.setTitle(getString(R.string.title_section1));
                }
                if (menuItem.getItemId() == R.id.nav_item_trips) {
                    fragmentTransaction.replace(R.id.containerView,new TripsFragment()).commit();
                    toolbar.setTitle(getString(R.string.title_section2));
                }
                /*if (menuItem.getItemId() == R.id.nav_item_manager) {
                    fragmentTransaction.replace(R.id.containerView,new PlaceholderFragment()).commit();
                    toolbar.setTitle(getString(R.string.title_section3));
                }*/
                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    PreferenceHelper.logOut(getApplicationContext());
                    final Intent startIntent = new Intent(getApplicationContext(), AccountActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startIntent);
                    finish();
                }
                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    fragmentTransaction.replace(R.id.containerView,new PlaceholderFragment()).commit();
                    toolbar.setTitle(getString(R.string.title_section4));
                }

                return false;
            }

        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void setToolbarTitle(String title){
        toolbar.setTitle(title);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
            return rootView;
        }

        /*@Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }*/
    }

}
