package cn.mozarta.bigdata.service;

import cn.mozarta.bigdata.pojo.DataBean;
import cn.mozarta.bigdata.pojo.ProvinceBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataService {


    List<Map<String, Object>> getNationalData();

    List<DataBean> getTodayData();

    List<Map<String, Object>> getIndustryData();

    List<ProvinceBean> getProvincialData();

    void updateData();

    String getMap();

    void updateFile(String path) throws IOException;
}
