package com.softgallery.story_playground_server.global.error.exception;


import com.softgallery.story_playground_server.global.error.ErrorCode;

public class ConflictException extends BusinessException {
    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}