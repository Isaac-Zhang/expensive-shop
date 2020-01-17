package com.liferunner.api.interceptors;

import com.alibaba.fastjson.JSON;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * UserTokenInterceptor for 添加用户权限拦截器
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/1/17
 **/
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserTokenInterceptor implements HandlerInterceptor {

    private final RedisUtils redisUtils;

    private static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 请求controller之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.printf("拦截器");
        val userId = request.getHeader("headerUserId");
        val userToken = request.getHeader("headerUserToken");
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            val userTokenFromRedis = redisUtils.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isNotBlank(userTokenFromRedis) && userTokenFromRedis.equalsIgnoreCase(userToken)) {
                return true;
            }
        }
        returnErrorObj(response, JsonResponse.errorMsg("请重新登录！"));
        return false;
    }

    private void returnErrorObj(HttpServletResponse response, JsonResponse jsonResponse) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(JSON.toJSONBytes(jsonResponse));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 请求controller之后，视图据渲染之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 视图数据渲染之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
