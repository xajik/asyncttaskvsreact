package cvs.com.asyncttaskvsreact.connection;

import android.support.annotation.Nullable;

/**
 * @author IgorSteblii on 01.10.16.
 */
public interface IHttpResponse<T> {

    void onSuccess(@Nullable T response);

    void onFailed(@Nullable String errorMessage);

}
