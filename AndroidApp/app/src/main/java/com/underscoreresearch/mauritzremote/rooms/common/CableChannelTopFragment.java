package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class CableChannelTopFragment extends DeviceFragment {
    private String room;
    private VerticalViewPager pager;
    private ViewPagerAdapter adapter;

    protected void setRoom(String room) {
        this.room = room;
    }

    protected String getRoom() {
        return room;
    }

    public static class Livingroom extends CableChannelTopFragment {
        public Livingroom() {
            setRoom("Livingroom");
        }
    }
    public static class Office extends CableChannelTopFragment {
        public Office() {
            setRoom("Office");
        }
    }
    public static class Zone2 extends CableChannelTopFragment {
        public Zone2() {
            setRoom("Zone2");
        }
    }

    protected class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public CableChannelTopFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.media_center_fragment, container, false);

        return ret;
    }

    @Override
    public void homePressed() {
        if (pager != null) {
            pager.setCurrentItem(0);
        }
        super.homePressed();
    }

    @Override
    public void lostFocus() {
        if (pager != null) {
            ((DeviceFragment)adapter.getItem(1)).lostFocus();
            pager.setCurrentItem(0);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (adapter == null) {
            adapter = new ViewPagerAdapter(getChildFragmentManager());
            CableFragment controlFragment = new CableFragment();
            controlFragment.setRoom(getRoom());
            adapter.addFragment(controlFragment, "Control");

            CableChannelFragment libraryFragment = new CableChannelFragment();
            libraryFragment.setRoom(getRoom());
            adapter.addFragment(libraryFragment, "Channels");
        }

        pager = (VerticalViewPager) view.findViewById(R.id.viewPagerMediaCenter);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                RemoteService.cancelButton();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != ViewPager.SCROLL_STATE_IDLE) {
                    RemoteService.cancelButton();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.i("Doh", "Hidden changed");
    }

    @Override
    public void onDestroyView() {
        if (pager != null) {
            pager.setAdapter(null);
            pager.setOnPageChangeListener(null);
            pager = null;
        }

        super.onDestroyView();
    }
}
