package cvs.com.asyncttaskvsreact.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author IgorSteblii on 28.02.17.
 */

public class LukeSkywalker {

    @SerializedName("name")
    private String mName;
    @SerializedName("height")
    private String mHeight;
    @SerializedName("mass")
    private String mMass;
    @SerializedName("birth_year")
    private String mBirthdayYear;

    @Override
    public String toString() {
        return "[" +
                mName + ", " +
                mHeight + ", " +
                mMass + ", " +
                mBirthdayYear + "]";
    }

}
