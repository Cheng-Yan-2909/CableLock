package com.chengyan.cablelock.exception;

import java.lang.RuntimeException;

public class ObjectNotInitializedException extends RuntimeException {
    public ObjectNotInitializedException(String s) {
        super(s);
    }
}
