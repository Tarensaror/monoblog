package de.hska.lkit.demo.web.redis.repo;

import java.util.ArrayList;
import java.util.Set;

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

import de.hska.lkit.demo.web.redis.model.UserData;
import de.hska.lkit.demo.web.redis.repo.UserDataRepo;

@Repository
public class FollowRepo {
	
	private static String KEY_SUFFIX_FOLLOWER = ":followers";
	private static String KEY_SUFFIX_FOLLOWING = ":following";
	private static String KEY_PREFIX_USER = "user:";
	
	private UserDataRepo userdatarepo;
	private UIDRepo uidrepo;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	private ValueOperations<String, String> srt_valOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public FollowRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, UserDataRepo userdata, UIDRepo uid) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.userdatarepo = userdata;
		this.uidrepo = uid;
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_valOps = stringRedisTemplate.opsForValue();
	}
	
	public void follow(String id, String name) {
		updateFollowersAdd(id, name);
		updateFollowingAdd(id, name);
	}
	
	public void unfollow(String id, String name) {
		updateFollowersDelete(id, name);
		updateFollowingDelete(id, name);
	}
	
	private void updateFollowersAdd(String idOfCurrentUser, String name) {
		String userKey = KEY_PREFIX_USER + idOfCurrentUser + KEY_SUFFIX_FOLLOWER;
		srt_setOps.add(userKey, name);
	}
	
	private void updateFollowingAdd(String idOfCurrentUser, String name) {
		String userName = userdatarepo.getName(idOfCurrentUser);
		String otherUserId = uidrepo.getId(name);
		
		System.out.println(otherUserId);
		
		String userKey = KEY_PREFIX_USER + otherUserId + KEY_SUFFIX_FOLLOWING;
		srt_setOps.add(userKey, userName);
		
	}
	
	private void updateFollowersDelete(String idOfCurrentUser, String name) {
		String key = KEY_PREFIX_USER + idOfCurrentUser + KEY_SUFFIX_FOLLOWER;
		srt_setOps.remove(key, name);
		
	}
	
	private void updateFollowingDelete(String idOfCurrentUser, String name) {
		String userName = userdatarepo.getName(idOfCurrentUser);
		String otherUserId = uidrepo.getId(name);
		
		String userKey = KEY_PREFIX_USER + otherUserId + KEY_SUFFIX_FOLLOWING;
		srt_setOps.remove(userKey, userName);
	}
	
	public ArrayList<String> getFollowers(String userId) {
		String key = KEY_PREFIX_USER + userId + KEY_SUFFIX_FOLLOWER;
		
		if(srt_setOps.size(key) == 0) {
			return new ArrayList<String>(1);
		}
		Set<String> followers = srt_setOps.members(key);
		return new ArrayList<String>(followers);
	}
	
	public ArrayList<String> getFollowing(String userId) {
		String key = KEY_PREFIX_USER + userId + KEY_SUFFIX_FOLLOWING;
		
		if(srt_setOps.size(key) == 0) {
			return new ArrayList<String>(1);
		}
		Set<String> following = srt_setOps.members(key);
		return new ArrayList<String>(following);
	}
	
}
