package com.accelerator.dpwc.exception;

import com.accelerator.framework.exception.CustomException;

public class AuthException extends CustomException {

    private static final long serialVersionUID = 9038829516720919458L;

    public AuthException(int code, Object... args) {
        super(code, args);
    }
}
