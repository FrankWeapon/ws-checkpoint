package edu.imu.cloud;

import edu.imu.cloud.vo.SearchRequest;
import edu.imu.cloud.vo.SearchResponse;
import edu.imu.cloud.vo.TaskInfo;
import org.junit.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.Future;

/**
 * Created by FrankWeapon on 4/7/16.
 */
public class requestHandlerTest {
    private static String endpointUrl;
    private TaskInfo task;

    @BeforeClass
    public static void beforeClass() {
        endpointUrl = "http://127.0.0.1:8080";
    }

    WebTarget target;
    boolean finished = false;

    @Test
    public void testAssign() throws Exception{
        SearchRequest request = new SearchRequest();
        request.setType(SearchRequest.Type.IMAGE);
        request.setEndpoint("10.211.55.11");
        Client client = ClientBuilder.newClient().register(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class);
        target = client.target(endpointUrl + "/master/assign");
        responseAsync(request);

        while (!finished){
            Thread.sleep(1000);
            if(!finished){
                System.out.println("calculating...");
            }
        }

    }

    public Future<SearchResponse> responseAsync(SearchRequest request) {
        return target.request(MediaType.APPLICATION_JSON_TYPE).async().post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<SearchResponse>() {
            @Override
            public void completed(SearchResponse searchResponse) {
                searchResponse.getUriList().forEach(System.out::println);
                finished = true;
            }

            @Override
            public void failed(Throwable throwable) {
                System.out.println("error!!!");
            }
        });
    }


}