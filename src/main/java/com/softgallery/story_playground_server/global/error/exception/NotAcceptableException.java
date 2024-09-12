package com.softgallery.story_playground_server.global.error.exception;

import com.softgallery.story_playground_server.global.error.ErrorCode;

public class NotAcceptableException extends BusinessException{
    public NotAcceptableException() {
        super(ErrorCode.NOT_ACCEPTABLE);
    }
}
