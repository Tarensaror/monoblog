package de.hska.lkit.demo.web;

public class Result {

	private String command;
    private String message;
    private boolean success;
    private Object userdata;

    public Result() {
    	this(null, null, true);
    }
    
    public Result(String command, String message) {
    	this(command, message, true);
    }

    public Result(String command, boolean success) {
    	this(command, null, success);
    }
    
    public Result(String command, Object userdata) {
    	this(command, null, true, userdata);
    }
    
    public Result(String command, String message, boolean success) {
    	this(command, message, success, null);
    }
    
    public Result(String command, String message, boolean success, Object userdata) {
    	this.command = command;
        this.message = message;
        this.success = success;
        this.userdata = userdata;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isSuccessful() {
    	return success;
    }
    
    public boolean isSuccess() {
    	return isSuccessful();
    }
    
    public Object getUserdata() {
    	return userdata;
    }

}