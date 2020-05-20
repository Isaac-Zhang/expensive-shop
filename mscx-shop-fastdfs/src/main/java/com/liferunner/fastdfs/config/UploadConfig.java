package com.liferunner.fastdfs.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * UploadConfig
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/5/20
 **/
@Component
@PropertySource("classpath:upload_file.yml")
@ConfigurationProperties(prefix = "file.upload.config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;
    private String imageUri;
}
