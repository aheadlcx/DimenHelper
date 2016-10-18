package me.aheadlcx.dimenhelper;

/**
 * Created by aheadlcx
 * 2016/10/17 下午6:36.
 */
public class OriginDimen {
    private String name;
    private float value;
    private String suffix;//dp dip px

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isDp(){
        if (suffix != null && suffix.equals("px")){
            return false;
        }
        return true;
    }

    public String getSuffix() {

        return suffix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getName() {

        return name;
    }

    public float getValue() {
        return value;
    }
}
