package com.MooneyB.common.exceptions;

public class ExpenseNotFoundException extends RuntimeException{
	public ExpenseNotFoundException(String message) {
        super(message);
    }
	public ExpenseNotFoundException(String message, Throwable cause) {
        super(message, cause);
	}
}
