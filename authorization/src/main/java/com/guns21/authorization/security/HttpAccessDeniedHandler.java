package com.guns21.authorization.security;

import com.guns21.domain.result.light.Result;
import com.guns21.servlet.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by ljj on 17/6/22.
 */
@Service
public class HttpAccessDeniedHandler implements AccessDeniedHandler {
    @Value("${com.guns21.security.message.access-denied:没有访问权限！}")
    private String accessDeniedMessage;

    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ResponseUtils.writeResponse(response, Result.fail403(accessDeniedMessage));
    }
}
