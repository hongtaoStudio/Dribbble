package com.hunter.cookies.entity;

import com.google.gson.annotations.SerializedName;

public class BucketsEntity {

    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("shots_count")
    private int mShotsCount;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getShotsCount() {
        return mShotsCount;
    }

    public void setShotsCount(int shotsCount) {
        mShotsCount = shotsCount;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }
}
