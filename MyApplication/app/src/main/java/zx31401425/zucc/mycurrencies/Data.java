package zx31401425.zucc.mycurrencies;

/**
 * Created by 赵轩 on 2017/7/3.
 */

public class Data {
    private String foregin;
    private String home;
    private double num;
    private double forenum;

    public Data (String foregin,String home,double forenum,double num){
        this.foregin = foregin;
        this.home = home;
        this.num = num;
        this.forenum = forenum;
    }

    public double getForenum() {
        return forenum;
    }

    public String getForegin() {
        return foregin;
    }

    public String getHome() {
        return home;
    }

    public double getNum() {
        return num;
    }

    public void setForegin(String foregin) {
        this.foregin = foregin;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public void setForenum(double forenum) {
        this.forenum = forenum;
    }
}
