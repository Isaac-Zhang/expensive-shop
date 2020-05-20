package com.liferunner.fastdfs.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.liferunner.fastdfs.config.UploadConfig;
import com.liferunner.fastdfs.service.FastdfsService;
import java.io.ByteArrayInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * FastdfsServiceImpl for TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/5/19
 **/
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FastdfsServiceImpl implements FastdfsService {

    // 注入 fastdfs 的 storary client
    private final FastFileStorageClient fastFileStorageClient;
    private final UploadConfig uploadConfig;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        val storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
            file.getSize(), fileExtName, null);
        val realPath = storePath.getFullPath();
        return realPath;
    }

    @Override
    public String uploadOSS(MultipartFile file, String fileExtName, String uid) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = uploadConfig.getEndpoint();
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = uploadConfig.getAccessKeyId();
        String accessKeySecret = uploadConfig.getAccessKeySecret();
        String bucketName = uploadConfig.getBucketName();
// <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String objectName = uploadConfig.getObjectName();

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
        String content = "Hello OSS";
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

// 关闭OSSClient。
        ossClient.shutdown();
        return uploadConfig.getImageUri() + objectName;
    }
}
