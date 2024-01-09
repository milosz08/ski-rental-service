/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import pl.polsl.skirentalservice.core.XMLConfigLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Slf4j
@Singleton
@Startup
public class S3ClientBean {
    private static final String S3_CFG = "/s3/s3.cfg.xml";

    private final Properties s3Properties;
    private final AmazonS3 client;

    public S3ClientBean() {
        final XMLConfigLoader<XMLS3Config> xmlConfigLoader = new XMLConfigLoader<>(S3_CFG, XMLS3Config.class);
        s3Properties = xmlConfigLoader.loadConfig();
        client = createClientInstance();
    }

    public FetchedObjectData getObject(S3Bucket bucket, String key) {
        FetchedObjectData fetchedObjectData;
        try (
            final S3Object object = client.getObject(bucket.getBucketName(), key);
            final InputStream inputStream = object.getObjectContent()
        ) {
            final ObjectMetadata metadata = object.getObjectMetadata();
            fetchedObjectData = FetchedObjectData.builder()
                .data(IOUtils.toByteArray(inputStream))
                .contentType(metadata.getContentType())
                .contentLength(metadata.getContentLength())
                .build();
        } catch (IOException ex) {
            log.info("Unable to fetch object from bucket: {} with key: {}", bucket, key);
            throw new RuntimeException(ex);
        }
        return fetchedObjectData;
    }

    public void putObject(S3Bucket bucket, String key, InputStream in, ContentType type, long length) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(type.getMimeType());
        objectMetadata.setContentLength(length);
        client.putObject(bucket.getBucketName(), key, in, objectMetadata);
    }

    public void putObject(S3Bucket bucket, String key, byte[] in, ContentType type) {
        try (final InputStream inputStream = new ByteArrayInputStream(in)) {
            final ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(type.getMimeType());
            objectMetadata.setContentLength(in.length);
            client.putObject(bucket.getBucketName(), key, inputStream, objectMetadata);
        } catch (IOException ex) {
            log.info("Unable to persist object in bucket: {} with key: {}", bucket, key);
            throw new RuntimeException(ex);
        }
    }

    public void deleteObject(S3Bucket bucket, String key) {
        client.deleteObject(bucket.getBucketName(), key);
    }

    private AmazonS3 createClientInstance() {
        AmazonS3 amazonS3 = null;
        try {
            final AWSCredentials credentials = new BasicAWSCredentials(
                s3Properties.getProperty("s3.access-key"),
                s3Properties.getProperty("s3.secret-key")
            );
            final var endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                s3Properties.getProperty("s3.host"),
                s3Properties.getProperty("s3.region")
            );
            amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(Boolean.valueOf(s3Properties.getProperty("s3.path-style-access-enabled")))
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

            final List<String> buckets = amazonS3.listBuckets().stream().map(Bucket::getName).toList();
            log.info("Successfully connected with S3 service. Found buckets: {}", buckets);
        } catch (AmazonServiceException ex) {
            log.error("Unable to connect with S3 service. Cause: {}", ex.getMessage());
        }
        return amazonS3;
    }
}
