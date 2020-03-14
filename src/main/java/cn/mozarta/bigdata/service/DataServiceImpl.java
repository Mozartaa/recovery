package cn.mozarta.bigdata.service;

import cn.mozarta.bigdata.constant.Constant;
import cn.mozarta.bigdata.pojo.DataBean;
import cn.mozarta.bigdata.pojo.ProvinceBean;
import cn.mozarta.bigdata.utils.ExcelUtil;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class DataServiceImpl implements DataService{

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Map<String, Object>> getNationalData() {
        List<Map<String,Object>> res = (List<Map<String, Object>>) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.NATIONAL);
        return res;
    }

    @Override
    public List<DataBean> getTodayData() {
        List<DataBean> res = (List<DataBean>) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.TODAY);
        return res;
    }

    @Override
    public List<Map<String, Object>> getIndustryData() {
        List<Map<String,Object>> res = (List<Map<String, Object>>) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.INDUSTRIAL);
        return res;
    }

    @Override
    public List<ProvinceBean> getProvincialData() {
        List<ProvinceBean> res = (List<ProvinceBean>) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.PROVINCIAL);
        return res;
    }

    @Override
    public void updateData()  {
        try {
            getDataFromExcel();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMap() {
        String url = (String) redisTemplate.boundHashOps(Constant.REDIS_KEY).get(Constant.CHINA_MAP);
        return url;
    }

    @Override
    public void updateFile(String path) throws IOException {
        Map<String, Object> data = ExcelUtil.getInstance().readExcelData(path);
        if(data != null){
            importDataToRedis(data);
        }else{
            throw new RuntimeException("analyze file error");
        }
    }


    public void getDataFromExcel() throws UnsupportedEncodingException {
        String rootPath = System.getProperty("user.dir");
        File dir = new File(rootPath);
        String filePath = null;
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            for (File file : files) {
                if(file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")){
                    // 找到了目标文件
                    filePath = file.getPath();
                }
            }
        }
        if(filePath == null){
            throw new RuntimeException("Excel file does not exist");
        }
        Map<String, Object> data = ExcelUtil.getInstance().readExcelData(filePath);
        if(data != null){
            System.out.println(data);
            importDataToRedis(data);
        }else{
            throw new RuntimeException("analyze file error");
        }

    }


    public  void importDataToRedis(Map<String,Object> data){
        Object todayData = data.get(Constant.TODAY);
        Object nationalData = data.get(Constant.NATIONAL);
        Object provinceData = data.get(Constant.PROVINCIAL);
        Object industrialData = data.get(Constant.INDUSTRIAL);
        // 将数据保存在redis中
        if(redisTemplate.boundHashOps(Constant.REDIS_KEY).hasKey(Constant.TODAY)){
            // 删除原来的数据
            redisTemplate.boundHashOps(Constant.REDIS_KEY).delete(Constant.TODAY);
        }
        redisTemplate.boundHashOps(Constant.REDIS_KEY).put(Constant.TODAY,todayData);

        if(redisTemplate.boundHashOps(Constant.REDIS_KEY).hasKey(Constant.NATIONAL)){
            // 删除原来的数据
            redisTemplate.boundHashOps(Constant.REDIS_KEY).delete(Constant.NATIONAL);
        }
        redisTemplate.boundHashOps(Constant.REDIS_KEY).put(Constant.NATIONAL,nationalData);

        if(redisTemplate.boundHashOps(Constant.REDIS_KEY).hasKey(Constant.PROVINCIAL)){
            // 删除原来的数据
            redisTemplate.boundHashOps(Constant.REDIS_KEY).delete(Constant.PROVINCIAL);
        }
        redisTemplate.boundHashOps(Constant.REDIS_KEY).put(Constant.PROVINCIAL,provinceData);

        if(redisTemplate.boundHashOps(Constant.REDIS_KEY).hasKey(Constant.INDUSTRIAL)){
            // 删除原来的数据
            redisTemplate.boundHashOps(Constant.REDIS_KEY).delete(Constant.INDUSTRIAL);
        }
        redisTemplate.boundHashOps(Constant.REDIS_KEY).put(Constant.INDUSTRIAL,industrialData);
    }



}
