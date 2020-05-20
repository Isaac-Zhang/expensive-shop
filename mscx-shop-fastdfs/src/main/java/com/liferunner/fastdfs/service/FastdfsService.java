package com.liferunner.fastdfs.service;

import com.google.common.base.Strings;
import com.sun.org.apache.xpath.internal.operations.Mult;
import io.netty.util.internal.StringUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * FastdfsService
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/5/19
 **/
public interface FastdfsService {

    /**
     * 用于上传文件到 分布式存储系统 fastdfs
     *
     * @param file 上传文件
     * @param fileExtName 文件后缀名
     * @return 返回上传后存储的全路径
     */
    default String upload(MultipartFile file, String fileExtName) throws Exception {
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 上传文件存储到 OSS
     * @param file
     * @param uid
     * @return
     */
    String uploadOSS(MultipartFile file, String fileExtName,String uid);

    ;
}
