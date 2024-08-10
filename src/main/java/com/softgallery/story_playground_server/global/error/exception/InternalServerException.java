package com.softgallery.story_playground_server.global.error.exception;


import com.softgallery.story_playground_server.global.error.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}