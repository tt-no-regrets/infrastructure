package com.guns21.jackjson.servlet.mvc.annotaion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guns21.jackjson.JaskonMixinCache;
import com.guns21.jackjson.annotation.JsonMixin;
import com.guns21.jackjson.annotation.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Objects;

public class JsonResponseRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private  static final Logger log = LoggerFactory.getLogger(JsonResponseRequestMappingHandlerMapping.class);

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        if (Objects.nonNull(objectMapper)) {
            JsonResponse annotation = method.getAnnotation(JsonResponse.class);
            if (Objects.nonNull(annotation)) {
                for (JsonMixin jsonMixin : annotation.mixins()) {
                    JaskonMixinCache.put(method.toString(),objectMapper.copy().addMixIn(jsonMixin.target(), jsonMixin.mixin()));
                    log.info("Add mixin for mapping {} onto {} ", mapping, method);
                }
            }
        }
        super.registerHandlerMethod(handler, method, mapping);
    }
}
