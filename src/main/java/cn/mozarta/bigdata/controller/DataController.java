package cn.mozarta.bigdata.controller;

import cn.mozarta.bigdata.pojo.DataBean;
import cn.mozarta.bigdata.pojo.ProvinceBean;
import cn.mozarta.bigdata.service.DataService;
import cn.mozarta.bigdata.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController {


    @Autowired
    private DataService dataService;

    @RequestMapping("/national")
    public List<Map<String, Object>> national(){
        List<Map<String, Object>> nationalData = dataService.getNationalData();
        return nationalData;
    }


    @RequestMapping("/today")
    public List<DataBean> today(){
        List<DataBean> todayData = dataService.getTodayData();
        return todayData;
    }

    @RequestMapping("/industrial")
    public List<Map<String, Object>> industry(){
        List<Map<String, Object>> industryData = dataService.getIndustryData();
        return industryData;
    }

    @RequestMapping("/provincial")
    public List<ProvinceBean> provincial(){
        List<ProvinceBean> provincialData = dataService.getProvincialData();
        return provincialData;
    }


    @RequestMapping("/testdata")
    public Map<String,Object> getData(){
        String path = System.getProperty("user.dir");
        String filePath = path+"/test217.xlsx";
        Map<String, Object> data = null;
        try {
            data = ExcelUtil.getInstance().readExcelData(filePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        return data;
    }

    @RequestMapping("/updateData")
    public String updateData(){
        try {
            dataService.updateData();
            return "success";
        }catch (Exception e){
            return "failed";
        }
    }


    @RequestMapping("/map")
    public String getMap(){
        String url = dataService.getMap();
        return url;
    }

}
