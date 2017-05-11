package de.hska.lkit.demo.web.redis.repo;

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

import java.util.UUID;

import java.util.concurrent.TimeUnit;

import de.hska.lkit.demo.web.redis.model.UUIDSession;

@Repository
public class UUIDSessionRepo {

	private static long TIMEOUT = 20l;
	private static TimeUnit UNIT  = TimeUnit.MINUTES;
	
	private static String KEY_PREFIX_SESSION = "session:";
	private static String KEY_PREFIX_USER = "user:";
	private static String KEY_PREFIX_NAME = "name:";
	
	private static String KEY_SUFFIX_USER = ":user";
	private static String KEY_SUFFIX_USERDATA = ":userdata";
	
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UUIDSession> rt_hashOps;
	
	@Autowired
	public UUIDSessionRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
	}
		
	
	
	public String login(String name, String password) {
		UUIDSession uuid = new UUIDSession();

		uuid.setName(name);
		uuid.setPassword(password);

		if (isCorrectLogin(uuid)) {
			
			uuid.setUUID(rollUUID());
			
			System.out.println("Rolled this UUID:" + uuid.getUUID());
			
			saveSession(uuid);
			
			
			System.out.println("Login success");
			return uuid.getUUID();
		} else {
			System.out.println("Login failed");
			return null;
		}
	}

	public void saveSession(UUIDSession uuid) {
		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		String key = KEY_PREFIX_SESSION + uuid.getUUID() + KEY_SUFFIX_USER ;
		srt_hashOps.put(key, "userid", uuid.getUserID());
		foo.expire(key, TIMEOUT, UNIT);
		
		System.out.println("I Put the session to key: " + key + " and i wrote " + uuid.getUserID());
		
	}
	
	
	private String rollUUID() {
		String temp = UUID.randomUUID().toString();
		System.out.println("Got this SessionKey:" + temp);
		
		System.out.println("entered loop");
		while(isExistingUUID(temp)) {
			temp = UUID.randomUUID().toString();
		}
		System.out.println("exited loop");
		return temp;
	}
	
	
	public boolean isExistingUUID(String uuid) { 	
		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		String value = foo.opsForValue().get(KEY_PREFIX_SESSION + uuid + KEY_SUFFIX_USER);
		System.out.println("Found this: " + value + " for: " + uuid + "in DB");
		
		 if(value == null) {
			  return false;
		 } else {
			 return true;
		 }
	}
	
	private boolean isCorrectLogin(UUIDSession uuid) {
		
		UUIDSession temp = new UUIDSession();

		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		uuid.setUserID(foo.opsForValue().get(KEY_PREFIX_NAME + uuid.getName() + KEY_SUFFIX_USER));
		
		String tempName = srt_hashOps.get(KEY_PREFIX_USER + uuid.getUserID() + KEY_SUFFIX_USERDATA, "name");
		String tempPassword = srt_hashOps.get(KEY_PREFIX_USER + uuid.getUserID() + KEY_SUFFIX_USERDATA, "password");
		
		System.out.println(tempName);
		System.out.println(uuid.getName());
		
		System.out.println(tempPassword);
		System.out.println(uuid.getPassword());
		
		
		if(uuid.getName().equals(tempName) && uuid.getPassword().equals(tempPassword)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void refresh(String uuid) {
		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		String key = KEY_PREFIX_SESSION + uuid + KEY_SUFFIX_USER ;
		foo.expire(key, TIMEOUT, UNIT);
	}
	
	public void logout(String uuid) {
		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		String key = KEY_PREFIX_SESSION + uuid + KEY_SUFFIX_USER ;
		foo.delete(key);
	}
	
	
	
	private boolean isExistingUser(String name) {
		
		StringRedisTemplate foo = new StringRedisTemplate(stringRedisTemplate.getConnectionFactory());
		
		 if(foo.hasKey(foo.opsForValue().get(KEY_PREFIX_NAME + name + KEY_SUFFIX_USER))) {
			  return false;
		 } else {
			 return true;
		 }
	}
	
	public String getUserID(String uuid) {
		return srt_hashOps.get(KEY_PREFIX_SESSION + uuid + KEY_SUFFIX_USER, "userid");
	}
	
	
	
}