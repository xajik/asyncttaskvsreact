package cvs.com.asyncttaskvsreact.connection;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.doConnection;
import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.getQuery;
import static cvs.com.asyncttaskvsreact.connection.BaseHttpUtils.getResponseWrapper;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author IgorSteblii on 28.02.17.
 *         <p>
 *         Reactive java, {@link Observable} wrapper for Http task execution
 */

public class ReactClient {

    /**
     * Execute http request as async task without parameters
     *
     * @see #doRequest(String, Class, List, IHttpResponse)
     */
    public static <T> void doRequest(@NonNull String baseUrl,
                                     @NonNull final Class<T> clazz,
                                     final IHttpResponse<T> responseListener) {
        doRequest(baseUrl, clazz, null, responseListener);
    }

    /**
     * Execute http request as async task
     *
     * @param clazz            - return class type
     * @param parameters       - required params key + value
     * @param responseListener - response callback
     */
    private static <T> void doRequest(@NonNull final String baseUrl,
                                      @NonNull final Class<T> clazz,
                                      final List<Pair<String, String>> parameters,
                                      final IHttpResponse<T> responseListener) {
        Observable.defer(
                new Callable<ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> call() throws Exception {
                        return getResponseWrapperObservable(baseUrl, clazz, parameters);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(getOnNext(responseListener), getOnError(responseListener));
    }

    @NonNull
    private static <T> Consumer<Throwable> getOnError(final IHttpResponse<T> listener) {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (listener != null) {
                    listener.onFailed(throwable.getMessage());
                }
            }
        };
    }

    @NonNull
    private static <T> Consumer<T> getOnNext(final IHttpResponse<T> listener) {
        return new Consumer<T>() {
            @Override
            public void accept(T response) throws Exception {
                if (listener != null) {
                    listener.onSuccess(response);
                }
            }
        };
    }

    private static <T> ObservableSource<T> getResponseWrapperObservable(
            @NonNull String baseUrl,
            @NonNull final Class<T> clazz,
            final List<Pair<String, String>> parameters) {
        String query = getQuery(parameters);
        Pair<Integer, String> connectionResult = doConnection(baseUrl, query);
        final int mResponseCode = connectionResult.first;
        if (mResponseCode == HTTP_OK) {
            T rawResponse = getResponseWrapper(clazz, connectionResult.second);
            return Observable.just(rawResponse);
        }
        String errorMessage = connectionResult.second;
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = "Failed to execute " + baseUrl;
        }
        throw new RuntimeException(errorMessage);
    }


}
