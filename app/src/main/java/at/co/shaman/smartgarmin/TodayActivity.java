package at.co.shaman.smartgarmin;

import static java.lang.Math.min;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;



public class TodayActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        mViewPager = findViewById(R.id.viewpager);

        // setting up the adapter
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // add the fragments
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String cur_day = sdf.format(c);
        updateTopDate( cur_day );

        for (int iDay = -60; iDay <= 0; iDay++) {
            Fragment day1 = new one_day();
            Bundle args1 = new Bundle();
            if (iDay >= -1) {
                args1.putString("date", formatStrDay(cur_day, iDay));
            } else {
                args1.putString("date", "");
            }
            day1.setArguments(args1);
            Integer iDayInt = iDay;
            mViewPagerAdapter.add(day1, "Page" + iDayInt.toString());
        }

        // Set the adapter
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mViewPagerAdapter.getCount() - 1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           }

           @Override
           public void onPageSelected(int position) {
               Fragment fragment = mViewPagerAdapter.getItem(position);
               if( fragment != null ) {
                   Bundle args = fragment.getArguments();
                   String cur_day = args.getString("date");
                   updateTopDate(cur_day);
                   if (position > 0) {
                       one_day day_prev = (one_day) mViewPagerAdapter.getItem(position - 1);
                       String str_prev = formatStrDay(cur_day, -1);
                       day_prev.updateDate(str_prev);
                   }
               }
           }

           @Override
           public void onPageScrollStateChanged(int state) {
           }

       }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public String formatStrDay(String cur_day, Integer diff) {
        String str_out = "";
        try {
            String str = cur_day;

            Date date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(str);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, diff);
            date = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            str_out = sdf.format(date);
        } catch (ParseException e) {
            // oops
        }
        return str_out;
    }

    private void updateTopDate(String cur_day) {
        // Show current day at the top
        try {
            Date date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(cur_day);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yy");

            String str_day = sdf.format(date);
            TextView viewDay = findViewById(R.id.lblDaySelect);
            viewDay.setText(str_day);

            // set month and year icons
            Calendar cal_next_month = Calendar.getInstance();
            cal_next_month.setTime(date);
            cal_next_month.add(Calendar.MONTH, 1);
            Date date_next_month = cal_next_month.getTime();

            Calendar cal_next_year = Calendar.getInstance();
            cal_next_year.setTime(date);
            cal_next_year.add(Calendar.YEAR, 1);
            Date date_next_year = cal_next_year.getTime();

            Calendar cal_prev_year = Calendar.getInstance();
            cal_prev_year.setTime(date);
            cal_prev_year.add(Calendar.YEAR, -1);
            Date date_prev_year = cal_prev_year.getTime();

            Date cal_today = Calendar.getInstance().getTime();

            Button lbl_month_next = findViewById(R.id.btnNextMonth);
            if (date_next_month.after(cal_today)) {
                lbl_month_next.setTextColor(0xffa0a0a0);
            } else {
                lbl_month_next.setTextColor(0xff000000);
            }

            Button lbl_year_next = findViewById(R.id.btnNextYear);
            if (date_next_year.after(cal_today)) {
                lbl_year_next.setTextColor(0xffa0a0a0);
            } else {
                lbl_year_next.setTextColor(0xff000000);
            }

            Button lbl_month_prev = findViewById(R.id.btnPrevMonth);
            lbl_month_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal_prev_month = Calendar.getInstance();
                    cal_prev_month.setTime(date);
                    cal_prev_month.add(Calendar.MONTH, -1);
                    Date date_prev_month = cal_prev_month.getTime();
                    resetFragments( date_prev_month );
                }
            });

            lbl_month_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal_next_month = Calendar.getInstance();
                    cal_next_month.setTime(date);
                    cal_next_month.add(Calendar.MONTH, 1);
                    Date date_next_month = cal_next_month.getTime();
                    resetFragments( date_next_month );
                }
            });

            Button lbl_year_prev = findViewById(R.id.btnPrevYear);
            lbl_year_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal_prev_year = Calendar.getInstance();
                    cal_prev_year.setTime(date);
                    cal_prev_year.add(Calendar.YEAR, -1);
                    Date date_prev_year = cal_prev_year.getTime();
                    resetFragments( date_prev_year );
                }
            });

            lbl_year_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal_next_year = Calendar.getInstance();
                    cal_next_year.setTime(date);
                    cal_next_year.add(Calendar.YEAR, 1);
                    Date date_next_year = cal_next_year.getTime();
                    resetFragments( date_next_year );
                }
            });
            
        } catch (ParseException e) {
            // oops
        }
    }

    private void resetFragments( Date date )
    {
        Date cal_today = Calendar.getInstance().getTime();
        if( date.after( cal_today ) ) {
            return;
        }

        long diff = cal_today.getTime() - date.getTime();
        int num_days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        int end_day = min( num_days, 30 );
        int start_day = end_day - 60;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String cur_day = sdf.format(date);

        for (int iDay = start_day; iDay <= end_day; iDay++) {
            one_day day1 = (one_day) mViewPagerAdapter.getItem(iDay - start_day);
            String str_next_day = formatStrDay(cur_day, iDay);
            day1.updateDate(str_next_day);
        }
        mViewPager.setCurrentItem(60-end_day);
        Integer pos = mViewPager.getCurrentItem();
        one_day cur_adapter = (one_day) mViewPagerAdapter.getItem(pos);
        Bundle args = cur_adapter.getArguments();
        String cur_day1 = args.getString("date");
        cur_adapter.loadDay(cur_day1);
        updateTopDate(cur_day1);
    }

}
