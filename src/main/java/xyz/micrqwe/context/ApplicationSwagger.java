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
import xyz.micrqwe.service.SwaggerService;
import xyz.micrqwe.util.FileUtil;

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
    @Autowired
    private SwaggerService swaggerService;

    @Override
    public void run(String... args) throws Exception {
        swaggerService.run();
    }
}
