package com.openmpy.taleswiki.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(final String message) {
        super(message);
    }
}
