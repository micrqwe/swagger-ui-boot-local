package xyz.micrqwe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import xyz.micrqwe.controller.CreateController;
import xyz.micrqwe.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaowenxing on 2017/11/22.
 * 生成本地api的swagger文件
 */
@Service
public class SwaggerService {
    private static final Logger logger = LoggerFactory.getLogger(CreateController.class);

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
    public SwaggerService(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }

    public void run() {
        if (filePath == null || filePath.equals("DEFAULT")) {
            filePath = "E:" + File.separator + "home";
        }
        logger.info("开始生成api文件，文件地址:{}", filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        // 获取所有的节点
        List<SwaggerResource> list = swaggerResources.get();
        // 获取模板地址
        String s = this.getClass().getClassLoader().getResource("static/swaggerTemplate").getPath();
        this.copyFolder(new File(s), new File(filePath));
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
            // 获取文件地址
            File file = new File(filePath + File.separator + "swaggerTemplate" + File.separator + "index.html");
            // 将文件转换为string处理
            s = FileUtil.fileToString(file);
            // 替换文件
            s = s.replace("${urlsLocation}", objectMapper.writeValueAsString(fileName));
            // 重新写入新文件
            FileUtil.stringtoFile(s, file);
        } catch (JsonProcessingException e) {
            logger.error("json序列化失败");
        }
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
        } catch (IOException e) {
            logger.error("写入json失败，请检查地址:{}", path);
        }
        map.put("name", swaggerResource.getName());
        map.put("url", "data/" + location);
        return map;
    }


    /**
     * 复制整个文件夹内容
     *
     * @param file   String 原文件路径 如：c:/fqf
     * @param toFile String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(File file, File toFile) {
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
            try {
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
                fos.flush();
            } catch (IOException e) {
                logger.error("写入文件失败，请检查地址:{}", filePath);
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(fos);
            }
        }
    }
}
