package com.yunus.exception;

public class BaseException extends RuntimeException {

    private final ErrorType errorType;

    public BaseException(ErrorType errorType, String detail) {

        super(errorType.getMessage() + "  " + detail);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

}
