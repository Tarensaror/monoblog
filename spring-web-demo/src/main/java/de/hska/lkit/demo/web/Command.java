package de.hska.lkit.demo.web;

import de.hska.lkit.demo.web.redis.repo.UIDRepo;
import de.hska.lkit.demo.web.redis.repo.UUIDSessionRepo;
import de.hska.lkit.demo.web.redis.repo.UserDataRepo;
import de.hska.lkit.demo.web.redis.repo.FollowRepo;
import de.hska.lkit.demo.web.redis.repo.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Component
public class Command {
	
	private final UIDRepo uidRepository;
	private final UUIDSessionRepo uuidSessionRepository;
	private final UserDataRepo userDataRepository;
	private final FollowRepo followRepository;
	private final PostRepo postRepository;
	
	@Autowired
	public Command(UIDRepo uidRepository, UUIDSessionRepo uuidSessionRepository, UserDataRepo userDataRepository, FollowRepo followRepository, PostRepo postRepository) {
		this.uidRepository = uidRepository;
		this.uuidSessionRepository = uuidSessionRepository;
		this.userDataRepository = userDataRepository;
		this.followRepository = followRepository;
		this.postRepository = postRepository;
	}
	
	private String getSessionUserName(String token) {
		String id = uuidSessionRepository.getUserID(token);
		return userDataRepository.getName(id);
	}
		
	public Result process(Request request) {
		String command = request.getCommand();
		String[] arguments = request.getArguments().split("(\\s)+");
		if (arguments.length == 1 && arguments[0].equals("")) arguments = new String[] {};
		String token = request.getToken();
		
		if (token != null) uuidSessionRepository.refresh(token);
		
		System.out.println(command + " " + request.getArguments() + " " + token);
		
		switch(command) {
		case "register": return register(command, arguments, token);
		case "login": return login(command, arguments, token);
		case "follow": return follow(command, arguments, token);
		case "unfollow": return unfollow(command, arguments, token);
		case "follower": return follower(command, arguments, token);
		case "follows": return follows(command, arguments, token);
		case "post": return post(command, arguments, token);
		case "logout": return logout(command, arguments, token);
		case "profile": return profile(command, arguments, token);
		case "posts": return posts(command, arguments, token);
		case "timeline": return timeline(command, arguments, token);
		case "more": return more(command, request.getLastCommand(), arguments, token);
		case "find": return find(command, arguments, token);
		default: return noSuchCommand(command, arguments, token);
		}
	}
	
	private Result register(String command, String[] arguments, String token) {
		if (arguments.length != 3) return new Result(command, "Invalid armount of operands given", false);
		if (token != null) return new Result(command, "Can't create account while being loged in", false);
		if (!arguments[1].equals(arguments[2])) return new Result(command, "Passwords don't match", false);
		
		if (uidRepository.createUser(arguments[0], arguments[1])) return new Result(command, true);
		return new Result(command, "Registration error", false);
	}
	
	private Result login(String command, String[] arguments, String token) {
		if (arguments.length != 2) return new Result(command, "Invalid amount of operands given", false);
		if (token != null) return new Result(command, "Already loged in", false);
		
		token = uuidSessionRepository.login(arguments[0], arguments[1]);
		if (token != null) return new Result(command, (Object) new String[]{token, getSessionUserName(token)});
		return new Result(command, "Authentication error", false);
	}
	
	private Result follow(String command, String[] arguments, String token) {
		if (arguments.length != 1) return new Result(command, "Invalid amount of operands given", false);
		
		String id = uuidSessionRepository.getUserID(token);
		if (id == null) return new Result(command, "Authentication required", false);
		
		if (followRepository.follow(id, arguments[0])) return new Result(command, true);
		return new Result(command, "Failure", false);
	}
	
	private Result unfollow(String command, String[] arguments, String token) {
		if (arguments.length != 1) return new Result(command, "Invalid amount of operands given", false);
		
		String id = uuidSessionRepository.getUserID(token);
		if (id == null) return new Result(command, "Authentication required", false);
		
		followRepository.unfollow(id, arguments[0]);
		
		return new Result(command, true);
	}
	
	private Result follower(String command, String[] arguments, String token) {
		if (arguments.length > 1) return new Result(command, "Invalid amount of operands given", false);
		
		String id;
		if (arguments.length == 0) {
			id = uuidSessionRepository.getUserID(token);
			if (id == null) return new Result(command, "Invalid amount of operands given or authentication required", false);
		}
		else {
			id = uidRepository.getId(arguments[0]);
			if (id == null) return new Result(command, "No such user", false);
		}
		
		List<String> followers = followRepository.getFollowers(id);
		StringBuilder message = new StringBuilder();
		message.append("Follower:\n\n");
		
		if (followers.size() == 0) message.append("Everybody hates you!");
		else for (String follower : followers) message.append(follower).append('\n');
		
		return new Result(command, message.toString(), false);		
	}
	
	private Result follows(String command, String[] arguments, String token) {
		if (arguments.length > 1) return new Result(command, "Invalid amount of operands given", false);
		
		String id;
		if (arguments.length == 0) {
			id = uuidSessionRepository.getUserID(token);
			if (id == null) return new Result(command, "Invalid amount of operands given or authentication required", false);
		}
		else {
			id = uidRepository.getId(arguments[0]);
			if (id == null) return new Result(command, "No such user", false);
		}
		
		List<String> following = followRepository.getFollowing(id);
		StringBuilder message = new StringBuilder();
		message.append("Following:\n\n");
		
		if (following.size() == 0) message.append("You're all lonely and hate everybody!");
		else for (String fellow : following) message.append(fellow).append('\n');
		
		return new Result(command, message.toString(), false);	
	}
	
	private Result post(String command, String[] arguments, String token) {
		if (arguments.length == 0) return new Result(command, "Invalid amount of operands given", false);

		String name = getSessionUserName(token);
		if (name == null) return new Result(command, "Authentication required", false);
	
		if (arguments[0].length() > 140) return new Result(command, "Don't waste the precious memory... You're running on a 64k RAM machine!", false);
		
		postRepository.createPost(name, arguments[0]);
		return new Result(command, true);
	}
	
	private Result logout(String command, String[] arguments, String token) {
		uuidSessionRepository.logout(token);
		return new Result(command, true);
	}
	
	private Result profile(String command, String[] arguments, String token) {
		return null;
	}
	
	private Result posts(String command, String[] arguments, String token) {
		return null;
	}
	
	private Result timeline(String command, String[] arguments, String token) {
		return null;
	}
	
	private Result more(String command, String lastCommand, String[] arguments, String token) {
		return null;
	}
	
	private Result find(String command, String[] arguments, String token) {
		if (arguments.length != 1) return new Result(command, "Invalid amount of operands given", false);
		
		Set<String> users = userdataRepository.findUsersWith(arguments[0]);

		StringBuilder message = new StringBuilder();
		message.append("Matches:\n\n");
		
		if (users.size() == 0) message.append("(>*.*)> none <(*.*<)");
		else {
			for (String user : users) {
				message.append(user).append('\n');
			}
		}
		
		return new Result(command, message.toString(), true);
	}
	
	private Result noSuchCommand(String command, String[] arguments, String token) {
		return new Result(command, "Command not found", false);
	}
}