package at.co.shaman.smartgarmin;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link one_day#newInstance} factory method to
 * create an instance of this fragment.
 */
public class one_day extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mDay;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mBaseURL;
    private ActivityAdapter mActivityAdapter;

    public one_day() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment one_day.
     */
    // TODO: Rename and change types and number of parameters
    public static one_day newInstance(String param1, String param2) {
        one_day fragment = new one_day();
        Bundle args = new Bundle();
        //args.putString("date", mDay);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDay = getArguments().getString("date");
        }
        mBaseURL = "http://192.168.13.28:8081/rest";
    }

    public void onStart() {
        super.onStart();

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipelayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        RecyclerView lst = getView().findViewById(R.id.lstActivities);
        lst.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mActivityAdapter = new ActivityAdapter();
        lst.setAdapter(mActivityAdapter);

        if( mDay.length() != 0 ) {
            loadDay(mDay);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one_day, container, false);
        return v;
    }

    private void refreshContent() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                loadDay( mDay );
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void updateDate( String str_day ) {
        mDay = str_day;
        getArguments().putString("date", mDay);
        //loadDay( str_day );
    }

    public void loadDay( String str_day) {
        if( str_day.length() == 0 ) {
            return;
        }
        FragmentActivity act = getActivity();
        if( act == null ) {
            Log.w("one_day", "FRAGMENT ACTIVITY IS NULL!!!" );
            return;
        }
        final SmartGarmin app = (SmartGarmin)act.getApplication();
        Context context = getActivity().getApplicationContext();

        app.getExecutors().networkIO().execute(() -> {
            String str = mBaseURL + "/last30/" + str_day;
            StringBuilder content_today = getREST( mBaseURL + "/last30/" + str_day );
            StringBuilder content_activities = getREST( mBaseURL + "/activities_day/" + str_day );
            StringBuilder content_week = getREST( mBaseURL + "/week/" + str_day );

            FragmentActivity act2 = getActivity();
            if( act2 == null ) {
                Log.w("one_day", "FRAGMENT ACTIVITY (2) IS NULL!!!" );
                return;
            }

            act2.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Process 30 days
                    String dist = "0";
                    String elevation = "0";
                    String runs = "0";
                    String time = "0";
                    View view = getView();
                    if( view == null ) {
                        Log.w( "one_day", "VIEW IS NULL!!!" );
                        return;
                    }
                    try {
                        JSONArray jArray = new JSONArray((content_today.toString()));
                        JSONObject jObject = jArray.getJSONObject(0);
                        JSONObject jRunning = jObject.getJSONObject("running");
                        dist = jRunning.getString("distance");
                        elevation = jRunning.getString("elevation");
                        runs = jRunning.getString("num");
                        time = jRunning.getString("time");

                        // process other acivities
                        String txtNext = new String();
                        for (Iterator<String> it = jObject.keys(); it.hasNext(); ) {
                            String key = it.next();
                            if( key.equals( "running" ) ) {
                                continue;
                            }
                            JSONObject next = jObject.getJSONObject( key );
                            txtNext += " - " + key.toLowerCase(Locale.ROOT);
                            try {
                                String nextDist = next.getString("distance") + " km";
                                String nextNum = " | " + next.getString("num") + "x";
                                String nextTime = " | " + next.getString("time");
                                String nextElev = next.getString("elevation");
                                if( nextElev.equals( "0.000" ) ) {
                                    nextElev = "";
                                } else {
                                    nextElev = " | " + nextElev;
                                }
                                txtNext = txtNext + ": " + nextDist + nextNum + nextTime + nextElev;
                            } catch( JSONException e ) {
                                // oops
                            }
                            if( it.hasNext() ) {
                                txtNext += "\n";
                            }
                        }
                        TextView lblOther = getView().findViewById(R.id.lblOther);
                        lblOther.setText( txtNext );

                    } catch( JSONException e ) {
                        // oops
                    }

                    TextView lbl = getView().findViewById(R.id.lbl30DaysDist);
                    lbl.setText( dist );
                    lbl = getView().findViewById(R.id.lbl30DaysAlt);
                    lbl.setText( elevation );
                    lbl = getView().findViewById(R.id.lbl30DaysRuns);
                    lbl.setText( runs );
                    lbl = getView().findViewById(R.id.lbl30DaysTime);
                    lbl.setText( time );

                    // Process this week
                    dist = "0";
                    elevation = "0";
                    runs = "0";
                    time = "0";
                    try {
                        JSONArray jArray = new JSONArray((content_week.toString()));
                        JSONObject jObject = jArray.getJSONObject(0);
                        JSONObject jRunning = jObject.getJSONObject("running");
                        dist = jRunning.getString("distance");
                        elevation = jRunning.getString("elevation");
                        runs = jRunning.getString("num");
                        time = jRunning.getString("time");
                    } catch( JSONException e ) {
                        // oops
                    }

                    lbl = getView().findViewById(R.id.lblThisWeekDist);
                    lbl.setText( dist );
                    lbl = getView().findViewById(R.id.lblThisWeekAlt);
                    lbl.setText( elevation );
                    lbl = getView().findViewById(R.id.lblThisWeekRuns);
                    lbl.setText( runs );
                    lbl = getView().findViewById(R.id.lblThisWeekTime);
                    lbl.setText( time );

                    // Process activities
                    RecyclerView lst = getView().findViewById(R.id.lstActivities);
                    mActivityAdapter = new ActivityAdapter();
                    lst.setAdapter(mActivityAdapter);
                    mActivityAdapter.setActivities(content_activities.toString(), app, ((AppCompatActivity) getActivity() ) );

                }
            });
        });
    }

    private StringBuilder getREST( String baseUrl ) {
        StringBuilder content = new StringBuilder();
        try {
            URL filterUrl = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
            connection.setReadTimeout(10000); // 10 sec
            connection.setConnectTimeout(10000); // 10 sec
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            is.close();
            connection.disconnect();
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}