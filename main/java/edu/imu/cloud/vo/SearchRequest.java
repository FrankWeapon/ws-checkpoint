package edu.imu.cloud.vo;


/**
 * Created by FrankWeapon on 4/7/16.
 */
public class SearchRequest {

    public enum Type {IMAGE,AUDIO,VIDEO}

    private Type type;
    private String endpoint;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
