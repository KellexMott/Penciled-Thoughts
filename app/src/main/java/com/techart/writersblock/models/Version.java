package com.techart.writersblock.models;

/**
 * Created by Kelvin on 05/06/2017.
 */

public class Version {
    private String status;
    private int version;

    public Version() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
