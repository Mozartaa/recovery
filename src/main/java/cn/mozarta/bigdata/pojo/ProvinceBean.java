package cn.mozarta.bigdata.pojo;

import java.io.Serializable;
import java.util.List;

public class ProvinceBean implements Serializable {

    private String name;

    private List<DataBean> dataBeans;

    public ProvinceBean(String keyName, List<DataBean> dataBeanList) {
        this.name = keyName;
        this.dataBeans = dataBeanList;
    }

    public ProvinceBean(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataBean> getDataBeans() {
        return dataBeans;
    }

    public void setDataBeans(List<DataBean> dataBeans) {
        this.dataBeans = dataBeans;
    }

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "name='" + name + '\'' +
                ", dataBeans=" + dataBeans +
                '}';
    }
}
