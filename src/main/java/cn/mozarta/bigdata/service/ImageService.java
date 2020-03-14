package cn.mozarta.bigdata.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    void saveImage(MultipartFile file) throws Exception;
}
