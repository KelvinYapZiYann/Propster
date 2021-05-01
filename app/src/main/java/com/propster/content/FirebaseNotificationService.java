package com.propster.content;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.propster.R;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        System.out.println("onNewToken token = " + token);
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("token", token);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_UPDATE_FCM_TOKEN, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("onMessageReceived from ==> " + remoteMessage.getFrom());
//        System.out.println("onMessageReceived ==> " + new String(remoteMessage.getRawData()));
//        Map<String, String> map = remoteMessage.getData();
//        for (String key : map.keySet()) {
//            System.out.println("key ==> " + key);
//        }
        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(getPackageName(), getPackageName(), NotificationManager.IMPORTANCE_DEFAULT));
        }
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this, getPackageName());
        notificationCompatBuilder.setSmallIcon(R.drawable.notification_bell_ic);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification == null) {
            return;
        }
        notificationCompatBuilder.setContentTitle(notification.getTitle());
        notificationCompatBuilder.setContentText(notification.getBody());
        notificationCompatBuilder.setContentIntent(notificationPendingIntent);
        notificationManager.notify(0, notificationCompatBuilder.build());

    }
}
