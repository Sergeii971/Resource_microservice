package com.os.course.persistent.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.setup")
@Getter
@Setter
public class AwsProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;

}
