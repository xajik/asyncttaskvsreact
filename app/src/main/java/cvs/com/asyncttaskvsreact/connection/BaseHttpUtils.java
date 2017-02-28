package cvs.com.asyncttaskvsreact.connection;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author IgorSteblii on 28.02.17.
 *         <p>
 *         Common Utils for {@link AsyncTaskClient} and {@link ReactClient}
 */

class BaseHttpUtils {

    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Build valid URI from {@code List<Pair<String, String>>} pair
     *
     * @param parameters key value pair of request parameters
     */
    @Nullable
    static String getQuery(@Nullable List<Pair<String, String>> parameters) {
        if (parameters == null) {
            return null;
        }
        Uri.Builder builder = new Uri.Builder();
        for (Pair<String, String> p : parameters) {
            builder.appendQueryParameter(p.first, p.second);
        }

        return builder.build().getEncodedQuery();
    }

    /**
     * Deserialize raw string response to desired class with {@link Gson}
     *
     * @param clazz       to de deserialized into
     * @param rawResponse string response from server
     * @return object of desired class or String if clazz is String.class
     */
    @SuppressWarnings("unchecked")
    static <T> T getResponseWrapper(final Class<T> clazz, String rawResponse) {
        if (clazz == String.class) {
            return (T) rawResponse;
        }
        try {
            return GSON.fromJson(rawResponse, clazz);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException(clazz.toString() + ", Raw response " + rawResponse, e);
        }
    }

    /**
     * Open  {@link HttpURLConnection} connection with desired URL.
     *
     * @param url   to connect to
     * @param query key value pair of parameters from {@link #getQuery(List)}
     * @return {@code Pair<Integer, String> } first - is connection response code, second is raw response
     */
    static Pair<Integer, String> doConnection(@NonNull String url, @Nullable String query) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        int responseCode = -1;
        String errorMessage = null;
        try {
            //if we don't have output stream - use GET otherwise POST
            if (TextUtils.isEmpty(query)) {
                connection = doGet(url);
            } else {
                connection = doPost(url, query);
            }
            responseCode = connection.getResponseCode();
            if (responseCode == HTTP_OK) {
                inputStream = connection.getInputStream();
                return readInputStream(responseCode, inputStream);
            } else if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST &&
                    responseCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {
                inputStream = connection.getErrorStream();
                return readInputStream(responseCode, inputStream);
            }
        } catch (IOException e) {
            errorMessage = e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return Pair.create(responseCode, errorMessage);
    }

    @NonNull
    private static Pair<Integer, String> readInputStream(int responseCode, InputStream inputStream) throws IOException {
        String response = "";
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            response += line;
        }
        return Pair.create(responseCode, response);
    }

    /**
     * Execute POST request
     *
     * @see <a href="http://google.com">https://developer.android.com/reference/java/net/HttpURLConnection.html</a>
     */
    private static HttpURLConnection doPost(String url, String query) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(url, RequestHeader.RequestType.POST.name());
        connection.setDoOutput(true);
        final boolean doInput = !TextUtils.isEmpty(query);
        if (doInput) {
            connection.setDoInput(true);
            connection.setFixedLengthStreamingMode(query.getBytes().length);
        }
        connection.connect();
        if (doInput) {
            OutputStream outputStream = connection.getOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8.displayName());
            writer.write(query);
            writer.flush();
            writer.close();
        }
        return connection;
    }

    /**
     * Execute GET request
     *
     * @see <a href="http://google.com">https://developer.android.com/reference/java/net/HttpURLConnection.html</a>
     */
    private static HttpURLConnection doGet(String url) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(url, RequestHeader.RequestType.GET.name());
        connection.connect();
        return connection;
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String baseUrl, String name) throws IOException {
        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(HttpConstants.CONNECT_TIMEOUT);
        connection.setReadTimeout(HttpConstants.READ_TIMEOUT);
        connection.setRequestMethod(name);
        connection.setRequestProperty(HttpConstants.CONTENT_TYPE, getContentTypeValue());
        return connection;
    }

    @NonNull
    private static String getContentTypeValue() {
        return RequestHeader.ContentType.X_WWW_FORM_URLENCODED.getType() + ";" +
                RequestHeader.SupportedCharset.UTF_8.getCharset();
    }

}
