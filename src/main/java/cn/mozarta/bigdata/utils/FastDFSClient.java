package cn.mozarta.bigdata.utils;


import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FastDFSClient {

    /*private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient1 storageClient = null;

    public FastDFSClient(String conf) throws Exception {
        if (conf.contains("classpath:")) {
            conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
        }
        ClientGlobal.init(conf);
        trackerClient = new TrackerClient();
        trackerServer = trackerClient.getConnection();
        storageServer = null;
        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    *//**
     * @param file 文件二进制
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @return
     * @throws Exception
     *//*
    public String uploadFile(byte[] file, String fileName, long fileSize) throws Exception {
        NameValuePair[] metas = new NameValuePair[3];
        metas[0] = new NameValuePair("fileName", fileName);
        metas[1] = new NameValuePair("fileSize", String.valueOf(fileSize));
        metas[2] = new NameValuePair("fileExt", FilenameUtils.getExtension(fileName));
        String result = storageClient.upload_file1(file, FilenameUtils.getExtension(fileName), metas);
        return result;
    }

    *//**
     *
     * @param storagePath  文件的全部路径 如：group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     * @throws Exception
     *//*
    public Integer delete_file(String storagePath){
        int result=-1;
        try {
            result = storageClient.delete_file1(storagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }*/

    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;


    //上传文件
    public String upload(MultipartFile myfile) throws Exception{
        //文件名
        String originalFilename = myfile.getOriginalFilename().substring(myfile.getOriginalFilename().lastIndexOf(".") + 1);
        // 文件扩展名
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());

        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(myfile.getInputStream(), myfile.getSize(),originalFilename , null);

        String path = storePath.getFullPath();

        return path;
    }

    /**
     * 删除文件
     * @Param fileUrl 文件访问地址
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            e.printStackTrace();
        }
    }


}