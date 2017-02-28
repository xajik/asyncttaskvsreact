package cvs.com.asyncttaskvsreact.connection;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.List;

import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.doConnection;
import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.getQuery;
import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.getResponseWrapper;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author IgorSteblii on 28.02.17.
 *         <p>
 *         {@link AsyncTask} wrapper for Http connectio execution
 */

public class AsyncTaskClient {

    /**
     * Execute http request as async task without parameters
     *
     * @see #doRequest(String, Class, List, IHttpResponse)
     */
    public static <T> void doRequest(@NonNull final String baseUrl,
                                     @NonNull final Class<T> clazz,
                                     final IHttpResponse<T> responseListener) {
        doRequest(baseUrl, clazz, null, responseListener);
    }

    /**
     * Execute http request as async task
     *
     * @param baseUrl          - url to call
     * @param clazz            - return class type
     * @param parameters       - required params fro post + must have token
     * @param responseListener - response callback
     */
    private static <T> void doRequest(@NonNull final String baseUrl,
                                      @NonNull final Class<T> clazz,
                                      final List<Pair<String, String>> parameters,
                                      final IHttpResponse<T> responseListener) {
        new AsyncTask<Void, Void, T>() {

            private String errorMessage;

            @Override
            protected T doInBackground(Void... voids) {
                String query = getQuery(parameters);
                Pair<Integer, String> connectionResult = doConnection(baseUrl, query);
                if (connectionResult.first == HTTP_OK) {
                    return getResponseWrapper(clazz, connectionResult.second);
                }
                errorMessage = connectionResult.second;
                return null;
            }

            @Override
            protected void onPostExecute(@Nullable T response) {
                if (errorMessage != null) {
                    responseListener.onFailed(errorMessage);
                } else {
                    responseListener.onSuccess(response);
                }
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
