package com.softgallery.story_playground_server.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgallery.story_playground_server.global.error.dto.ErrorResponse;
import com.softgallery.story_playground_server.global.error.exception.UnauthorizedTokenException;
import com.softgallery.story_playground_server.service.auth.EndpointRegistry;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionControllerAdvice {

    private final ObjectMapper objectMapper;
    private final EndpointRegistry endpointRegistry;

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleWebClientResponseException() {
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("used auth token")
                .build();
    }

    // 404 처리: 존재하지 않는 엔드포인트
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("No handler found for " + ex.getRequestURL())
                .build();
    }

    // 401 처리: 인증 오류에 대한 구체적인 처리
    @ExceptionHandler(UnauthorizedTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleUnauthorizedTokenException(UnauthorizedTokenException ex) {
        return ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ServletException.class)
    @ResponseBody
    public ErrorResponse handleServletException(ServletException ex, WebRequest request) {
        System.out.println("path: " + request.getDescription(false));

        // 엔드포인트가 없을 때의 예외 처리
        if(!endpointRegistry.isEndpointExists(request.getDescription(false).substring(4))) {
//        if (request.getDescription(false).contains("path=")) {
            return ErrorResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("No handler found for " + request.getDescription(false).split("path=")[1])
                    .build();
        }

        // 그 외의 경우는 500 또는 적절한 상태 코드로 처리
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error: " + ex.getMessage())
                .build();
    }
}
