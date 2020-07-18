package com.v.im.common.service;

import com.v.im.common.utils.FdfsClient;
import org.csource.common.MyException;

import java.io.FileInputStream;
import java.io.IOException;

public class FileMangeService {

    public static String[] uploadFile(FileInputStream fis, String userId) {
        String[] fileid = null;
        try {
            fileid = FdfsClient.uploadFile(fis, userId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return fileid;
    }

    public static String[] uploadFile(byte[] fis, String userId) {
        String[] fileid = null;
        try {
            fileid = FdfsClient.uploadFile(fis, userId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return fileid;
    }

    public static byte[] downloadFile(String group_name, String remoteFilename) {
        byte[] fileid = null;
        try {
            fileid = FdfsClient.downloadFile(group_name, remoteFilename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return fileid;
    }

    public static void deleteFile(String group_name, String remoteFilename) {
        try {
            FdfsClient.deleteFile(group_name, remoteFilename);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
