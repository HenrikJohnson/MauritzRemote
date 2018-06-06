package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.rooms.common.CableFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class BedroomFragment extends MainFragment {

    public BedroomFragment() {
        setMainTitle("Bedroom");
    }

    @Override
    protected void addPages() {
        addPage(new MediaCenterFragment.Bedroom(), "Media", R.drawable.ic_mediacenter_white);
        addPage(new CableFragment.Bedroom(), "Cable", R.drawable.ic_cable_white);
        addPage(new CableFragment.Bedroom(), "Netflix", R.drawable.ic_netflix_white);
        addPage(new TvFragment.Bedroom(), "TV", R.drawable.ic_tv_white);
    }

    @Override
    public void selectPage(int page, boolean switched) {
        super.selectPage(page, switched);

        switch(page) {
            case 0:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Media_Center_On));
                break;
            case 1:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Cable_On));
                break;
            case 2:
                RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Netflix_On));
                break;
            case 3:
                if (!switched) {
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_TV_On));
                }
                break;
        }
    }

    @Override
    public void turnOn() {
        RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_TV_On));
    }
}

