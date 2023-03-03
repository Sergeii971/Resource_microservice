package com.os.course.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@EnableConfigurationProperties(AwsTestProperties.class)
@Profile("dev")
public class AwsTestConfig {
    @Autowired
    private AwsTestProperties awsTestProperties;

    @Value("${bucket.name}")
    public String BUCKET_NAME;

    @Bean
    public AmazonS3 getS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsTestProperties.getEndpoint(), awsTestProperties.getRegion()))
                .enablePathStyleAccess()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsTestProperties.getAccessKey(), awsTestProperties.getSecretKey())))
                .build();

    }

    @Bean
    public Bucket createBucket() {
        return getS3().createBucket(BUCKET_NAME);
    }
}
