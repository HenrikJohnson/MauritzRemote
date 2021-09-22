package com.underscoreresearch.mauritzremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.underscoreresearch.mauritzremote.config.Constants;
import com.underscoreresearch.mauritzremote.config.Settings;

import java.util.HashMap;
import java.util.Map;

public class RemoteService {
    private static final long FIRST_DELAY = 200;
    private static final long SECOND_DELAY = 500;
    private static final long REPEAT_DELAY = 50;
    private static Handler handler;
    private static RequestQueue queue;
    private static String password;
    private static String currentRoom;

    public static void setCurrentRoom(String currentRoom) {
        RemoteService.currentRoom = currentRoom;
    }

    public static String getCurrentRoom() {
        return currentRoom;
    }

    private static class AuthenticatedStringRequest extends StringRequest {
        public AuthenticatedStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> params = new HashMap<String, String>();
            String creds = String.format("%s:%s", Constants.SERVER_LOGIN, password);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            params.put("Authorization", auth);
            return params;
        }
    }

    private static class AuthenticatedBytearrayRequest extends Request<byte[]> {
        private final Response.Listener<byte[]> mListener;

        public AuthenticatedBytearrayRequest(int method, String url, Response.Listener<byte[]> response, Response.ErrorListener listener) {
            super(method, url, listener);

            this.mListener = response;
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(byte[] response) {
            if (mListener != null)
                mListener.onResponse(response);
        }
    }

    private static Runnable repeatButton;

    public static void initialize(Context context) {
        if (handler == null) {
            handler = new Handler();

            repeatButton = new Runnable() {
                @Override
                public void run() {
                    if (activeCommand != null) {
                        RemoteService.issueIdle(activeCommand);
                        handler.postDelayed(repeatButton, REPEAT_DELAY);
                    }
                }
            };
        }

        password = Settings.getPassword(context);

        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public static RequestQueue getQueue() {
        return queue;
    }

    public static void setPassword(Context context, String newPassword) {
        password = newPassword;
        Settings.setPassword(context, password);
    }

    private static String activeCommand;
    private static boolean firstCancelled;

    public static void cancelButton()
    {
        firstCancelled = true;
        handler.removeCallbacks(repeatButton);
        activeCommand = null;
    }

    public static void buttonUp(String destination) {
        Log.i("RemoteService", "Button up " + destination);

        if (destination.equals(activeCommand)) {
            handler.removeCallbacks(repeatButton);
            activeCommand = null;
        }
    }

    public static String getActiveCommand() {
        return activeCommand;
    }

    public static void buttonDown(final String destination) {
        Log.i("RemoteService", "Button down " + destination);

        if (activeCommand != null) {
            handler.removeCallbacks(repeatButton);
        }
        activeCommand = destination;
        firstCancelled = false;
        handler.postDelayed(repeatButton, SECOND_DELAY);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!firstCancelled) {
                    issueRemote(destination);
                }
            }
        }, FIRST_DELAY);
    }

    public static void issueRemote(String destination) {
        scheduleGet(Constants.SERVER_BASE + "send/" + destination);
    }

    public static void sendInput(String room, String input) {
        scheduleGet(Constants.SERVER_BASE + "input/" + room + "/" + Uri.encode(input));
    }

    public static void sendSearch(String room, String search) {
        scheduleGet(Constants.SERVER_BASE + "find/" + room + "/" + Uri.encode(search));
    }

    public static void issueIdle(String destination) {
        scheduleGet(Constants.SERVER_BASE + "idle/" + destination);
    }

    private static void scheduleGet(String url) {
        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void getRoomDevice(final Context context, final String room, final Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "room/" + room;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.GET,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                        response.onResponse(Integer.toString(Settings.getSelectedTab(context, room)));
                    }
                })
        );
    }

    public static void getActiveQueue(final String room, final Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "activequeue/" + room;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.GET,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void setRoomDevice(String room, int page) {
        String url = Constants.SERVER_BASE + "room/" + room + "/" + page;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.PUT,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void getQueueContents(String room, String queryQuery,
                                        final Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "queue/" + room + "/" + queryQuery;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.GET,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void removeQueueItem(String room, String queryQuery, int queueId,
                                       final Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "queue/" + room + "/" + queryQuery + "/" + queueId;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.DELETE,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void moveQueueItem(String room, String queryQuery, int queueId, int afterQueueId,
                                     final Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "queue/" + room + "/" + queryQuery + "/" + queueId + "/" + afterQueueId;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.PUT,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void addQueueItem(String room, String queuryQueue, String songId, Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "queue/" + room + "/" + queuryQueue + "/" + songId;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.POST,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void query(String queryRoom, String type, String criteria, int offset, int size, Response.Listener<String> response) {
        String url = Constants.SERVER_BASE + "search/" + queryRoom + "/" + type.replace(" ", "") + "/" + Uri.encode(criteria) + "?offset=" + offset + "&size=" + size;

        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedStringRequest(Request.Method.GET,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

    public static void loadData(String url, Response.Listener<byte[]> response) {
        Log.i("RemoteService", "Sending " + url);

        queue.add(new AuthenticatedBytearrayRequest(Request.Method.GET,
                        url, response, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getCause() != null) {
                            Log.e("RemoteService", error.getCause().toString());
                        } else if (error.getMessage() != null) {
                            Log.e("RemoteService", error.getMessage());
                        } else {
                            Log.e("RemoteService", "Unknown network error");
                        }
                    }
                })
        );
    }

}
