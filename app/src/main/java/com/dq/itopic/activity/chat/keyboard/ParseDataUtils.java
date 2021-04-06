package com.dq.itopic.activity.chat.keyboard;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.dq.itopic.views.keyboard.data.EmoticonEntity;
import com.dq.itopic.views.keyboard.utils.imageloader.ImageBase;

public class ParseDataUtils {

    public static ArrayList<EmoticonEntity> ParseQqData(Map<String, Integer> data) {

        Iterator iter = data.entrySet().iterator();
        if(!iter.hasNext()){
            return null;
        }
        ArrayList<EmoticonEntity> emojis = new ArrayList<>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            EmoticonEntity entity = new EmoticonEntity();
            entity.setContent((String) key);
            entity.setIconUri("" + val);
            emojis.add(entity);
        }
        return emojis;
    }

    public static ArrayList<EmoticonEntity> ParseXhsData(String[] arry, ImageBase.Scheme scheme) {
        try {
            ArrayList<EmoticonEntity> emojis = new ArrayList<>();
            for (int i = 0; i < arry.length; i++) {
                if (!TextUtils.isEmpty(arry[i])) {
                    String temp = arry[i].trim().toString();
                    String[] text = temp.split(",");
                    if (text != null && text.length == 2) {
                        String fileName;
                        if (scheme == ImageBase.Scheme.DRAWABLE) {
                            if (text[0].contains(".")) {
                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
                            } else {
                                fileName = scheme.toUri(text[0]);
                            }
                        } else {
                            fileName = scheme.toUri(text[0]);
                        }
                        String content = text[1];
                        EmoticonEntity bean = new EmoticonEntity(fileName, content);
                        emojis.add(bean);
                    }
                }
            }
            return emojis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static EmoticonPageSetEntity<EmoticonEntity> parseDataFromFile(Context context, String filePath, String assetsFileName, String xmlName) {
//        String xmlFilePath = filePath + "/" + xmlName;
//        File file = new File(xmlFilePath);
//        if (!file.exists()) {
//            try {
//                FileUtils.unzip(context.getAssets().open(assetsFileName), filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        XmlUtil xmlUtil = new XmlUtil(context);
//        return xmlUtil.ParserXml(filePath, xmlUtil.getXmlFromSD(xmlFilePath));
//    }
}
