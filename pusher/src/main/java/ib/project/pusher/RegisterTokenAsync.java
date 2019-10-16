package ib.project.pusher;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;




class RegisterTokenAsync extends AsyncTask<String, Integer, PushResult> {

    private InfoListener<String> callback;
    private HashMap<String, String> headers;
    private String body = "";

    RegisterTokenAsync(InfoListener<String> callback, HashMap<String, String> headers, String body) {
        this.callback = callback;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
//        NetworkInfo networkInfo = callback.getActiveNetworkInfo();
//        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo
//                .getType() != ConnectivityManager.TYPE_WIFI && networkInfo
//                .getType() != ConnectivityManager.TYPE_MOBILE)) {
//            // If no connectivity, cancel task and update Callback with null data.
//            callback.updateFromPusher(null);
//            cancel(true);
//        }
    }


    @Override
    protected PushResult doInBackground(String... urls) {
        PushResult results = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            try {
                URL url = new URL(urls[0]);
                String resultVal = postToken(url);
                results = new PushResult(resultVal);
            } catch (Exception e) {
                results = new PushResult(e);
            }
        }

        return results;
    }

    private String postToken(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("POST");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Set Headers If exist
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // Set body
            byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            // Open communications link (network traffic occurs here).
            connection.connect();
            publishProgress(InfoListener.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            publishProgress(InfoListener.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 5000.
                result = readStream(stream, 5000);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream, int maxReadSize)
            throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuilder buffer = new StringBuilder();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(PushResult result) {
        if (result != null && callback != null) {
            if (result.getException() != null) {
                callback.updateFromPusher(result.getException().getMessage());
            } else if (result.getResultValue() != null) {
                callback.updateFromPusher(result.getResultValue());
            }
            callback.finishRequest();
        }
    }
}
