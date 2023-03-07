package com.xueluoanping.arknights.pro;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xueluoanping.arknights.SimpleApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleTool {
    private static final String TAG = SimpleTool.class.getSimpleName();

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

    public static String getText(String url) throws IOException {
        File file = new File(url);
        int size = (int) file.length();
        if (size == 0) return "";
        byte[] cbuf = new byte[size];
        InputStream is = new FileInputStream(file);
        int len = is.read(cbuf);
        return new String(cbuf, 0, len);
    }

    public static String getStringAssents(String name) throws IOException {
        Context context = SimpleApplication.getContext();
        int size = (int) context.getAssets().openFd(name).getLength();
        byte[] cbuf = new byte[size];
        InputStream is = context.getAssets().open(name);
        int len = is.read(cbuf);
        return new String(cbuf, 0, len);
    }

    // 需要是活动
    public static void toastInThread(Activity context, String s) {
        if (context != null && !context.isDestroyed())
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                }
            });
        else Log.d(TAG, "toastInThread: 错误的活动指向" + context);
    }

    public static boolean isTransparent(Bitmap bitmap) {
        int color1 = bitmap.getPixel(0, 0);
        int a1 = Color.alpha(color1);
        int color2 = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        int a2 = Color.alpha(color2);
        if (a1 != 255 || a2 != 255) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean saveImageToGallery(Bitmap bmp, String name) {
        try {
            OutputStream out;
            ContentResolver contentResolver = SimpleApplication.getContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".png");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            out = contentResolver.openOutputStream(uri);
            out.write(bitmapToByte(bmp));
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat format;
        if (isTransparent(bitmap)) {
            format = Bitmap.CompressFormat.PNG;
        } else format = Bitmap.CompressFormat.JPEG;
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public static List<Integer> getSignPoss(String aa, char string) {
        List<Integer> bIntegers = new ArrayList<>();
        char[] cs = aa.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == string) {
                bIntegers.add(i);
            }
        }
        return bIntegers;
    }

    public static JsonObject getGsonObject(String gameJson) {
        Gson g = new Gson();
        return g.fromJson(gameJson, JsonObject.class);
    }

    public static Bitmap drawableToBitamp(Drawable drawable)
    {
        //声明将要创建的bitmap
        Bitmap bitmap = null;
        //获取图片宽度
        int width = drawable.getIntrinsicWidth();
        //获取图片高度
        int height = drawable.getIntrinsicHeight();
        //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //创建一个空的Bitmap
        bitmap = Bitmap.createBitmap(width,height,config);
        //在bitmap上创建一个画布
        Canvas canvas = new Canvas(bitmap);
        //设置画布的范围
        drawable.setBounds(0, 0, width, height);
        //将drawable绘制在canvas上
        drawable.draw(canvas);
        return bitmap;
    }
    public static int getComplementaryColor(int colorToInvert) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorToInvert), Color.green(colorToInvert),
                Color.blue(colorToInvert), hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        return Color.HSVToColor(hsv);
    }

    public static String getShortAmountDescriptionText(int amount) {
        if(amount<100000)return amount+"";
        else if(amount<100000000) return (amount/10000)+"万";
        else return amount/100000000+"亿";
    }

    public static String getShorterAmountDescriptionText(int amount) {
        if(amount<10000)return amount+"";
        else if(amount<100000000) return (amount/10000)+"万";
        else return amount/100000000+"亿";
    }

    public static String getShortestAmountDescriptionText(int amount) {
        if(amount<1000)return amount+"";
        else if(amount<10000) return (amount/1000)+"千";
        else if(amount<100000000) return (amount/10000)+"万";
        else return amount/100000000+"亿";
    }


}
