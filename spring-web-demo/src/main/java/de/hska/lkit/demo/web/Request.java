package de.hska.lkit.demo.web;


public class Request {

    private String command;
    private String arguments;
    private String token;

    public Request() {}

    public Request(String command, String arguments, String token) {
        setCommand(command);
        setArguments(arguments);
        setToken(token);
    }

    public void setCommand(String command) {
    	this.command = command.trim();
    }
    
    public void setArguments(String arguments) {
    	this.arguments = arguments;
    }
    
    public void setToken(String token) {
    	this.token = token;
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getArguments() {
    	return arguments;
    }

    public String getToken() {
        return token;
    }
}