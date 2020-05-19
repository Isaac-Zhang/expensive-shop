package com.liferunner.fastdfs.controller;

import com.liferunner.fastdfs.service.FastdfsService;
import com.liferunner.utils.JsonResponse;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * FastdfsController
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/5/19
 **/
@RequestMapping(value = "/file/distributed")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FastdfsController {

    private final FastdfsService fastdfsService;

    @PostMapping("/upload")
    public JsonResponse upload(@RequestParam String uid,
        MultipartFile file,
        HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        if (null == file) {
            return JsonResponse.errorMsg("文件不能为空");
        }
        //获取文件名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return JsonResponse.errorMsg("文件名称不能为空");
        }
        //文件重命名
        String[] fileNameArray = filename.split("\\.");

        //获取文件后缀名
        String fileSuffix = fileNameArray[fileNameArray.length - 1];
        if (!(fileSuffix.equalsIgnoreCase("png") ||
            fileSuffix.equalsIgnoreCase("jpg") ||
            fileSuffix.equalsIgnoreCase("jpeg"))) {
            return JsonResponse.errorMsg("上传图片格式错误！");
        }

        String distributedPath = fastdfsService.upload(file,fileSuffix);
        return JsonResponse.ok(distributedPath);
    }
}
