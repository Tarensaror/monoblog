package de.hska.lkit.demo.web.redis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

public class UID {
	/* BenutzerID 
	 * muss einzigigartig sein 
	 * 
	 * in redis
	 * mapping name -> uid  
	 * 
	 */
	
	/**
	 * 
	 */
	private static final String KEY_SET_ALL_USERNAMES 	= "all:usernames";

	private static final String KEY_ZSET_ALL_USERNAMES 	= "all:usernames:sorted";
	
	private static final String KEY_HASH_ALL_USERS 		= "all:user";
	
	private static final String KEY_PREFIX_USER 	= "user:";

	/**
	 * to generate unique ids for user
	 */
	private RedisAtomicLong userid;
	
	

	/**
	 * to save data in String format
	 */
	private StringRedisTemplate stringRedisTemplate;


	/**
	 * hash operations for stringRedisTemplate
	 */
	private HashOperations<String, String, String> srt_hashOps;

	/**
	 * set operations for stringRedisTemplate
	 */
	private SetOperations<String, String> srt_setOps;
	
	/**
	 * zset operations for stringRedisTemplate
	 */
	private ZSetOperations<String, String> srt_zSetOps;
	
	
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
	}
	
	
	
	public static boolean createUser(String name) {
		
		//ready template
		
		
		
		
		/*TODO Lookup in DBase
		* if none exists 
		* create new user
		* return true
		* else false
		*/
		
		return false;
	}
	
	private static boolean isExistingUser(String name) {
		//TODO
		//ready template and retrun true if user exists else return false
		return false;
	}
	
	private static long getUID() {
		//TODO Lookup for next uid + increment (atomic long)
		return 12345678;
	}
	
	
	
	
	
	
}