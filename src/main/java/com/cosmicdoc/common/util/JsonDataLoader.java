package com.cosmicdoc.common.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JsonDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(JsonDataLoader.class);
    private final ObjectMapper objectMapper;

    public JsonDataLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public <T> List<T> loadData(String fileName, String collectionName, Class<T> type) {
        try {
            Resource resource;
            // Try loading from different locations
            String[] paths = {
                "mock-data/" + fileName,
                "/app/mock-data/" + fileName,
                "src/main/resources/mock-data/" + fileName
            };

            InputStream inputStream = null;
            for (String path : paths) {
                try {
                    // Try classpath first
                    resource = new ClassPathResource(path);
                    if (resource.exists()) {
                        inputStream = resource.getInputStream();
                        break;
                    }
                    // Try file system
                    resource = new FileSystemResource(path);
                    if (resource.exists()) {
                        inputStream = resource.getInputStream();
                        break;
                    }
                } catch (IOException ignored) {}
            }

            if (inputStream == null) {
                throw new IOException("Could not find file: " + fileName);
            }

            JavaType mapType = objectMapper.getTypeFactory().constructMapType(
                Map.class,
                objectMapper.getTypeFactory().constructType(String.class),
                objectMapper.getTypeFactory().constructCollectionType(List.class, type)
            );
            Map<String, List<T>> data = objectMapper.readValue(inputStream, mapType);
            return data.getOrDefault(collectionName, Collections.emptyList());
        } catch (IOException e) {
            logger.error("Error loading data from file: " + fileName, e);
            throw new RuntimeException("Failed to load data from file: " + fileName, e);
        }
    }

    public <T> Map<String, List<T>> loadDataAsMap(String fileName, Class<T> type) {
        try {
            Resource resource;
            // Try loading from different locations
            String[] paths = {
                "mock-data/" + fileName,
                "/app/mock-data/" + fileName,
                "src/main/resources/mock-data/" + fileName
            };

            InputStream inputStream = null;
            for (String path : paths) {
                try {
                    // Try classpath first
                    resource = new ClassPathResource(path);
                    if (resource.exists()) {
                        inputStream = resource.getInputStream();
                        break;
                    }
                    // Try file system
                    resource = new FileSystemResource(path);
                    if (resource.exists()) {
                        inputStream = resource.getInputStream();
                        break;
                    }
                } catch (IOException ignored) {}
            }

            if (inputStream == null) {
                throw new IOException("Could not find file: " + fileName);
            }

            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
            JavaType stringType = objectMapper.getTypeFactory().constructType(String.class);
            JavaType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, stringType, listType);
            return objectMapper.readValue(inputStream, mapType);
        } catch (IOException e) {
            logger.error("Error loading data from file: " + fileName, e);
            throw new RuntimeException("Failed to load data from file: " + fileName, e);
        }
    }
}
