package xyz.micrqwe.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaowenxing on 2017/11/20.
 * 初始化运行数据，生成html数据
 */
@Component
public class ApplicationSwagger implements CommandLineRunner {
    @Value("${swagger-filePath:DEFAULT}")
    private String filePath;
    private FileInputStream fis;
    private FileOutputStream fos;
    @Autowired
    private DocumentationCache documentationCache;
    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    private final SwaggerResourcesProvider swaggerResources;

    @Autowired
    public ApplicationSwagger(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }

    @Override
    public void run(String... args) throws Exception {
        if (filePath == null || filePath.equals("DEFAULT")) {
            filePath = "E:" + File.separator + "home";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // 获取所有的节点
        List<SwaggerResource> list = swaggerResources.get();
//        String in = this.getClass().getClassLoader().getResource("static").getPath();
        try {
            String s = this.getClass().getClassLoader().getResource("static/swaggerTemplate").getPath();
            this.copyFolder(new File(s), new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> fileName = new ArrayList<Map<String, String>>();
        try {
            Map map = null;
            int i = 1;
            for (SwaggerResource tmp : list) {
                // 获取文档分类模板
                Documentation documentation = documentationCache.documentationByGroup(tmp.getName());
                // 获取注释代码
                Swagger swagger = mapper.mapDocumentation(documentation);
                map = this.createFile(objectMapper.writeValueAsString(swagger), tmp, i);
                fileName.add(map);
                i++;
                map = null;
            }
            File file = new File(filePath + File.separator + "swaggerTemplate" + File.separator + "index.html");
            String s = this.fileToString(file);
            s = s.replace("${urlsLocation}", objectMapper.writeValueAsString(fileName));
            this.stringtoFile(s, file);
        } catch (JsonProcessingException e) {

        }
    }


    private boolean stringtoFile(String str, File file) {
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
     * @param swagger
     * @param swaggerResource
     */
    private Map<String, String> createFile(String swagger, SwaggerResource swaggerResource, int i) {
        String location = "api-docs-" + swaggerResource.getName() + ".json";
        String path = filePath + File.separator + "swaggerTemplate" + File.separator + "data";
        File file = new File(path + File.separator + location);
        Map map = new HashMap();
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.print(swagger);
            ps.close();
        } catch (Exception e) {

        }
        map.put("name", swaggerResource.getName());
        map.put("url", "data/" + location);
        return map;
    }

    /**
     * 获取一个文件
     *
     * @param file
     * @return
     */
    private String fileToString(File file) {
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

    /**
     * 复制整个文件夹内容
     *
     * @param file   String 原文件路径 如：c:/fqf
     * @param toFile String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(File file, File toFile) throws Exception {
        byte[] b = new byte[1024];
        int a;
        if (file.isDirectory()) {
            String filepath = file.getPath();
            filepath = filepath.replaceAll("\\\\", "/");
            String toFilepath = toFile.getPath();
            toFilepath = toFilepath.replaceAll("\\\\", "/");
            int lastIndexOf = filepath.lastIndexOf("/");
            toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length());
            File copy = new File(toFilepath);
            //复制文件夹
            boolean boo = copy.mkdirs();
            //遍历文件夹
            for (File f : file.listFiles()) {
                copyFolder(f, copy);
            }
        } else {
            if (toFile.isDirectory()) {
                String filepath = file.getPath();
                filepath = filepath.replaceAll("\\\\", "/");
                String toFilepath = toFile.getPath();
                toFilepath = toFilepath.replaceAll("\\\\", "/");
                int lastIndexOf = filepath.lastIndexOf("/");
                toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length());

                //写文件
                File newFile = new File(toFilepath);
                fis = new FileInputStream(file);
                fos = new FileOutputStream(newFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            } else {
                //写文件
                fis = new FileInputStream(file);
                fos = new FileOutputStream(toFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            }
            fis.close();
            fos.flush();
            fos.close();

        }
    }
}
