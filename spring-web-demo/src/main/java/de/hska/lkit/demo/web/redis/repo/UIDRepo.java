package de.hska.lkit.demo.web.redis.repo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;

import de.hska.lkit.demo.web.redis.model.UID;
import de.hska.lkit.demo.web.redis.model.UserData;
import de.hska.lkit.demo.web.redis.repo.UserDataRepo;

@Repository
public class UIDRepo {
	/* BenutzerID 
	 * muss einzigigartig sein 
	 * 
	 * in redis
	 * mapping name -> uid  
	 * 
	 */
	
	
	private final String KEY_PREFIX_NAME 	= "name:";
	private final String KEY_SUFFIX_USER = ":user";

	private RedisAtomicLong userid;
	
	private UserDataRepo userdatarepo;

	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;


	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	private ValueOperations<String, String> srt_valOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public UIDRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, UserDataRepo userdata) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.userid = new RedisAtomicLong("userid", stringRedisTemplate.getConnectionFactory());
		this.userdatarepo = userdata;
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_valOps = stringRedisTemplate.opsForValue();
	}
	
	
	/**
	 * Creates new user by name
	 * @param name username
	 * @return true, if created successfully, false if already existed.
	 */
	public boolean createUser(String name, String password) {		
		String key = KEY_PREFIX_NAME + name + KEY_SUFFIX_USER;
		
		if(isExistingUser(name)) {
			System.out.println("Already there");
			return false;
		}
		else {
			System.out.println("did not exist");
			String id = String.valueOf(getNextUID());
			
			srt_valOps.set(key, id);
			
			UserData userdata = new UserData();
			userdata.setName(name);
			userdata.setPassword(password);
			userdata.setId(id);
			
			userdatarepo.saveUserData(userdata);
			System.out.println("created and saved");
			return true;
			
		}
		
	}
	
	/*
	 * Checks whether user already exists.
	 * True, if exists.
	 */
	private boolean isExistingUser(String name) {
		String key = KEY_PREFIX_NAME + name + KEY_SUFFIX_USER;
		
		if(stringRedisTemplate.hasKey(key)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Get next UserID.
	 */
	private long getNextUID() {
		//TODO Lookup for next uid + increment (atomic long)
		return userid.incrementAndGet();
	}
	
	/*
	 * Get id by name.
	 */
	public String getId(String name) {
		if(isExistingUser(name)) {
			System.out.println("exists");
//			return String.valueOf(srt_hashOps.get((KEY_PREFIX_NAME + name + KEY_SUFFIX_USER), "id"));
			return String.valueOf(srt_valOps.get(KEY_PREFIX_NAME + name + KEY_SUFFIX_USER));
		}
		return "none";
	}
	
	
	
	
	
}
