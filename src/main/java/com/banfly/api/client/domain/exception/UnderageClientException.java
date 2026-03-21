package com.banfly.api.client.domain.exception;

public class UnderageClientException extends RuntimeException{
    public UnderageClientException(int age){
        super("Client must be at least 18 years old. Current age: " + age);
    }
}
