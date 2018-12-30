package com.klmklm.meeting_attendance.lib;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class GlobalResponseHandler implements HandlerMethodReturnValueHandler {
    private HandlerMethodReturnValueHandler proxyObject;

    GlobalResponseHandler(HandlerMethodReturnValueHandler proxyObject) {
        this.proxyObject = proxyObject;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return proxyObject.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(
            Object returnValue,
            MethodParameter returnType,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest
    ) throws Exception {
        Response response = new Response(STATUS_CODE_SUCCEEDED,"",returnValue);
        proxyObject.handleReturnValue(response, returnType, mavContainer, webRequest);
    }

    private static final Integer STATUS_CODE_SUCCEEDED = 0;
}
