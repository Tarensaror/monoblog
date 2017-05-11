package de.hska.lkit.demo.web;


public class Request {

    private String command;
    private String lastCommand;
    private String arguments;
    private String token;

    public Request() {}

    public Request(String command, String lastCommand, String arguments, String token) {
        setCommand(command);
        setLastCommand(lastCommand);
        setArguments(arguments);
        setToken(token);
    }

    public void setCommand(String command) {
    	this.command = command.trim();
    }
    
    public void setLastCommand(String lastCommand) {
    	this.lastCommand = lastCommand;
    }
    
    public void setArguments(String arguments) {
    	this.arguments = arguments;
    }
    
    public void setToken(String token) {
    	this.token = token;
    }
    
    public String getCommand() {
        return command == null ? "" : command;
    }
    
    public String getLastCommand() {
    	return lastCommand == null ? "" : lastCommand;
    }
    
    public String getArguments() {
    	return arguments == null ? "" : arguments;
    }

    public String getToken() {
        return token;
    }
}