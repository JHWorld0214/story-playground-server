package com.softgallery.story_playground_server.global.error.exception;


import com.softgallery.story_playground_server.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}