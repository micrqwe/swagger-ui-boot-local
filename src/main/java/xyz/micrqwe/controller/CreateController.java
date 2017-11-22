package xyz.micrqwe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import xyz.micrqwe.service.SwaggerService;
import xyz.micrqwe.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaowenxing on 2017/11/13.
 */
@Controller
@RequestMapping("swagger")
public class CreateController {
    private static final Logger logger = LoggerFactory.getLogger(CreateController.class);
    @Value("${swagger-filePath:DEFAULT}")
    private String filePath;
    @Autowired
    private DocumentationCache documentationCache;
    @Autowired
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired
    private SwaggerService swaggerService;

    /**
     * 下载api
     *
     * @param groupName
     */
    @RequestMapping("api-docs")
    public ResponseEntity<byte[]> downloadApi(String groupName) {
        groupName = Optional.fromNullable(groupName).or(Docket.DEFAULT_GROUP_NAME);
        Documentation documentation = documentationCache.documentationByGroup(groupName);
        if (documentation == null) {
            HttpStatus statusCode = HttpStatus.OK;
            ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>("null".getBytes(), null, statusCode);
            return entity;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attchement;filename=api-docs-" + groupName + ".json");
        HttpStatus statusCode = HttpStatus.OK;
        // 获取注释代码
        Swagger swagger = mapper.mapDocumentation(documentation);
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(swagger);
        } catch (JsonProcessingException e) {
            logger.error("json序列化失败");
        }
        ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body.getBytes(), headers, statusCode);
        return entity;
    }

    @RequestMapping("/swagger-resources")
    @ResponseBody
    String swaggerResources() {
        swaggerService.run();
        return "YES";
    }
}

