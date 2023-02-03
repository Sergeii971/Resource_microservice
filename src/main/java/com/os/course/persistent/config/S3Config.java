package com.os.course.persistent.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.os.course.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class S3Config {
    @Autowired
    private AwsProperties awsProperties;

    @Bean
    public AmazonS3 getS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsProperties.getEndpoint(), awsProperties.getRegion()))
                .enablePathStyleAccess()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey())))
                .build();

    }

//    TODO this apllication use aws emulator - localstack(docker image)
//    @Bean
//    public Bucket createBucket() {
//        return getS3().createBucket(Constant.BUCKET_NAME);
//    }
}
