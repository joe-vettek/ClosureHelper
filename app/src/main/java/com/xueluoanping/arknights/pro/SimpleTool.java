package com.xueluoanping.arknights.pro;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xueluoanping.arknights.SimpleApplication;
import com.xueluoanping.arknights.api.Game;
import com.xueluoanping.arknights.global.Global;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SimpleTool {
    /**
     * 按指定大小，分隔集合，将集合按规定的个数分为n个部分
     *
     * @param <T>
     * @param list<String> list列表
     * @param len          长度
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }


    public static void saveTextFile(Context context, String text, String name) {
        try {
            // 存在cache目录
            File file = new File(context.getExternalCacheDir().getAbsolutePath() + "/" + name);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(text.getBytes());
            outStream.close();
            Log.e("save", file.getName() + ""+file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Desc: 深度拷贝
     * Author:
     *
     * @param srcList
     */
    public static <T> List<T> deepCopy(List<T> srcList) throws IOException, ClassNotFoundException {
        List<T> desList = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        //序列化
        baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(baos);
        oos.writeObject(srcList);

        //反序列化
        bais = new ByteArrayInputStream(baos.toByteArray());
        ois = new ObjectInputStream(bais);
        desList = (List<T>) ois.readObject();

        baos.close();
        oos.close();
        bais.close();
        ois.close();

        return desList;
    }

    public static String protectTelephoneNum(String num) {
        try {
            String front = num.substring(0, 3);
            String later = num.substring(9);
            return front + "******" + later;
        } catch (Exception e) {
            // e.printStackTrace();
        }
       return "***";

    }

    public static UUID getUUID(String s) {
        return UUID.nameUUIDFromBytes(s.getBytes());
    }

    public static String getFormatDate(long lastFreshTs,boolean isUnix) {
        int baseMultiplier=1;
        if(isUnix)baseMultiplier=1000;
        Date date = new Date(new BigDecimal(lastFreshTs).multiply(new BigDecimal(baseMultiplier)).longValue());
        String MM = Integer.valueOf(String.format("%tm", date)).toString();
        String dd = String.format(Locale.CHINA, "%td", date);
        String HH = String.format("%tk", date);
        String mm = String.format("%tM", date);
        return String.format("%s月%s日%s时%s分", MM, dd, HH, mm);
    }

    public static JSONObject getJson(String url) throws JSONException, IOException {
        File file = new File(url);
        int size = (int) file.length();
        if (size == 0) return HttpConnectionUtil.emptyJsonObject1;
        byte[] cbuf = new byte[size];
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);
        String text = new String(cbuf, 0, len);
        JSONTokener tokener = new JSONTokener(text);
        JSONObject object = new JSONObject(tokener);
        // Log.d(TAG, "getCharacterTable: ");
        return object;
    }

    public static String getText(String url) throws  IOException {
        File file = new File(url);
        int size = (int) file.length();
        if (size == 0) return "";
        byte[] cbuf = new byte[size];
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);
        return new String(cbuf, 0, len);
    }

    public static String getStringAssents(String name) throws  IOException {
        Context context=SimpleApplication.getContext();
        int size = (int) context.getAssets().openFd(name).getLength();
        byte[] cbuf = new byte[size];
        InputStream is = context.getAssets().open(name);
        int len = is.read(cbuf);
        return  new String(cbuf, 0, len);
    }
    // 需要是活动
    public static void toastInThread(Activity context, String s) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
