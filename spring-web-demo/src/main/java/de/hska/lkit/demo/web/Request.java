package de.hska.lkit.demo.web;


public class Request {

    private String command;
    private String arguments;
    private String token;

    public Request() {}

    public Request(String command, String arguments, String token) {
        this.command = command;
        this.arguments = arguments;
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