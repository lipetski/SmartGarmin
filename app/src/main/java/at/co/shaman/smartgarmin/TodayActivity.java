package at.co.shaman.smartgarmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TodayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Context context = getApplicationContext();
        //View view = this.findViewById(android.R.id.content);
        ConstraintLayout view = findViewById(R.id.layoutDay);
        view.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                Toast.makeText(TodayActivity.this, "Left", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSwipeRight() {
                Toast.makeText(TodayActivity.this, "Right", Toast.LENGTH_LONG).show();
            }
        });

        RecyclerView lst = findViewById(R.id.lstActivities);
        lst.setLayoutManager(new LinearLayoutManager(this));
    }
}