package edu.imu.cloud.controller.master;

import edu.imu.cloud.util.FileUtil;
import edu.imu.cloud.vo.SearchRequest;
import edu.imu.cloud.vo.SearchResponse;
import edu.imu.cloud.vo.TaskInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by FrankWeapon on 4/7/16.
 */
@Path("/master")
public class TaskController {

    private List<String> workerAddress = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private String endpoint;
    private List<String> fileNameList;
    //保存结果
    List<String> matchedUris = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(TaskController.class);



    /**
     * master节点收到请求，解析并分配任务给workerAddress中所有worker
     *
     * @param request
     * @return
     */
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/assign")
    public Response assign(SearchRequest request) {

        this.endpoint = request.getEndpoint();

        fileNameList = getFileNameList(endpoint, request.getType());

        workerAddress.add("http://localhost:8080");
        logger.info("assigning task...");

        int totalCount = fileNameList.size();

        logger.info("number of files in " + request.getEndpoint() + " is " + totalCount);

        int index = 0;
        for (String address : workerAddress) {
            Worker worker = new Worker();
            worker.endpointUri = address;
            worker.webClient = createClient(address);
            worker.task = createTask(totalCount, index);
            logger.info("the worker " + index + " assigned jobs");
            index++;
            workers.add(worker);
        }

        logger.info("start workers...");

        //启动所有工作节点等等待线程，异步接受结果数据
        workers.forEach(TaskController.Worker::run);


        while (!isReturnable()){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        SearchResponse response = new SearchResponse();
        response.setUriList(matchedUris);
        return Response.ok().entity(response).build();
    }

    private List<String> getFileNameList(String uri, SearchRequest.Type type){
        //TODO: 设置type对应扩展文件筛选文件
        FileUtil fileUtil = new FileUtil(uri);
        return fileUtil.getFileNameList();
    }

    private boolean isReturnable(){
        for(Worker worker : workers){
            if(worker.task.getStatus() != TaskInfo.TaskStatus.SUCCESS){
                return false;
            }
        }
        return true;
    }

    private WebClient createClient(String uri) {
        List<Object> providers = new ArrayList<>();
        providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
        return  WebClient.create(uri + "/worker/search", providers);
    }

    /**
     * 创建任务信息
     * @param total 总任务量
     * @param index 当前worker的位置
     * @return
     */
    private TaskInfo createTask(int total, int index){
        int workerCount = workerAddress.size();
        int countPerWorker = total/ workerCount;

        TaskInfo task = new TaskInfo();
        task.setEndpoint(endpoint);

        //生成测试任务
        task.setStatus(TaskInfo.TaskStatus.WORKING);
        HashMap<String, String> jobs = new HashMap<>();

        for (int i = 0; i < countPerWorker; i++) {
            String fileName = fileNameList.get(index * countPerWorker + i);
            jobs.put(fileName,"prepare");
            logger.info(fileName + " was assign to worker " + index );
        }

        task.setJobs(jobs);

        return task;
    }

    class Worker implements Runnable{
        WebClient webClient;
        String endpointUri;
        TaskInfo task;
        WebTarget target;

        @Override
        public void run() {

            Client client = ClientBuilder.newClient().register(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class);
            target = client.target(endpointUri + "/worker/search");

            responseAsync(task);
            while (task.getStatus() == TaskInfo.TaskStatus.WORKING){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(task.getStatus() == TaskInfo.TaskStatus.WORKING){
                    logger.info(endpointUri + " is working");
                }
            }

        }

        public Future<TaskInfo> responseAsync(TaskInfo taskInfo) {
            return target.request(MediaType.APPLICATION_JSON_TYPE).async().post(Entity.entity(taskInfo, MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<TaskInfo>() {
                @Override
                public void completed(TaskInfo param) {
                    task = param;
                    logger.info(endpointUri + " work complete");

                    param.getJobs().entrySet().stream().filter(entry -> "matched".equals(entry.getValue())).forEach(entry -> {
                        matchedUris.add(entry.getKey());
                    });
                }

                @Override
                public void failed(Throwable throwable) {
                    task.setStatus(TaskInfo.TaskStatus.ERROR);
                    logger.error(endpointUri + " work failed");
                }
            });
        }
    }

}
