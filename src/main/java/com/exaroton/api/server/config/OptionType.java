package com.exaroton.api.server.config;

import com.google.gson.annotations.SerializedName;

public enum OptionType {
    @SerializedName("string")
    STRING,
    @SerializedName("integer")
    INTEGER,
    @SerializedName("float")
    FLOAT,
    @SerializedName("boolean")
    BOOLEAN,
    @SerializedName("multiselect")
    MULTISELECT,
    @SerializedName("select")
    SELECT,
}
