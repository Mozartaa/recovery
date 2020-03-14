package cn.mozarta.bigdata.pojo;

import java.io.Serializable;

public class DataBean implements Serializable,Comparable<DataBean> {

    private String type;

    private String date;

    private String data;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DataBean(){}
    public DataBean(String type, String date, String data) {
        this.type = type;
        this.date = date;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", data='" + data + '\'' +
                '}';
    }


    @Override
    public int compareTo(DataBean o) {

       return this.getType().compareTo(o.getType());

    }
}
