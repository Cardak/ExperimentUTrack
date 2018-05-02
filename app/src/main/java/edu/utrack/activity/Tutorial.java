package edu.utrack.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.utrack.R;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.settings.AppSettings;

import static java.security.AccessController.getContext;

public class Tutorial extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            ListView listView = rootView.findViewById(R.id.list_view);
            Button button = rootView.findViewById(R.id.button);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALENDAR},0);
            switch(getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1:
                    textView.setText("Welcome to the UTrack app tutorial! You may be wondering why the app just crashed, It was meant to do that and assuming you gave us the permissions we needed it won't happen again!\n\nUTrack can import events from a " +
                            "google calendar then monitor which apps you use during these events to help cut down on distractions during important events.\n\nPlease swipe to continue." +
                            "");
                    button.setVisibility(View.INVISIBLE);
                    AppSettings settings = new AppSettings(new File(getContext().getFilesDir(), "settings.conf"));
                    settings.load();
                    settings.tutorial = true;
                    settings.save();
                    settings.load();
                break;
                case 2:
                    textView.setText("Calendar Setup!\n\nPlease select a calendar to monitor. UTrack will monitor your app usage during every event in the calendar you select," +
                            " for best results use on a timetable. You can disable tracking for individual events on your calendar by opening the event and excluding it within this app.");
                    ListPreference preferenceCalendar = new ListPreference(getActivity());

                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Uri uri = CalendarContract.Calendars.CONTENT_URI;
                    String[] qry = {
                            CalendarContract.Calendars._ID,
                            CalendarContract.Calendars.ACCOUNT_NAME,
                            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                            CalendarContract.Calendars.OWNER_ACCOUNT
                    };

                    @SuppressLint("MissingPermission")
                    Cursor cursor = contentResolver.query(uri, qry, "", new String[0], null);
                    List<CalendarData> data = new ArrayList<>();

                    while(cursor.moveToNext()) {
                        data.add(new CalendarData(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                    }
                    cursor.close();
                    String[] ray = new String[data.size()];
                    for(int i = 0;i < data.size();i++)
                    {
                        ray[i] = data.get(i).getName();
                    }
                    listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,android.R.id.text1,ray));
                    listView.setClickable(true);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            // TODO Auto-generated method stub
                            System.out.println(position);
                            listView.setSelection(position);
                            textView.setText("Calendar Setup!\n\nPlease select a calendar to monitor. UTrack will monitor your app usage during every event in the calendar you select, for best results use on a timetable.\n\n\n\n\nCurrently Selected: " + ray[position]);
                            AppSettings settings = new AppSettings(new File(getContext().getFilesDir(), "settings.conf"));
                            settings.load();
                            settings.currentCalendarID = position + 1;
                            settings.tutorial = false;
                            settings.save();
                            settings.load();
                        }


                      //  @Override
                      //  public void onNothingSelected(AdapterView<?> arg0) {
                      //      // TODO Auto-generated method stub
                      //
                      //  }

                    });
                    button.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    textView.setText("Goals and objectives can be set within the app to set targets for phone use during monitored events, these can be set by an absolute number of screen ons or a " +
                            "total time in seconds. You can also set relative targets such spending less than 10% of the event on the phone.\n\n" +
                            "You are in full control of your data! Tracking can be disabled at any time in the settings menu, you can also change how long we keep your data for.\n\n" +
                            "Congratulations on configuring UTrack! Please click the continue button to explore the app," +
                            " there won't be any data yet though so feel free to close the app and check back later!\n\n" +
                            "Note: If you are returned to the start of the tutorial you configured UTrack incorrectly. This was likely caused by not selecting a calendar when prompted, it was quite simple and" +
                            " if this is the case please try again.");
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ActivitySelectEvent.class);
                            startActivity(intent);
                        }
                    });
                    break;
            }
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
