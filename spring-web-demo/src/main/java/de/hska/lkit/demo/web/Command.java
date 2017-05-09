package de.hska.lkit.demo.web;


public class Command {
	
	public static Result process(Request request) {
		String command = request.getCommand();
		String[] arguments = request.getArguments().split("(\\s)+");
		String token = request.getToken();
		
		switch(command) {
		case "register": return register(command, arguments, token);
		case "login": return login(command, arguments, token);
		default: return noSuchCommand(command, arguments, token);
		}
	}
	
	private static Result register(String command, String[] arguments, String token) {
		if (arguments.length != 3) return new Result(command, "Invalid armount of operands given", false);
		if (token != null) return new Result(command, "Can't create account while being loged in", false);
		if (!arguments[1].equals(arguments[2])) return new Result(command, "Passwords don't match", false);
		//if (Redis.register(arguments[0], arguments[1])) return new Result(command);
		return new Result(command, false);
	}
	
	private static Result login(String command, String[] arguments, String token) {
		if (arguments.length != 2) return new Result(command, "Invalid amount of operands given", false);
		if (token != null) return new Result(command, "Already loged in", false);
		
		/*String token = Redis.login(arguments[0], arguments[1]);
		if (token != null) return new Result(command, (Object) token);*/
		return new Result(command, false);
	}
	
	private static Result noSuchCommand(String command, String[] arguments, String token) {
		return new Result(command, "Command not found", false);
	}
}