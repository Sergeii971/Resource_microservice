package com.os.course.util;

import com.os.course.model.dto.StorageDto;
import com.os.course.model.dto.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MicroserviceUtil {
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${gateway.server.url}")
    public String gatewayUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public MicroserviceUtil(CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public <T> T postObject(String url, T object, Class<T> responseType) {
        return restTemplate.postForObject(url, object, responseType);
    }

    public <T> T getObject(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    public StorageDto getStorageData(StorageType storageType) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("storageBreaker");
        List<StorageDto> storageDtoList = circuitBreaker.run(() -> Arrays.stream(
                        getObject(gatewayUrl + Constant.STORAGE_REQUEST_MAPPING, StorageDto[].class))
                .collect(Collectors.toList()), throwable -> {
            log.error(throwable.getMessage() + ", will use storage data that got in last calling");
            return Constant.storageDtoCache;
        });
        Constant.storageDtoCache = storageDtoList;
        return parseStorageTypeObject(storageDtoList, storageType);
    }

    public List<StorageDto> getStorageData() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("storageBreaker");
        List<StorageDto> storageDtoList = circuitBreaker.run(() -> Arrays.stream(
                        getObject(gatewayUrl + Constant.STORAGE_REQUEST_MAPPING, StorageDto[].class))
                .collect(Collectors.toList()), throwable -> {
            log.error(throwable.getMessage() + ", will use storage data that got in last calling");
            return Constant.storageDtoCache;
        });
        Constant.storageDtoCache = storageDtoList;
        return storageDtoList;
    }

    public StorageDto parseStorageTypeObject(List<StorageDto> storageDtoList, StorageType storageType) {
        return storageDtoList.stream()
                .filter(storageDto -> storageDto.getStorageType().equals(storageType))
                .findAny()
                .orElse(new StorageDto());
    }
}