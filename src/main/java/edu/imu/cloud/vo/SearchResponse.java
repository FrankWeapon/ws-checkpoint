package edu.imu.cloud.vo;

import java.util.List;

/**
 * Created by FrankWeapon on 4/7/16.
 */
public class SearchResponse {

    private List<String> uriList;

    public List<String> getUriList() {
        return uriList;
    }

    public void setUriList(List<String> uriList) {
        this.uriList = uriList;
    }

    public SearchResponse(List<String> uriList) {
        this.uriList = uriList;
    }

    public SearchResponse() {
    }
}
