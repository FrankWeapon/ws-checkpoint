package edu.imu.cloud.controller.worker;

import edu.imu.cloud.service.ImageComparator;
import edu.imu.cloud.vo.TaskInfo;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by FrankWeapon on 4/8/16.
 */
@Path("worker")
public class WorkHandler {

    private static final Logger logger = LogManager.getLogger(WorkHandler.class);

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/search")
    public Response search(TaskInfo taskInfo) {

        OutputStream outputStream;
        FTPClient ftp = new FTPClient();
        File source = new File("source.jpg");
        try {
            if (!source.exists()) {
                source.createNewFile();
            }
            ftp.connect(taskInfo.getEndpoint());
            ftp.enterLocalPassiveMode();
            ftp.login("anonymous", "");

            outputStream = new FileOutputStream(source);
            ftp.retrieveFile("source.jpg" , outputStream);
            outputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

//        FileUtil fileUtil = new FileUtil(taskInfo.getEndpoint());
//        TODO: 根据Endpoint和key 下载文件、实施计算, 修改value值
//        taskInfo.getJobs().forEach((k, v) -> {
//            fileUtil.retrieve(k);
//            System.out.println(k);
//            v = "downloaded";
//            logger.trace("file " + k + "retrieved");
//        });

        taskInfo.getJobs().entrySet().parallelStream().forEach(e -> {

            OutputStream output;
            FTPClient ftpClient = new FTPClient();
            File outputFile = new File("/Users/frankweapon/Desktop/" + e.getKey());
            try {
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                ftpClient.connect(taskInfo.getEndpoint());
                ftpClient.enterLocalPassiveMode();
                ftpClient.login("anonymous", "");

                output = new FileOutputStream(outputFile);
                ftpClient.retrieveFile(e.getKey(), output);
                output.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.setValue("downloaded");
            logger.trace("file " + e.getKey() + "retrieved");

            ImageComparator imageCompare = null;
            try {
                imageCompare = new edu.imu.cloud.serviceImp.ImageComparator(ImageIO.read(outputFile),ImageIO.read(source));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            assert imageCompare != null;
            double rate = imageCompare.match();

            if (rate > 0.95) {
                e.setValue("matched");
            } else {
                e.setValue("unmatched");
            }

        });

        taskInfo.setStatus(TaskInfo.TaskStatus.SUCCESS);
        return Response.ok().entity(taskInfo).build();
    }


}
