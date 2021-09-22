package com.underscoreresearch.mauritzremote.rooms;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.config.Settings;
import com.underscoreresearch.mauritzremote.rooms.common.DeviceFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private int currentPage;

    public interface OnFragmentListener {
        void onViewCreated(Fragment fragment, View view);
    }

    private String mainTitle;

    private ViewPagerAdapter pageAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    protected class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Integer> mFragmentIcon = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public int getIcon(int position) {
            return mFragmentIcon.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, int icon) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mFragmentIcon.add(icon);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageAdapter = new ViewPagerAdapter(getChildFragmentManager());

        addPages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.room_fragment, container, false);

        ViewPager viewPager = getViewPager(ret);
        viewPager.setAdapter(pageAdapter);

        Context context = getContext();
        if (context instanceof OnFragmentListener) {
            OnFragmentListener listener = (OnFragmentListener) context;
            listener.onViewCreated(this, ret);
        }

        return ret;
    }

    public ViewPager getViewPager(View view) {
        return (ViewPager) view.findViewById(R.id.viewPager);
    }

    public void applyPager(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);

        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(pageAdapter.getIcon(i));
        }
        int selected = Settings.getSelectedTab(getContext(), getMainTitle());
        if (selected < tabLayout.getTabCount()) {
            currentPage = selected;
            tabLayout.getTabAt(selected).select();
        }
    }

    protected void addPages() {
    }

    protected final void addPage(Fragment fragment, String title, int iconId) {
        pageAdapter.addFragment(fragment, title, iconId);
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void selectPage(int page, boolean switched) {
        Log.i(getClass().getName(), "Selected page " + page);

        Settings.setSelectedTab(getContext(), getMainTitle(), page);

        if (currentPage != page) {
            lostFocus();

            currentPage = page;

            refresh();
        }
    }

    public void refresh() {
        Fragment fragment = pageAdapter.getItem(currentPage);
        if (fragment instanceof DeviceFragment) {
            ((DeviceFragment)fragment).refresh();
        }
    }

    public void lostFocus() {
        Fragment fragment = pageAdapter.getItem(currentPage);
        if (fragment instanceof DeviceFragment) {
            ((DeviceFragment)fragment).lostFocus();
        }
    }

    public void homePressed() {
        Fragment fragment = pageAdapter.getItem(currentPage);
        if (fragment instanceof DeviceFragment) {
            ((DeviceFragment)fragment).homePressed();
        }
    }

    public void turnOn() {
        selectPage(currentPage, false);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void turnOff() {
        RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Power_Off));
    }

    public void volumeDown(boolean pressed) {
        String command = getMainTitle() + "/" + getString(R.string.cmd_Decrease_Volume);
        if (pressed) {
            if (!command.equals(RemoteService.getActiveCommand())) {
                RemoteService.buttonDown(command);
            }
        } else {
            RemoteService.buttonUp(command);
        }
    }

    public void volumeUp(boolean pressed) {
        String command = getMainTitle() + "/" + getString(R.string.cmd_Increase_Volume);
        if (pressed) {
            if (!command.equals(RemoteService.getActiveCommand())) {
                RemoteService.buttonDown(command);
            }
        } else {
            RemoteService.buttonUp(command);
        }
    }
}

