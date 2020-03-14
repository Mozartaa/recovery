package cn.mozarta.bigdata;


import cn.mozarta.bigdata.pojo.DataBean;
import cn.mozarta.bigdata.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
class BigdataApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    public void testExcel(){
        String path = System.getProperty("user.dir");
        String filePath = path+"/test217.xlsx";
        Map<String, Object> data = null;
        try {
            data = ExcelUtil.getInstance().readExcelData(filePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(data);
    }

    @Test
    public void test(){
//        getDataFromExcel();
        String path = System.getProperty("user.dir");
        String filePath = path+"/副本疫情物流大数据分析-新基准-20200313.xlsx";
        Map<String, Object> data = null;
        try {
            data = ExcelUtil.getInstance().readExcelData(filePath);
            System.out.println(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(data);
    }



    @Test
    public void testCode() {
        String path = System.getProperty("user.dir");
        String filePath = path + "/test217.xlsx";
        Workbook workbook = getWorkbook(filePath);
        if(workbook != null){
            Sheet sheet = workbook.getSheetAt(1);
            try {
                String sheetName = sheet.getSheetName();
                /*String name = new String(sheet.getSheetName().getBytes("GBK"),"GBK");
                System.out.println(name);
                String key = new String("复工率".getBytes(),"utf-8");
                System.out.println(name.contains(key));*/
                String name = "全网复工率";
                System.out.println(sheetName.contains(name));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

        // 根据路径得到workbook对象（需要判断是2003版本还是2007)
        public Workbook getWorkbook(String filePath) {
            Workbook wb = null;
            if (filePath == null) {
                return null;
            }
            String extString = filePath.substring(filePath.lastIndexOf("."));// 判断Excel是什么版本的
            InputStream is = null;
            try {
                is = new FileInputStream(filePath);// 文件流对象
                if (".xls".equals(extString)) {
                    return wb = new HSSFWorkbook(is);// Excel版本2003
                } else if (".xlsx".equals(extString)) {
                    return wb = new XSSFWorkbook(is);// Excel版本2007/2010
                } else {
                    wb = null;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wb;
        }

    @Test
    public void testList(){
        List<DataBean> datas = new ArrayList<>();
        DataBean dataBean = new DataBean("23", "3423", "123");
        datas.add(dataBean);
        DataBean dataBean1 = datas.get(0);
        dataBean1.setData("345");
        System.out.println(datas);
    }

}
