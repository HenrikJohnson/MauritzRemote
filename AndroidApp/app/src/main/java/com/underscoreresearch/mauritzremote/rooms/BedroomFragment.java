package com.underscoreresearch.mauritzremote.rooms;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.rooms.common.AmazonTVFragment;
import com.underscoreresearch.mauritzremote.rooms.common.CableSearchTopFragment;
import com.underscoreresearch.mauritzremote.rooms.common.MediaCenterFragment;
import com.underscoreresearch.mauritzremote.rooms.common.TvFragment;

public class BedroomFragment extends MainFragment {

    public BedroomFragment() {
        setMainTitle("Bedroom");
    }

    @Override
    protected void addPages() {
        addPage(new CableSearchTopFragment.Bedroom(), "Cable", R.drawable.ic_cable_white);
        addPage(new CableSearchTopFragment.Bedroom(), "Netflix", R.drawable.ic_netflix_white);
        addPage(new CableSearchTopFragment.Bedroom(), "Disney", R.drawable.ic_disney_white);
    }

    @Override
    public void selectPage(int page, boolean switched) {
        super.selectPage(page, switched);

        if (!switched) {
            switch (page) {
                case 0:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Cable_On));
                    break;
                case 1:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Netflix_On));
                    break;
                case 2:
                    RemoteService.issueRemote(getMainTitle() + "/" + getString(R.string.cmd_Turn_Disney_On));
                    break;
            }
        }
    }
}

