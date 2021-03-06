package com.guns21.web.advice;


import com.google.common.base.Throwables;
import com.guns21.common.exception.CurrentUserIsNullException;
import com.guns21.common.exception.IllegalInputArgumentException;
import com.guns21.domain.result.light.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 异常处理
 * 即把@ControllerAdvice注解内部使用@ExceptionHandler、@InitBinder、@ModelAttribute注解的方法应用到所有的
 *
 * @RequestMapping注解的方法。非常简单，不过只有当使用@ExceptionHandler最有用，另外两个用处不大。
 */
@ControllerAdvice(annotations = RestController.class)
class ApiExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandlerAdvice.class);

    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {NoSuchElementException.class})
    @ResponseBody
    public Result dataNotFound(Exception exception,  WebRequest request) {
        LOGGER.error("url ["+request.getDescription(false)+"] data Not Found", exception);
        return Result.fail("0000002", "输入数据无效");
    }


    /**
     * @param exception
     * @return
     */
    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseBody
    public Result nullPointerException(Exception exception, WebRequest request) {
        LOGGER.error("url ["+request.getDescription(false)+"] has null Pointer Exception", exception);
        return Result.fail(firstThrowableAsString(exception));
    }

    /**
     * 处理javax.validation中的各类注解校验信息
     */
    @ExceptionHandler(value = {BindException.class })
    @ResponseBody
    public Result exceptionBind(BindException exception, BindingResult bindingResult, WebRequest request) {
        return validation(bindingResult.getAllErrors(), request);
    }
    /**
     * 处理validation 注解校验信息
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class })
    @ResponseBody
    public Result exceptionValid(MethodArgumentNotValidException valiException, WebRequest request) {
        return validation(valiException.getBindingResult().getAllErrors(), request);
    }

    /**
     * 错误信息描述：错误码|错误描述
     *
     * @param objectErrors
     * @return
     */
    protected Result validation(List<ObjectError> objectErrors, WebRequest request) {
        for (ObjectError fieldError : objectErrors) {
            String message = fieldError.getDefaultMessage();
            if (fieldError instanceof FieldError) {
                LOGGER.error("url [{}] validation field [{}] [{}]", request == null ? "" : request.getDescription(false), ((FieldError) fieldError).getField(), message);
            } else {
                LOGGER.error("url [{}] validation objectName [{}] [{}]", request == null ? "" : request.getDescription(false), fieldError.getObjectName(), message);
            }

            if (!message.contains(":")) {
                return Result.fail(message);
            }
            String[] split = StringUtils.split(message, ":");
            if (split.length == 2 && NumberUtils.isNumber(split[0])) {
                return Result.fail(split[1], split[0]);
            } else {
                return Result.fail(message);
            }
        }
        return Result.fail("解析message系统异常。");
    }

    /**
     * 类型转换异常
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = TypeMismatchException.class)
    @ResponseBody
    public Result typeMismatchException(TypeMismatchException exception, WebRequest request) {
        Result resultData = Result.fail();
        resultData.setCode(Result.Code.TYPE_VIOLATION.getCode());
        resultData.setMessage(Result.Code.TYPE_VIOLATION.getText() + ":参数需要[" + exception.getRequiredType().getSimpleName() + "]类型，但传入值为{" + exception.getValue() + "}");

        LOGGER.error("url [{}] type mismatch : {} ", request.getDescription(false), resultData.getMessage());
        return resultData;
    }

    /**
     * 丢失必填参数异常，参数注解使用@RequestParam、@PathVariable，@Valid验证不会在本函数处理
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public Result missingServletRequestParameterException(MissingServletRequestParameterException exception, WebRequest request) {
        Result resultData = Result.fail();
        resultData.setCode(Result.Code.MISS_PARAMETER.getCode());
        resultData.setMessage(exception.getMessage());
        LOGGER.error("url [{}] missing servlet request parameter : {} ", request.getDescription(false), resultData.getMessage());
        return resultData;
    }

    @ExceptionHandler(value = CurrentUserIsNullException.class)
    @ResponseBody
    public Result currrentUserIsNull(CurrentUserIsNullException exception, WebRequest request) {
        LOGGER.error("url ["+request.getDescription(false)+"] Current user is null");
        return Result.fail401("请登录.");
    }

    /**
     * Handle exceptions thrown by handlers.
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exception(Exception exception, WebRequest request) {
        LOGGER.error("url ["+request.getDescription(false)+"] has exception", exception);
        if (exception instanceof IllegalInputArgumentException) {
            return Result.fail(exception.getMessage(), ((IllegalInputArgumentException) exception).getCode());
        } else {
            return Result.fail(Throwables.getRootCause(exception).getLocalizedMessage());
        }
    }


    private String firstThrowableAsString(Throwable throwable) {
        List<Throwable> causalChain = Throwables.getCausalChain(throwable);
        String causal = causalChain.size() >= 1 ? Throwables.getStackTraceAsString(causalChain.get(0)) : "空异常信息";
        return StringUtils.left(causal, 200);
    }
}