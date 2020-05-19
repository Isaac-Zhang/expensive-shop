package com.liferunner.fastdfs.service.impl;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.liferunner.fastdfs.service.FastdfsService;
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

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        val storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
            file.getSize(), fileExtName, null);
        val realPath = storePath.getFullPath();
        return realPath;
    }
}
