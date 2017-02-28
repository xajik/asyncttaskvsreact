package cvs.com.asyncttaskvsreact.dto;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * @author IgorSteblii on 28.02.17.
 */

@Keep
public class DeathStar {

    @SerializedName("name")
    private String mName;
    @SerializedName("model")
    private String mModel;
    @SerializedName("manufacturer")
    private String mManufacturer;
    @SerializedName("cost_in_credits")
    private String mCostInCredits;

    @Override
    public String toString() {
        return "[" +
                mName + ", " +
                mModel + ", " +
                mManufacturer + ", " +
                mCostInCredits + "]";
    }
}
