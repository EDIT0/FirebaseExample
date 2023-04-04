package com.example.firebasecloudmessaging1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FCMSender {

    final String TAG = FCMSender.class.getSimpleName();

    final String serverKey = "server key";

    private static FCMSender fcmSender;
    Context context;

    JsonObject jsonObj = new JsonObject();
    Gson gson = new Gson();

    public FCMSender(Context context) {
        this.context = context;
    }

    public static FCMSender getInstance(Context context) {
        if (fcmSender == null) {
            fcmSender = new FCMSender(context);
        }

        return fcmSender;
    }

    /**
     * 데이터 메시지는 포그라운드, 백그라운드에 있든 무조건 내부 코드로 처리
     * */
    public void sendDataMessage(String token, String type, String title, String message) {
        Log.i(TAG, "onNewToken sendFcm !!!");

        //메시지 가공
        JsonElement jsonElement = gson.toJsonTree(token);
        jsonObj.add("to", jsonElement);

        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("message", message);
        data.addProperty("type", type);
        jsonObj.add("data", data);

        sendMessage();
    }

    /**
     * 알림 메시지는
     * 포그라운드 일 경우 내부 코드로 처리하고,
     * 백그라운드 일 경우 기본 알림
     * */
    public void sendAlarmMessage(String token, String type, String title, String message) {
        Log.i(TAG, "onNewToken sendFcm !!!");

        //메시지 가공
        JsonElement jsonElement = gson.toJsonTree(token);
        jsonObj.add("to", jsonElement);

        JsonObject notification = new JsonObject();
        notification.addProperty("title", title);
        notification.addProperty("body", message);
//        notification.addProperty("type", type);
        jsonObj.add("notification", notification);

        sendMessage();
    }

    public void sendMessage() {
        /**
         * 통신
         * */
        final MediaType mediaType = MediaType.parse("application/json");
        OkHttpClient httpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .addHeader("Authorization", "key=" + serverKey)
                    .post(RequestBody.create(mediaType, jsonObj.toString())).build();

            Log.i(TAG, jsonObj.toString());

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    Log.i(TAG, "onNewToken onFailure() " + e);
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    Log.i(TAG, "onNewToken onResponse() " + response.message());
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "Error in sending message to FCM server " + e);
        }
    }
}
