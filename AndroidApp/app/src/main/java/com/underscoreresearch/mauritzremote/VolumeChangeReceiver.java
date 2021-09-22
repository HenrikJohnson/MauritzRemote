package com.underscoreresearch.mauritzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by henri on 3/6/2017.
 */

public class VolumeChangeReceiver extends BroadcastReceiver {
    public static boolean isTablet(Context context) {
        TelephonyManager tm= (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE;
    }

    private static boolean needMiddle;
    private static int followStreamType = -1;

    public VolumeChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isTablet(context)) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION") && RemoteService.getCurrentRoom() != null) {
                int streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 0);
                if (followStreamType == -1)
                    followStreamType = streamType;

                if (streamType == followStreamType) {
                    int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
                    int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);

                    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    int middle = audio.getStreamMaxVolume(followStreamType) / 2;

                    Log.i("VOLUME", "Volume " + oldVolume + " => " + newVolume + " (" + middle + ") fromm " + streamType);

                    if (needMiddle) {
                        if (newVolume == middle) {
                            needMiddle = false;
                        }
                    } else {
                        String command;
                        if (newVolume < oldVolume) {
                            command = RemoteService.getCurrentRoom() + "/" + context.getString(R.string.cmd_Decrease_Volume);
                        } else if (newVolume > oldVolume) {
                            command = RemoteService.getCurrentRoom() + "/" + context.getString(R.string.cmd_Increase_Volume);
                        } else {
                            return;
                        }
                        RemoteService.issueIdle(command);
                        needMiddle = true;
                    }
                    audio.setStreamVolume(followStreamType, middle, AudioManager.FLAG_SHOW_UI);
                }
            }
        }
    }
}