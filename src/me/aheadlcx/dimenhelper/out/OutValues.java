package me.aheadlcx.dimenhelper.out;

/**
 * Created by aheadlcx
 * 2016/10/18 下午3:48.
 */
public class OutValues {
    private int width;
    private int height;
    private float dp2pxScale;

    public OutValues(int width, int height) {
        this.width = width;
        this.height = height;
        this.dp2pxScale = (float) width / 1920f;

    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getDp2pxScale() {
        return dp2pxScale;
    }
}
