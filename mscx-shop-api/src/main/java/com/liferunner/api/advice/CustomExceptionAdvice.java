package com.liferunner.api.advice;

import com.liferunner.utils.JsonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * CustomExceptionAdvice for : 自定义异常advice
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/24
 */
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JsonResponse handlerMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return JsonResponse.errorMsg("图片大小不能超过200kb");
    }
}
