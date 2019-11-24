package com.liferunner.api.controller.usercenter;

import com.alibaba.fastjson.JSON;
import com.liferunner.api.controller.BaseController;
import com.liferunner.dto.UserResponseDTO;
import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.DateTools;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * UserCenterController for : 用户中心controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
@RestController
@Slf4j
@RequestMapping("/usercenter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "用户中心相关API接口", value = "用户中心controller")
public class UserCenterController extends BaseController {
    private final IUserCenterLoginUserService userCenterLoginUserService;

    /**
     * @return
     */
    @PostMapping("/userinfo")
    @ApiOperation(tags = "根据用户id获取用户", value = "根据用户id获取用户")
    public JsonResponse findUserByUid(@RequestParam String uid) {
        val user = this.userCenterLoginUserService.findUserById(uid);
        if (null != user) {
            return JsonResponse.ok(user);
        }
        return JsonResponse.errorMsg("获取用户信息失败");
    }

    @PostMapping("/update")
    @ApiOperation(tags = "根据用户id更新用户", value = "根据用户id更新用户")
    public JsonResponse updateUser(
            @RequestParam String uid,
            @RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (result.hasErrors()) {
            val errorsMap = getErrorsMap(result);
            return JsonResponse.errorMap(errorsMap);
        }
        log.info("==========update user:{} begin by uid:{}",
                JSON.toJSONString(userUpdateRequestDTO),
                uid);
        val updateUser = this.userCenterLoginUserService.updateUser(uid, userUpdateRequestDTO);
        if (null != updateUser) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(updateUser, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                        JSON.toJSONString(userResponseDTO), true);
                log.info("==========update user:{} success by uid:{}",
                        JSON.toJSONString(userResponseDTO),
                        uid);
                return JsonResponse.ok(userResponseDTO);
            }
        }
        log.warn("==========update user failed:{} by uid:{}",
                JSON.toJSONString(userUpdateRequestDTO),
                uid);
        return JsonResponse.errorMsg("更新用户失败");
    }

    @PostMapping("/upload")
    @ApiOperation(tags = "用户头像上传", value = "用户头像上传")
    public JsonResponse uploadFile(
            @RequestParam String uid,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (null == file) {
            return JsonResponse.errorMsg("文件不能为空");
        }
        String filePathDir = IMG_FACE_UPLOAD_PATH;
        // 每个用户单独存储一个目录
        String uploadPathPrefix = File.separator + uid;
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
        String newFileName = "face-" + uid + "-"
                + System.currentTimeMillis()
                + "." + fileSuffix;
        //文件上传的最终保存位置
        String fileSavePath = filePathDir + uploadPathPrefix + File.separator + newFileName;
        String faceWebUrl = IMG_FACE_BASE_WEB_URL + "/face-img/" + uid + "/" + newFileName;
        File fileOut = new File(fileSavePath);
        if (fileOut.getParentFile() != null) {
            //创建存储文件夹
            fileOut.getParentFile().mkdirs();
            //输出文件到保存目录
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(fileOut);
                InputStream inputStream = file.getInputStream();
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        val userFace = this.userCenterLoginUserService.updateUserFace(uid, faceWebUrl);
        if (null != userFace) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(userFace, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                        JSON.toJSONString(userResponseDTO), true);
                log.info("==========update user:{} success by uid:{}",
                        JSON.toJSONString(userResponseDTO),
                        uid);
                return JsonResponse.ok(userResponseDTO);
            }
        }
        return JsonResponse.ok();
    }
}
