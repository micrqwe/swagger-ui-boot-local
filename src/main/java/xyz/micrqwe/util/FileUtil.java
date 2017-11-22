package xyz.micrqwe.util;

import java.io.*;

/**
 * Created by shaowenxing on 2017/11/22.
 */
public class FileUtil {

    public static boolean stringtoFile(String str, File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取一个文件
     *
     * @param file
     * @return
     */
    public static String fileToString(File file) {
        String s = "";
        try {
            InputStream inputStream = new FileInputStream(file);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int count = -1;
            while ((count = inputStream.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
            data = null;
            s = new String(outStream.toByteArray(), "UTF-8");
            inputStream.close();
            outStream.flush();
            outStream.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return s;
    }

}
