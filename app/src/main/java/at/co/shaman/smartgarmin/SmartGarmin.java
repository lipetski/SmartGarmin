package at.co.shaman.smartgarmin;


import android.app.Application;

public class SmartGarmin extends Application {
    private AppExecutors _executors;

    @Override
    public void onCreate() {
        super.onCreate();
        _executors = new AppExecutors();
    }

    public AppExecutors getExecutors() {
        return _executors;
    }
}
