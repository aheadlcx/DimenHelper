package me.aheadlcx.dimenhelper.out;

import me.aheadlcx.dimenhelper.OriginDimen;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aheadlcx
 * 2016/10/18 下午3:58.
 */
public class OutUtils {

    public static List<OutValues> getOutValues() {
        List<OutValues> outValuesList = new ArrayList<>();

        OutValues outValues = getOutValues1280720();
        outValuesList.add(outValues);
        outValuesList.add(getOutValues1280672());
        outValuesList.add(getOutValues19201008());
        outValuesList.add(getOutValues19201080());
        outValuesList.add(getOutValues25601440());
        outValuesList.add(getOutValues38402160());
        return outValuesList;
    }

    @NotNull
    private static OutValues getOutValues1280720() {
        return new OutValues(1280, 720);
    }

    @NotNull
    private static OutValues getOutValues1280672() {
        return new OutValues(1280, 672);
    }
    @NotNull
    private static OutValues getOutValues19201008() {
        return new OutValues(1920, 1008);
    }
    @NotNull
    private static OutValues getOutValues19201080() {
        return new OutValues(1920, 1080);
    }

    @NotNull
    private static OutValues getOutValues25601440() {
        return new OutValues(2560, 1440);
    }
    @NotNull
    private static OutValues getOutValues38402160() {
        return new OutValues(3840, 2160);
    }
    private static String genFilesName(OutValues values) {
        return "values-nodpi-" + values.getWidth() + "x" + values.getHeight();
    }

    public static File createDimenFile(String ouput, OutValues outValues) {
        String dicPath = "";
        String fileName = genFilesName(outValues);
        if (ouput.endsWith(File.separator)) {
            dicPath = ouput + fileName + File.separator;
        } else {
            dicPath = ouput + File.separator + fileName + File.separator;
        }

        File dic = new File(dicPath);
        if (dic.exists()) {
            dic.delete();
        }
        dic.mkdirs();
        String dimenFile = dicPath + "dimen_px.xml";
        File dimen = new File(dimenFile);
        if (dimen.exists()) {
            dimen.delete();
        }

        try {
            dimen.createNewFile();
            return dimen;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeDimen(File outFile, List<OriginDimen> originDimenList, OutValues outValue) throws IOException {
        File dimenFile = outFile;
        FileOutputStream fos = new FileOutputStream(dimenFile);
        StreamResult result = new StreamResult(fos);
        SAXTransformerFactory sff = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler th = null;

        try {
            th = sff.newTransformerHandler();
            Transformer transformer = th.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            th.setResult(result);

            th.startDocument();


            AttributesImpl attr = new AttributesImpl();
            char[] space = "\n".toCharArray();
            th.characters(space, 0, space.length);
            th.startElement("", "", "resources", attr);

            //
            char[] spaceChar = "\n    ".toCharArray();
            for (OriginDimen dimen : originDimenList) {
                //white space
                th.characters(spaceChar, 0, spaceChar.length);
                //name attr
                attr.addAttribute("", "", "name", String.class.getName(), dimen.getName());
                th.startElement("", "", "dimen", attr);
                float value = dimen.getValue() * outValue.getDp2pxScale();
                if (!dimen.isDp()) {
                    value = dimen.getValue();
                } else {
                    value = Math.round(value) * 2;
                }
                char[] valueChars = String.format("%spx", value).toCharArray();
                th.characters(valueChars, 0, valueChars.length);
                th.endElement("", "", "dimen");
            }

            //

            th.endElement("", "", "resources");
            th.endDocument();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }
}
