package xyz.micrqwe.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.micrqwe.controller.CreateController;

import java.io.*;

/**
 * Created by shaowenxing on 2017/11/22.
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(CreateController.class);

    public static boolean stringtoFile(String str, File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            logger.error("文件被占用");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error("流关闭失败");
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
            logger.error("文件被占用");
        }
        return s;
    }

}
