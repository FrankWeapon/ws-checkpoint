package edu.imu.cloud.vo;

import java.util.HashMap;

/**
 * Created by FrankWeapon on 4/8/16.
 */
public class TaskInfo {

    public enum TaskStatus{SUCCESS, ERROR, WORKING}

    /**
     * jobs key: job对象位置索引, value: job当前状态
     */
    private HashMap<String,String> jobs;
    private TaskStatus status;
    private String endpoint;

    public HashMap<String, String> getJobs() {
        return jobs;
    }

    public void setJobs(HashMap<String, String> jobs) {
        this.jobs = jobs;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
