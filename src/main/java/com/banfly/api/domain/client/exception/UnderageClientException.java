package com.banfly.api.domain.client.exception;

public class UnderageClientException extends RuntimeException{
    public UnderageClientException(int age){
        super("Client must be at least 18 years old. Current age: " + age);
    }
}
