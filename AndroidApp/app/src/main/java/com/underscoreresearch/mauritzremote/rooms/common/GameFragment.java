package com.underscoreresearch.mauritzremote.rooms.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.underscoreresearch.mauritzremote.R;

public class GameFragment extends DeviceFragment {
    private void setupButtons(View view) {
        setupButton(view, R.id.btn_xbox, R.string.cmd_Go_To_Xbox_In_Game);
        setupButton(view, R.id.btn_playstation, R.string.cmd_Go_To_Playstation_In_Game);
        setupButton(view, R.id.btn_wii, R.string.cmd_Go_To_Wii_In_Game);
        setupButton(view, R.id.btn_mac, R.string.cmd_Go_To_Mac_In_Game);
        setupButton(view, R.id.btn_mac2, R.string.cmd_Go_To_Mac_Secondary_In_Game);

        setupButton(view, R.id.btn_menu, R.string.cmd_Show_Menu_In_Media_Center, "Menu");
        setupButton(view, R.id.btn_up, R.string.cmd_Go_Up_In_Media_Center);
        setupButton(view, R.id.btn_context, R.string.cmd_Show_Context_Menu_In_Media_Center, "Context");
        setupButton(view, R.id.btn_left, R.string.cmd_Go_Left_In_Media_Center);
        setupButton(view, R.id.btn_select, R.string.cmd_Select_In_Media_Center);
        setupButton(view, R.id.btn_right, R.string.cmd_Go_Right_In_Media_Center);
        setupButton(view, R.id.btn_info, R.string.cmd_Show_Info_In_Media_Center, "Info");
        setupButton(view, R.id.btn_down, R.string.cmd_Go_Down_In_Media_Center);
        setupButton(view, R.id.btn_back, R.string.cmd_Go_Back_In_Media_Center, "Back");

        setupButton(view, R.id.btn_skip_back, R.string.cmd_Step_Back_In_Media_Center);
        setupButton(view, R.id.btn_skip_forward, R.string.cmd_Step_Forward_In_Media_Center);
        setupButton(view, R.id.btn_mute, R.string.cmd_Mute);

        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Media_Center);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Media_Center);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Media_Center);
        setupButton(view, R.id.btn_skip, R.string.cmd_Go_To_Next_In_Media_Center);
    }

    public static class Livingroom extends GameFragment {
        public Livingroom() {
            setRoom("Livingroom");
        }
    }
    public static class Office extends GameFragment {
        public Office() {
            setRoom("Office");
        }
    }
    public static class Zone2 extends GameFragment {
        public Zone2() {
            setRoom("Zone2");
        }
    }

    public GameFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.game_fragment, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }
}
