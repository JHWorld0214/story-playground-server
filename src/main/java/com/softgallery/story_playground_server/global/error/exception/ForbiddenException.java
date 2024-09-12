package com.softgallery.story_playground_server.global.error.exception;

import com.softgallery.story_playground_server.global.error.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}