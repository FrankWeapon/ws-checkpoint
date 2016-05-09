package edu.imu.cloud.controller.worker;

import edu.imu.cloud.vo.TaskInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created by FrankWeapon on 4/10/16.
 */
public class WorkHandlerTest {

    WebTarget target;
    TaskInfo task;
    static String endpointUri;

    @BeforeClass
    public static void beforeClass() {
        endpointUri = "http://localhost:8080";
    }

    @Test
    public void testSearch() throws Exception {
        task = new TaskInfo();
        task.setStatus(TaskInfo.TaskStatus.WORKING);
        HashMap<String, String> jobs = new HashMap<>();
        jobs.put("1", "0");
        jobs.put("2", "0");
        jobs.put("3", "0");
        task.setJobs(jobs);

        Client client = ClientBuilder.newClient().register(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class);
        target = client.target(endpointUri + "/worker/search");
        responseAsync(task);
        while (task.getStatus() == TaskInfo.TaskStatus.WORKING) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(task.getStatus() == TaskInfo.TaskStatus.WORKING)
            System.out.println(endpointUri + " is running!");
        }

        assertEquals(TaskInfo.TaskStatus.SUCCESS,task.getStatus());
    }

    public Future<TaskInfo> responseAsync(TaskInfo taskInfo) {
        return target.request(MediaType.APPLICATION_JSON_TYPE).async().post(Entity.entity(taskInfo, MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<TaskInfo>() {
            @Override
            public void completed(TaskInfo param) {
                task = param;
                System.out.println("succeed!!!");
            }

            @Override
            public void failed(Throwable throwable) {
                task.setStatus(TaskInfo.TaskStatus.ERROR);
                System.out.println("error!!!");
            }
        });
    }
}