package cvs.com.asyncttaskvsreact.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author IgorSteblii on 28.02.17.
 */

public class YavinFour {

    @SerializedName("name")
    private String mName;
    @SerializedName("rotation_period")
    private String mRotationPeriod;
    @SerializedName("diameter")
    private String mDiameter;
    @SerializedName("climate")
    private String mClimate;

    @Override
    public String toString() {
        return "[" +
                mName + ", " +
                mRotationPeriod + ", " +
                mDiameter + ", " +
                mClimate + "]";
    }

}
