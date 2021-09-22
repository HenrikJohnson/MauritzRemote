package com.underscoreresearch.mauritzremote.rooms.common;

import androidx.fragment.app.Fragment;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;

import mehdi.sakout.fancybuttons.FancyButton;

public class DeviceFragment extends Fragment {
    private View.OnTouchListener buttonListener;
    private String room;

    protected void setRoom(String room) {
        this.room = room;
    }

    protected String getRoom() {
        return room;
    }

    protected FancyButton setupButton(View view, int viewId, int tagStringId) {
        FancyButton child = (FancyButton) view.findViewById(viewId);
        child.setTag(room + "/" + getString(tagStringId));
        return child;
    }

    protected FancyButton setupButton(View view, int viewId, int tagStringId, String title) {
        FancyButton child = (FancyButton) view.findViewById(viewId);
        child.setTag(room + "/" + getString(tagStringId));
        child.setText(title);
        return child;
    }

    public DeviceFragment() {
        buttonListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RemoteService.buttonDown((String) v.getTag());
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    RemoteService.buttonUp((String) v.getTag());
                }
                return false;
            }
        };
    }

    protected void attachButtonCallbacks(ViewGroup parent) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if ((child instanceof Button || child instanceof FancyButton)) {
                if (child.getTag() instanceof String ) {
                    if (((String)child.getTag()).length() > 0) {
                        child.setOnTouchListener(buttonListener);
                    }
                } else if (child.getTag() == null) {
                    child.setVisibility(View.INVISIBLE);
                }
            }
            if (child instanceof ViewGroup) {
                attachButtonCallbacks((ViewGroup) child);
            }
        }
    }

    public void refresh() {
    }

    public void lostFocus() {
    }

    public void homePressed() {
    }
}
