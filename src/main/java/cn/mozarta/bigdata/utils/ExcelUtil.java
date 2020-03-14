package cn.mozarta.bigdata.utils;

import cn.mozarta.bigdata.constant.Constant;
import cn.mozarta.bigdata.pojo.DataBean;
import cn.mozarta.bigdata.pojo.ProvinceBean;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ExcelUtil {

    // 使用单例模式
    private static ExcelUtil excelUtil;

    private ExcelUtil() {
    }

    ;

    public static ExcelUtil getInstance() {
        if (excelUtil == null) {
            excelUtil = new ExcelUtil();
        }
        return excelUtil;
    }

    /**
     * 日志
     */
    private Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 阿尔法的不同取值
     */
    private final double ALPHA1_2020_WEEKEND_2019_NOT = 1.074395;
    private final double ALPHA1_2020_NOT_2019_WEEKEND = 1 / 1.074395;
    private final double ALPHA_2020_2019_SAME = 1;

    private final double BETA = 0.944219;
    // 类型所在Excel的列
    private final int typeIndex = 0;
    // 日期所在的Excel列
    private final int dateIndex = 1;
    // 2019 复工率
    private final int recover2019Index = 3;
    // 2019 车辆时长
    private final int time2019Index = 4;
    // 2019 车辆里程
    private final int mileage2019Index = 5;

    private final int recover2020Index = 6;
    private final int time2020Index = 7;
    private final int mileage2020Index = 8;

    // 读取Excel数据
    public Map<String, Object> readExcelData(String excelPath) throws UnsupportedEncodingException {
        Workbook wb = null;
        wb = getWorkbook(excelPath);// Excel文件读取
        Map<String, Object> res = null;
        List<Map<String, Object>> industryList = null;
        List<Map<String, Object>> nationalList = null;
        List<Map<String, Object>> provincialList = null;
        if (wb != null) {
            res = new HashMap<>();
            industryList = new ArrayList<>();
            nationalList = new ArrayList<>();
            provincialList = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i); // 获取sheet
                if (sheet != null) {
                    Map<String, Object> sheetData = calSheetData(sheet);
                    if (sheetData != null) {
                        // 判断是行业还是全国还是省市
                        String sheetName = (String) sheetData.get("sheetName");
//                        String name = new String(sheetName.getBytes("UTF-8"), "GBK");
                        if (sheetName.contains("全网复工率")) {
                            nationalList.add(sheetData);
                        }
                        if (sheetName.contains("冷链复工率")) {
                            industryList.add(sheetData);
                        }
                        if (sheetName.contains("医药复工率")) {
                            // 医药复工率单独处理
//                            Map<String,Object> medicalData = formatMedicalData(sheetData);
                            industryList.add(sheetData);
                        }
                        if (sheetName.contains("各省复工率")) {
                            provincialList.add(sheetData);
                        }
                    }
                }
            }
            res.put(Constant.NATIONAL, nationalList);
            res.put(Constant.INDUSTRIAL, industryList);
            // 省市单独处理
            Map<String, List<DataBean>> formatProvince = formatProvince(provincialList);
            // 对数据进行排序
            List<ProvinceBean> provinceBeanList = sortProvinceData(formatProvince);
            if (provinceBeanList != null) {
                res.put(Constant.PROVINCIAL, provinceBeanList);
            }
            // 获取第一天并排序
            List<DataBean> todayData = getTodayData(formatProvince);
            res.put(Constant.TODAY, todayData);
        }
        return res;
    }

    private Map<String, Object> formatMedicalData(Map<String, Object> sheetData) {
        if(sheetData == null){
            return null;
        }

        List<DataBean> list = (List<DataBean>) sheetData.get("sheetData");
        if(list == null || list.size()<6){
            return null;
        }
        for(int i =list.size()-7;i>=0;i--){
            DataBean dataBean = list.get(i);
            int data = getAvgData(list,i);
            dataBean.setData(String.valueOf(data));
        }
        return sheetData;
    }

    private int getAvgData(List<DataBean> list, int i) {
        // 1 2 3 4 5 6 7 8
        int day1 = Integer.parseInt(list.get(i+6).getData());
        int day2 = Integer.parseInt(list.get(i+5).getData());
        int day3 = Integer.parseInt(list.get(i+4).getData());
        int day4 = Integer.parseInt(list.get(i+3).getData());
        int day5 = Integer.parseInt(list.get(i+2).getData());
        int day6 = Integer.parseInt(list.get(i+1).getData());
        int day7 = Integer.parseInt(list.get(i).getData());
        int res = (day1+day2+day3+day4+day5+day6+day7)/7;
        return res;
    }

    // 对省进行排序
    private List<ProvinceBean> sortProvinceData(Map<String, List<DataBean>> formatProvince) {
        if (formatProvince == null) {
            return null;
        }
        Set<String> keySet = formatProvince.keySet();
        List<ProvinceBean> list = new ArrayList<>();
        for (String keyName : keySet) {
            // 对重庆单独处理
            List<DataBean> dataBeanList = formatProvince.get(keyName);
            if (keyName.contains("重庆")) {
                keyName = "冲庆";
            }
            ProvinceBean provinceBean = new ProvinceBean(keyName, dataBeanList);
            list.add(provinceBean);
        }
        Collections.sort(list, new Comparator<ProvinceBean>() {
            @Override
            public int compare(ProvinceBean o1, ProvinceBean o2) {
                Comparator<Object> compare = Collator.getInstance(Locale.CHINA);
                return ((Collator) compare).compare(o1.getName(), o2.getName());
            }
        });
        // 再将重庆改回来
        for (ProvinceBean provinceBean : list) {
            String provinceBeanName = provinceBean.getName();
            if (provinceBeanName.contains("冲庆")) {
                provinceBeanName = "重庆";
                provinceBean.setName(provinceBeanName);
                break;
            }
        }
        return list;
    }


    // 计算每一页的复工率
    public Map<String, Object> calSheetData(Sheet sheet) throws UnsupportedEncodingException {
        if (sheet == null) {
            return null;
        }
        Map<String, Object> res = null;
        String sheetName = sheet.getSheetName();
        List<DataBean> beanList = null;
        boolean flag = sheetName.contains("复工率");
        if (flag) {
            int lastRowNum = sheet.getLastRowNum();
            beanList = new ArrayList<>();
            // 从第二行读取数据
            for (int i = 1; i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    DataBean dataBean = parseRowToBean(row);
                    if (dataBean != null) {
                        beanList.add(dataBean);
                    }
                }
            }
        }
        if (beanList != null) {
            res = new HashMap<>();
            res.put("sheetName", sheetName);
            res.put("sheetData", beanList);
        }
        return res;
    }


    // 计算每一行的复工率 并且转成DataBean
    private DataBean parseRowToBean(Row row) {
        if (row == null || isRowEmpty(row)) {
            return null;
        }
        // 计算复工率
//        Double recover = calRecover(row);
        Double recover = Double.valueOf(getCellFormatValue(row.getCell(recover2020Index)));
        int recoverRes = (int)(recover+0.5);
        // 获取类型
        String typeValue = getCellFormatValue(row.getCell(typeIndex));

        String dateValue = getCellFormatValue(row.getCell(dateIndex));
        // 处理一下日期
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = HSSFDateUtil.getJavaDate(Double.valueOf(dateValue));//获取成DATE类型
        dateValue = format.format(dt);
        DataBean dataBean = new DataBean(typeValue, dateValue, String.valueOf(recoverRes));
        return dataBean;
    }

    // 每行的复工率具体计算过程
    private Double calRecover(Row row) {
        Double value = 0.0;
        try {
            // 先获取日期
            String date = getCellFormatValue(row.getCell(dateIndex));
            // 获取ALPHA
            if (StringUtils.isEmpty(date)) {
                return value;
            }
            double alpha = getALPHA(date);
            if (date.contains("2/15")) {
                System.out.println(alpha);
            }
            Double recover2020 = Double.valueOf(getCellFormatValue(row.getCell(recover2020Index)));
            Double recover2019 = Double.valueOf(getCellFormatValue(row.getCell(recover2019Index)));

            Double time2019 = Double.valueOf(getCellFormatValue(row.getCell(time2019Index)));
            Double time2020 = Double.valueOf(getCellFormatValue(row.getCell(time2020Index)));

            Double mile2019 = Double.valueOf(getCellFormatValue(row.getCell(mileage2019Index)));
            Double mile2020 = Double.valueOf(getCellFormatValue(row.getCell(mileage2020Index)));
            // 计算
            value = ((time2020 + mile2020) * recover2020) / ((time2019 + mile2019) * recover2019) * alpha * BETA * 100;
        } catch (Exception e) {
            log.error("error message", e);
            return value;
        }
        return value;
    }

    // 根据日期判断阿尔法的值
    public double getALPHA(String dateTime) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date dt = HSSFDateUtil.getJavaDate(Double.valueOf(dateTime));//获取成DATE类型
        dateTime = format.format(dt);
        try {
            Date date = format.parse(dateTime);
            cal.setTime(date);
            int nowWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            cal.add(Calendar.YEAR, -1);
            int lastYearWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            // 判断是不是周末
            if (isWeekend(nowWeek) && isWeekend(lastYearWeek) || ((!isWeekend(nowWeek) && !isWeekend(lastYearWeek)))) {
                return ALPHA_2020_2019_SAME;
            }
            // 2020 是周末 2019 不是周末
            if (isWeekend(nowWeek)) {
                return ALPHA1_2020_WEEKEND_2019_NOT;
            }
            return ALPHA1_2020_NOT_2019_WEEKEND;
        } catch (ParseException e) {
            log.error("日期计算失败，无法获取阿尔法值", e);
            return 0.0;
        }
    }

    // 判断是不是周末
    public boolean isWeekend(int week) {
        if (week == 6 || week == 0) {
            return true;
        } else {
            return false;
        }
    }

    // 处理省市数据
    public Map<String, List<DataBean>> formatProvince(List<Map<String, Object>> provincialList) {
        if (provincialList == null || provincialList.size() == 0) {
            return null;
        }
        // 结果集
        Map<String, List<DataBean>> res = new HashMap<>();
        // map 只有一个
        Map<String, Object> map = provincialList.get(0);
        // 获取所有省市自治区的名字
        Set<String> typeSet = new HashSet<>();
        List<DataBean> dataBeanList = (List<DataBean>) map.get("sheetData");
        // 获取到所有省市
        for (DataBean dataBean : dataBeanList) {
            if (dataBean != null) {
                String type = dataBean.getType();
                typeSet.add(type);
            }
        }
        for (String name : typeSet) {
            res.put(name, new ArrayList<DataBean>());
        }
        // 将数据添加到对应的list中
        for (DataBean dataBean : dataBeanList) {
            String provinceName = dataBean.getType();
            List<DataBean> list = res.get(provinceName);
            list.add(dataBean);
            res.put(provinceName, list);
        }
        return res;
    }

    // 获取每个省当天数据
    public List<DataBean> getTodayData(Map<String, List<DataBean>> formatProvince) {
        if (formatProvince == null) {
            return null;
        }
        Set<String> keySet = formatProvince.keySet();
        List<DataBean> res = new ArrayList<>();
        for (String key : keySet) {
            List<DataBean> dataBeanList = formatProvince.get(key);
            if (dataBeanList != null && dataBeanList.size() > 0) {
                DataBean dataBean = dataBeanList.get(0);
                if (dataBean.getType().contains("重庆")) {
                    String newName = new String("冲庆");
                    dataBean.setType(newName);
                }
                res.add(dataBean);
            }
        }
        Collections.sort(res, new Comparator<DataBean>() {
            @Override
            public int compare(DataBean o1, DataBean o2) {
                Comparator<Object> compare = Collator.getInstance(Locale.CHINA);
                return ((Collator) compare).compare(o1.getType(), o2.getType());
            }
        });
        for (DataBean bean : res) {
            if (bean.getType().contains("冲庆")) {
                bean.setType("重庆");
                break;
            }
        }
        return res;
    }

    // 判断是否是空行
    public boolean isRowEmpty(Row row) {
        int count = row.getLastCellNum() > 2 ? 2 : row.getLastCellNum();
        for (int i = 0; i < count; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && (!cell.getCellTypeEnum().equals(CellType.BLANK))) {
                return false;
            }
        }
        return true;
    }

    // 读取单元格数据  全部返回String  注 ： 对于日期需要单独处理  读出来的数据是时间戳
    public String getCellFormatValue(Cell cell) {
        String cellValue = null;
        if (cell != null) {

            // 判断cell类型
//            cell.setCellType(CellType.STRING);
            CellType cellTypeEnum = cell.getCellTypeEnum();
//            cellValue = cell.getStringCellValue();
            switch (cellTypeEnum) {
                case NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case FORMULA: {
                    // 判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 转换为日期格式YYYY-mm-dd
                        cellValue = String.valueOf(cell.getDateCellValue());
                    } else {
                        // 数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
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



}