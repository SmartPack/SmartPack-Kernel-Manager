package com.grarak.kerneladiutor.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by willi on 02.12.16.
 */

public class Server {

    private String mAddress;

    public Server(String address) {
        mAddress = address;
    }

    public String postDeviceCreate(JSONObject data) {
        HttpURLConnection connection = null;
        try {
            String text = data.toString();
            URL url = new URL(mAddress + "/kerneladiutor/api/v1/device/create");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setFixedLengthStreamingMode(text.length());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(text);
            outputStream.flush();
            outputStream.close();

            InputStream inputStream = connection.getResponseCode() == HttpURLConnection.HTTP_OK ?
                    connection.getInputStream() : connection.getErrorStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line).append("\n");
            }

            bufferedReader.close();

            return response.toString().trim();
        } catch (IOException ignored) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

}
