package cvs.com.asyncttaskvsreact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import cvs.com.asyncttaskvsreact.connection.AsyncTaskClient;
import cvs.com.asyncttaskvsreact.connection.IHttpResponse;
import cvs.com.asyncttaskvsreact.connection.ReactClient;
import cvs.com.asyncttaskvsreact.dto.DeathStar;
import cvs.com.asyncttaskvsreact.dto.LukeSkywalker;
import cvs.com.asyncttaskvsreact.dto.YavinFour;

public class MainActivity extends AppCompatActivity {

    private static final int ASYNC_CLIENT = 1;
    private static final int REACT_CLIENT = 2;

    private int mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupUi();
    }

    private void setupUi() {
        final TextView result = (TextView) findViewById(R.id.result);
        final EditText urlInput = (EditText) findViewById(R.id.enter_url);
        ((RadioGroup) findViewById(R.id.radio_group_client))
                .setOnCheckedChangeListener(
                        getRadioGroupListener()
                );
        findViewById(R.id.execute).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = urlInput.getText().toString();
                        if (!TextUtils.isEmpty(url)) {
                            execute(url, String.class, getStringResponseListener(result));
                        }
                    }
                }
        );
        //Start Wars Api buttons
        findViewById(R.id.luke_skywalker_execute).setOnClickListener(getStarWarsApiListener(result));
        findViewById(R.id.yavin_four_execute).setOnClickListener(getStarWarsApiListener(result));
        findViewById(R.id.death_star_execute).setOnClickListener(getStarWarsApiListener(result));
    }


    /**
     * To show implementation of responce deserialization
     * with {@link com.google.gson.Gson} provided example with usage of
     * <a href="https://swapi.co/"> Star Wars API</>.
     * DTO like {@link LukeSkywalker} corespond to Json response
     * from {@link StarWarsApiConstant#LUKE_SKYWALKER} URL
     *
     * @see LukeSkywalker
     * @see YavinFour
     * @see DeathStar
     */
    @NonNull
    private View.OnClickListener getStarWarsApiListener(final TextView result) {
        return new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View view) {
                Class clazz = null;
                String url = null;
                switch (view.getId()) {
                    case R.id.luke_skywalker_execute:
                        url = StarWarsApiConstant.LUKE_SKYWALKER;
                        clazz = LukeSkywalker.class;
                        break;
                    case R.id.yavin_four_execute:
                        url = StarWarsApiConstant.YAVIN_FOUR;
                        clazz = YavinFour.class;
                        break;
                    case R.id.death_star_execute:
                        url = StarWarsApiConstant.DEATH_STAR;
                        clazz = DeathStar.class;
                        break;
                }
                execute(url, clazz, new IHttpResponse<Object>() {
                    @Override
                    public void onSuccess(@Nullable Object response) {
                        if (response != null) {
                            result.setText(response.toString());
                        }
                    }

                    @Override
                    public void onFailed(@Nullable String errorMessage) {
                        result.setText(errorMessage);
                    }
                });
            }
        };
    }

    @NonNull
    private RadioGroup.OnCheckedChangeListener getRadioGroupListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_group_async_task:
                        mClient = ASYNC_CLIENT;
                        break;
                    case R.id.radio_group_react:
                        mClient = REACT_CLIENT;
                }
            }
        };
    }

    /**
     * Considering that we don't know return type of request we pass {@code String.class}
     * as a return type.
     *
     * @see cvs.com.asyncttaskvsreact.connection.BaseHttpUtils#getResponseWrapper(Class, String)
     */
    @NonNull
    private IHttpResponse<String> getStringResponseListener(final TextView result) {
        return new IHttpResponse<String>() {
            @Override
            public void onSuccess(@Nullable String response) {
                result.setText(response);
            }

            @Override
            public void onFailed(@Nullable String errorMessage) {
                result.setText(errorMessage);
            }
        };
    }

    /**
     * Do execute async request base on  {@link #mClient} value
     *
     * @see AsyncTaskClient
     * @see ReactClient
     */
    private <T> void execute(String url, Class<T> clazz, IHttpResponse<T> responseListener) {
        if (mClient == ASYNC_CLIENT) {
            AsyncTaskClient.doRequest(url, clazz, responseListener);
        } else if (mClient == REACT_CLIENT) {
            ReactClient.doRequest(url, clazz, responseListener);
        }
    }

}
