package com.os.course.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ConfigurationProperties(prefix = "aws.setup.test")
@ActiveProfiles("dev")
@Getter
@Setter
public class AwsTestProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;

}
