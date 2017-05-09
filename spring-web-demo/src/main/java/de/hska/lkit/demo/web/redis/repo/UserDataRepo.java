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

// import de.hska.lkit.demo.web.redis.model.User;
import de.hska.lkit.demo.web.redis.model.UserData;

@Repository
public class UserDataRepo {
	
	private static String KEY_PREFIX_USER = "user:";
	
	private RedisAtomicLong userid;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public UserDataRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.userid = new RedisAtomicLong("userid", stringRedisTemplate.getConnectionFactory());
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
	}

	public void saveUserData(UserData userdata) {
		String id = String.valueOf(userid.incrementAndGet());
		userdata.setId(id);
		
		String key = (KEY_PREFIX_USER + id);
		
		
		
		srt_hashOps.put(key, "name", userdata.getName());
		srt_hashOps.put(key, "password", userdata.getPassword());
		
//		rt_hashOps.put(key, id, userdata);
				
		
	}
	
//	public UserData getUserData(String id) {
//		UserData userdata = new UserData();
//		
//		if (rt_hashOps.get(KEY_PREFIX_USER, KEY_PREFIX_USER + id) != null) {
//			
//		}
//	}

}
