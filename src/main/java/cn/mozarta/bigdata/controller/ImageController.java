package cn.mozarta.bigdata.controller;

import cn.mozarta.bigdata.constant.Constant;
import cn.mozarta.bigdata.service.DataService;
import cn.mozarta.bigdata.service.ImageService;
import cn.mozarta.bigdata.utils.FastDFSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ImageController {


    Logger log = LoggerFactory.getLogger(ImageController.class);



    @Autowired
    private ImageService imageService;


    @Autowired
    private DataService dataService;

    @RequestMapping("/image")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file){
        try {
            imageService.saveImage(file);
            // 将地址存入redis中
            return "success";
        } catch (Exception e) {
            log.error("error message:",e);
            return "failed";
        }

    }


    @RequestMapping("/upload")
    public String upload(){
        return "upload";
    }


    @RequestMapping("/fileupload")
    public String fileupload(){
        return "file";
    }


    @RequestMapping("/file")
    @ResponseBody
    public String file(@RequestParam("file") MultipartFile file){
        if(file == null){
            return "please select file";
        }
        try{
            String rootPath = System.getProperty("user.dir");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
            String date = sdf.format(new Date());
            String path = rootPath + "/files/"+date+"/"+file.getOriginalFilename();
            System.out.println(path);
            File fileSave = new File(path);
            if (!fileSave.getParentFile().exists()) {
                fileSave.getParentFile().mkdirs();
            }
            file.transferTo(fileSave);
            dataService.updateFile(path);
            return "upload success and data update success";
        }catch (Exception e){
            log.error("error message",e);
            return "failed";
        }
    }

}
