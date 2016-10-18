package me.aheadlcx.dimenhelper;

import org.w3c.dom.Element;

/**
 * Created by aheadlcx
 * 2016/10/18 上午11:42.
 */
public class Utils {

    public static OriginDimen translate(Element item) {
        String name = item.getAttribute("name");
        String value = item.getFirstChild().getNodeValue();
        OriginDimen originDimen = new OriginDimen();
        originDimen.setName(name);
        translateValue(value, originDimen);
        return originDimen;
    }

    private static void translateValue(String value, OriginDimen originDimen) {
        float floatValue;
        int indexOf;
        if (isDp(value)) {
            if (value.contains("d")) {
                indexOf = value.indexOf("d");
                originDimen.setSuffix("dp");
            } else {
                originDimen.setSuffix("sp");
                indexOf = value.indexOf("s");
            }
            floatValue = Float.valueOf(value.substring(0, indexOf + 1));

        }else {
            indexOf = value.indexOf("p");
            floatValue = Float.valueOf(value.substring(0, indexOf + 1));
            originDimen.setSuffix("px");
        }

        originDimen.setValue(floatValue);

    }

    private static boolean isDp(String value) {
        if (value == null) {
            return false;
        }
        if (value.contains("dp") || value.contains("dip") || value.contains("sp")) {
            return true;
        }
        return false;
    }
}
