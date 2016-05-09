package edu.imu.cloud.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FrankWeapon on 4/13/16.
 */
public class FileUtil {

    static FTPClient client;
    private String server;
    private String password;

    public FileUtil(String url) {
        this.server = url;
        this.client = new FTPClient();
    }

    public static void main(String[] args){
//        OutputStream output;
//        try {
//            File outputFile = new File("/Users/frankweapon/Desktop/" + 1 + ".jpg");
//
//            if (!outputFile.exists()) {
//                outputFile.createNewFile();
//            }
//            client.connect();
//            client.enterLocalPassiveMode();
//            client.login("anonymous", "");
//            output = new FileOutputStream(outputFile);
//            System.out.println(client.getReplyCode());
//            InputStream input = client.retrieveFileStream(1);
//            BufferedInputStream bif = new BufferedInputStream(input);
//            int bytesRead;
//            byte[] buffer=new byte[1024];
//            while((bytesRead=bif.read(buffer))!=-1)
//            {
//                output.write(buffer,0, bytesRead);
//            }
////            client.retrieveFile(name, output);
//            output.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return true;
    }

//    public String getFileName(int index){
//        String fileName = "";
//        try {
//            client.connect(server);
//            client.enterLocalPassiveMode();
//            client.login("anonymous", "");
//            FTPFile[] files = client.listFiles("/");
//            fileName = files[index].getName();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return fileName;
//    }

    public List<String> getFileNameList(){
        List<String> names = new ArrayList<>();
        try {
            client.connect(server);
            client.enterLocalPassiveMode();
            client.login("anonymous", "");
            FTPFile[] files = client.listFiles("/");
            for (int i=0;i<files.length;i++){
                names.add(files[i].getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }


    public boolean retrieve(String name){
        OutputStream output;
        try {
            File outputFile = new File("/Users/frankweapon/Desktop/" + name);

            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            client.connect(server);
            client.enterLocalPassiveMode();
            client.login("anonymous", "");
            output = new FileOutputStream(outputFile);
            client.retrieveFile(name, output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
