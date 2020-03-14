package cn.mozarta.bigdata.service;

import cn.mozarta.bigdata.constant.Constant;
import cn.mozarta.bigdata.utils.FastDFSClient;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Override
    public void saveImage(MultipartFile file) throws Exception {
        String path = fastDFSClient.upload(file);
        String url = Constant.FILE_SERVER_URL+"/"+path;
        // 将url存入redis中 ，并删除之前的
        if (redisTemplate.boundHashOps(Constant.REDIS_KEY).hasKey(Constant.CHINA_MAP)) {
            // 删除之前的
            String urlPath = (String) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.CHINA_MAP);
            String deletePath = urlPath.substring(urlPath.indexOf(Constant.FILE_SERVER_URL) + Constant.FILE_SERVER_URL.length()+1);
            fastDFSClient.deleteFile(deletePath);
            // 删除redis中的
            redisTemplate.boundHashOps(Constant.REDIS_KEY).delete(Constant.CHINA_MAP);
        }
        // 存入新的地图
        redisTemplate.boundHashOps(Constant.REDIS_KEY).put(Constant.CHINA_MAP,url);
    }
}
