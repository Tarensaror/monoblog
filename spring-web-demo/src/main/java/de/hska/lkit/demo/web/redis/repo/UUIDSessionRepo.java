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

import de.hska.lkit.demo.web.redis.model.UUIDSession;

@Repository
public class UUIDSessionRepo {

	private static String KEY_PREFIX_SESSION = "session:";
	private static String KEY_PREFIX_USER = "user:";
	private static String KEY_PREFIX_NAME = "name:";
	
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
			saveSession(uuid);
			
			return uuid.getUUID();
		} else {
			return null;
		}
	}

	public void saveSession(UUIDSession uuid) {

		/* TODO DB eintrag des Hashs */
		String key = KEY_PREFIX_SESSION + uuid.getUUID();
		srt_hashOps.put(key, "userid", uuid.getUserID());
		
		//DEBUG
		System.out.println("I Put the session to key: " + key + " and i wrote " + uuid.getUserID());
		
	}
	
	
	private String rollUUID() {
		String temp = UUID.randomUUID().toString();
		while(isExistingUUID(temp)) {
			temp = UUID.randomUUID().toString();
		}
		return temp;
	}
	
	
	public boolean isExistingUUID(String uuid) {
		// KEY_PREFIX_SESSION + uuid, String
		
		StringRedisTemplate foo = new StringRedisTemplate();
		String value = foo.opsForValue().get(KEY_PREFIX_SESSION + uuid);
		
		
		 if(foo.hasKey(KEY_PREFIX_SESSION + uuid)) {
			  return false;
		 } else {
			 return true;
		 }
	}
	
	private boolean isCorrectLogin(UUIDSession uuid) {
		
		UUIDSession temp = new UUIDSession();

		StringRedisTemplate foo = new StringRedisTemplate();
		uuid.setUserID(foo.opsForValue().get(KEY_PREFIX_NAME + uuid.getName()));
		
		temp.setName(srt_hashOps.get(KEY_PREFIX_USER + uuid.getUserID(), "name"));
		temp.setPassword(srt_hashOps.get(KEY_PREFIX_USER + uuid.getUserID(), "password"));
		
		if( (temp.getName().equals(uuid.getName())) && (temp.getPassword().equals(uuid.getPassword()))) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean isExistingUser(String name) {
		
		if(stringRedisTemplate.hasKey(KEY_PREFIX_NAME + name)) {
			  return true;
		 } else {
			 return false;
		 }
	}	
}