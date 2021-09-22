package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.underscoreresearch.mauritzremote.R;

public class CableFragment extends DeviceFragment {
    private void setupButtons(View view) {
        setupButton(view, R.id.btn_menu, R.string.cmd_Go_To_Home_In_Cable, "Home");
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_Cable);
        setupButton(view, R.id.btn_context, R.string.cmd_Search_In_Cable, "Search");
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_Cable);
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_Cable);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_Cable);
        setupButton(view, R.id.btn_info, R.string.cmd_Show_Info_In_Cable, "Info");
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_Cable);
        setupButton(view, R.id.btn_back, R.string.cmd_Go_Back_In_Cable, "Back");

        setupButton(view, R.id.btn_netflix, R.string.cmd_Go_To_Netflix_In_Cable);
        setupButton(view, R.id.btn_hulu, R.string.cmd_Go_To_Hulu_In_Cable);
        setupButton(view, R.id.btn_disney, R.string.cmd_Go_To_Disney_In_Cable);
        setupButton(view, R.id.btn_plex, R.string.cmd_Go_To_Plex_In_Cable);
        setupButton(view, R.id.btn_amazon, R.string.cmd_Go_To_Amazon_In_Cable);
        setupButton(view, R.id.btn_apple, R.string.cmd_Go_To_Apple_In_Cable);
        setupButton(view, R.id.btn_discovery, R.string.cmd_Go_To_Discovery_In_Cable);
        setupButton(view, R.id.btn_hbo, R.string.cmd_Go_To_HBO_In_Cable);

        setupButton(view, R.id.btn_mute, R.string.cmd_Mute);

        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Cable);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Cable);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Cable);
    }

    public static class Livingroom extends CableFragment {
        public Livingroom() {
            setRoom("Livingroom");
        }
    }
    public static class Office extends CableFragment {
        public Office() {
            setRoom("Office");
        }
    }
    public static class Zone2 extends CableFragment {
        public Zone2() {
            setRoom("Zone2");
        }
    }

    public CableFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.cable_fragment, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
