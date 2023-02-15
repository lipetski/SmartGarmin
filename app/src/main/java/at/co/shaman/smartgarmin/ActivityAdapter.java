package at.co.shaman.smartgarmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mActDesc;
    private List<String> mActType;
    private List<String> mActName;
    private List<String> mActWork;
    private List<String> mActHR;
    private List<String> mActAscent;
    private List<String> mActDistance;
    private SmartGarmin _app;
    private AppCompatActivity _parent;

    public ActivityAdapter() {
        mActDesc = new ArrayList<String>();
        mActType = new ArrayList<String>();
        mActName = new ArrayList<String>();
        mActWork = new ArrayList<String>();
        mActHR = new ArrayList<String>();
        mActAscent = new ArrayList<String>();
        mActDistance = new ArrayList<String>();
    }

    public void setActivities(String answer, SmartGarmin app, AppCompatActivity parent) {
        _app = app;
        _parent = parent;
        try {
            JSONArray jArrayAll = new JSONArray(answer);
            JSONArray jArray = jArrayAll.getJSONArray( 0 );
            for (int iRule = 0; iRule < jArray.length(); iRule++) {
                JSONObject next = jArray.getJSONObject(iRule);
                String str = next.getString("description");
                if( str.length() == 0 ) {
                    str = "-";
                }
                mActDesc.add(str);
                str = next.getString("distance");
                mActDistance.add(str + " km");
                str = next.getString("HR");
                mActHR.add(str);
                str = next.getString("elevation");
                mActAscent.add(str + " m");
                str = next.getString("name");
                mActName.add(str);
                str = next.getString("type");
                try {
                    String str1 = next.getString("type_full");
                    if( ( str1.length() > 0 ) && ( !str1.equals( "uncategorized") ) ) {
                        str = str + "/" + str1;
                    }
                } catch (JSONException e ) {
                }
                mActType.add(str);
                str = next.getString("work");
                mActWork.add("[" + str + "]");
            }
        } catch (JSONException e) {
            // Oops
        }

    }

    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity, parent, false);
        ActivityHolder holder1 = new ActivityHolder(view);

        return holder1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ActivityHolder holder1 = (ActivityHolder) (holder);
        TextView view = holder1.getDesc();
        view.setText(mActDesc.get(position));
        view = holder1.getName();
        view.setText(mActName.get(position));
        view = holder1.getWork();
        view.setText(mActWork.get(position));
        view = holder1.getHR();
        view.setText(mActHR.get(position));
        view = holder1.getAscent();
        view.setText(mActAscent.get(position));
        view = holder1.getDistance();
        view.setText(mActDistance.get(position));
        view = holder1.getType();

        // set running color
        view.setText(mActType.get(position));
        String str_type = mActType.get(position);
        if( str_type.contains("Threshold")) {
            view.setBackgroundColor(0xffffa500);
        } else if( ( str_type.contains("Intervals")) || ( str_type.contains("Fartlek")) || ( str_type.contains("Tune-up")) || ( str_type.contains("Race")) ) {
            view.setBackgroundColor(0xffFF2400);
        } else if( str_type.contains("Easy")) {
            view.setBackgroundColor(0xff2ECC71);
        } else if( str_type.contains("Recovery")) {
            view.setBackgroundColor(0xffA0A0A0);
        } else if( str_type.contains("Hill")) {
            view.setBackgroundColor(0xffE1C16E);
        } else if( str_type.contains("Long run")) {
            view.setBackgroundColor(0xffffc3a0);
        } else if( str_type.contains("Medium")) {
            view.setBackgroundColor(0xffc6e2ff);
        }
    }

    @Override
    public int getItemCount() {
        return mActDesc.size();

    }

    static class ActivityHolder extends RecyclerView.ViewHolder {
        private final TextView txtDesc;
        private final TextView txtName;
        private final TextView txtWork;
        private final TextView txtType;
        private final TextView txtHR;
        private final TextView txtDistance;
        private final TextView txtAscent;

        public ActivityHolder(@NonNull View itemView) {
            super(itemView);

            txtDesc = itemView.findViewById(R.id.lblActivityDescription);
            txtName = itemView.findViewById(R.id.lblActivityName);
            txtWork = itemView.findViewById(R.id.lblActivityWork);
            txtType = itemView.findViewById(R.id.lblActivityType);
            txtHR = itemView.findViewById(R.id.lblActivityHR);
            txtDistance = itemView.findViewById(R.id.lblActivityDistance);
            txtAscent = itemView.findViewById(R.id.lblActivityAltitude);
        }

        public TextView getDesc() {
            return txtDesc;
        }
        public TextView getName() {
            return txtName;
        }
        public TextView getWork() {
            return txtWork;
        }
        public TextView getType() {
            return txtType;
        }
        public TextView getHR() {
            return txtHR;
        }
        public TextView getDistance() {
            return txtDistance;
        }
        public TextView getAscent() {
            return txtAscent;
        }

    }

}