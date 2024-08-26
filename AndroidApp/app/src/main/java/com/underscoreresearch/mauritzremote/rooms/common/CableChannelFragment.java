package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;

public class CableChannelFragment extends DeviceFragment {
    private void setupButtons(View view) {
        setupButton(view, R.id.btn_channel_1, R.string.cmd_Channel_ABC);
        setupButton(view, R.id.btn_channel_2, R.string.cmd_Channel_CBS);
        setupButton(view, R.id.btn_channel_3, R.string.cmd_Channel_NBC);
        setupButton(view, R.id.btn_channel_4, R.string.cmd_Channel_Fox);
        setupButton(view, R.id.btn_channel_5, R.string.cmd_Channel_Bravo);
        setupButton(view, R.id.btn_channel_6, R.string.cmd_Channel_MSNBC);
        setupButton(view, R.id.btn_channel_7, R.string.cmd_Channel_CNN);
        setupButton(view, R.id.btn_channel_8, R.string.cmd_Channel_BBC_World_News);
        setupButton(view, R.id.btn_channel_9, R.string.cmd_Channel_BBC_World_News);
        setupButton(view, R.id.btn_channel_10, R.string.cmd_Channel_HGTV);
        setupButton(view, R.id.btn_channel_11, R.string.cmd_Channel_E);
        setupButton(view, R.id.btn_channel_12, R.string.cmd_Channel_Travel_Channel);
        setupButton(view, R.id.btn_channel_13, R.string.cmd_Channel_Disney);
        setupButton(view, R.id.btn_channel_14, R.string.cmd_Channel_Disney_Junior);
        setupButton(view, R.id.btn_channel_15, R.string.cmd_Channel_Disney_XD);
        setupButton(view, R.id.btn_channel_16, R.string.cmd_Channel_Sy_Fy);
        setupButton(view, R.id.btn_channel_17, R.string.cmd_Channel_Discovery_Channel);
    }

    public static class Livingroom extends CableChannelFragment {
        public Livingroom() {
            setRoom("Livingroom");
        }
    }
    public static class Office extends CableChannelFragment {
        public Office() {
            setRoom("Office");
        }
    }
    public static class Zone2 extends CableChannelFragment {
        public Zone2() {
            setRoom("Zone2");
        }
    }

    public CableChannelFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.piece_channels, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
