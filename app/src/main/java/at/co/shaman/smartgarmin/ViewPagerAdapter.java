package at.co.shaman.smartgarmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void clear()
    {
        fragments.clear();
        fragmentTitle.clear();
    }

    public void add(Fragment fragment,String title)
    {
        fragments.add(fragment);
        fragmentTitle.add(title);
    }

    public void add_first(Fragment fragment,String title)
    {
        fragments.add(0,fragment);
        fragmentTitle.add(0,title);
    }

    public void update(int pos, String date) {
        one_day day = (one_day) fragments.get(pos);
        day.updateDate( date );
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}