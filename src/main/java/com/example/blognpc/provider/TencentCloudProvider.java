package com.example.blognpc.provider;

import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TencentCloudProvider {

    @Value("${cloud.tencent.cam.capi.app-id}")
    private String appId;

    @Value("${cloud.tencent.cam.capi.secret-id}")
    private String secretId;

    @Value("${cloud.tencent.cam.capi.secret-key}")
    private String secretKey;

    @Value("${cloud.tencent.cos.bucket.bucket-name}")
    private String bucketName;

    @Value("${cloud.tencent.cos.bucket.region}")
    private String region;

    @Value("${cloud.tencent.cos.bucket.expires}")
    private Long expiresSec;



    private COSClient createCOSClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        ClientConfig clientConfig = new ClientConfig();

        clientConfig.setRegion(new Region(region));

        clientConfig.setHttpProtocol(HttpProtocol.https);

        return new COSClient(cred, clientConfig);
    }

    private TransferManager createTransferManager(COSClient cosClient) {
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    private void shutdownTransferManager(TransferManager transferManager) {
        transferManager.shutdownNow(true);
    }

    public String upload(InputStream fileStream, String fileName) {
        String generatedFileName;
        String[] fileNames = fileName.split("\\.");
        if (fileNames.length > 1) {
            generatedFileName = UUID.randomUUID() + "." + fileNames[fileNames.length - 1];
        } else {
            return null;
        }

        COSClient cosClient = createCOSClient();

        TransferManager transferManager = createTransferManager(cosClient);

        String key = generatedFileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileStream, objectMetadata);

        try {
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
        } catch (CosServiceException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (CosClientException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (InterruptedException e) {
            log.error("file upload fail, {}", key);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
        shutdownTransferManager(transferManager);

        Date expirationDate = new Date(System.currentTimeMillis() + expiresSec);
        HttpMethodName method = HttpMethodName.GET;

        URL url = cosClient.generatePresignedUrl(bucketName, key, expirationDate, method);

        return url.toString();
    }
}
