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

// import de.hska.lkit.demo.web.redis.model.User;
import de.hska.lkit.demo.web.redis.model.UserData;
import de.hska.lkit.demo.web.redis.repo.UIDRepo;

@Repository
public class UserDataRepo {
	
	private static String KEY_PREFIX_USER = "user:";
	private static String KEY_SUFFIX_USERDATA = ":userdata";
	
//	private RedisAtomicLong userid;
//	private UIDRepo uidrepo;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	private ValueOperations<String, String> srt_valOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public UserDataRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		//this.userid = new RedisAtomicLong("userid", stringRedisTemplate.getConnectionFactory());
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_valOps = stringRedisTemplate.opsForValue();
	}

	public void saveUserData(UserData userdata) {
			
		String key = (KEY_PREFIX_USER + userdata.getId() + KEY_SUFFIX_USERDATA);
		
		srt_hashOps.put(key, "name", userdata.getName());
		srt_hashOps.put(key, "password", userdata.getPassword());	
		
	}
	
	public String getName(String id) {
		String key = KEY_PREFIX_USER + id + KEY_SUFFIX_USERDATA;
		
		if(stringRedisTemplate.hasKey(key)) {
			System.out.println("found");
			return (srt_hashOps.get(key, "name"));
		}
		else {
			//TODO: find suitable return
			return "";
		}	
	}
	
	public String getPassword(String id) {
		
		String key = KEY_PREFIX_USER + id + KEY_SUFFIX_USERDATA;
		
		if(stringRedisTemplate.hasKey(key)) {
			return (srt_hashOps.get(key, "password"));
		}
		else {
			//TODO: find suitable return
			return "";
		}
	}

}
