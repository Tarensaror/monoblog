package de.hska.lkit.demo.web.redis.repo;

import java.util.ArrayList;
import java.util.HashSet;
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
import de.hska.lkit.demo.web.redis.model.Post;
import de.hska.lkit.demo.web.redis.repo.UIDRepo;
import de.hska.lkit.demo.web.redis.repo.UserDataRepo;
import de.hska.lkit.demo.web.redis.repo.FollowRepo;
import de.hska.lkit.demo.web.redis.repo.PostRepo;

@Repository
public class TimelineRepo {
	
	private static String KEY_PREFIX_USER = "user:";
	private static String KEY_SUFFIX_TIMELINE = ":timeline";
	
	private UIDRepo uidRepository;
	private FollowRepo followRepository;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	private ValueOperations<String, String> srt_valOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public TimelineRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, UIDRepo uidrepo, FollowRepo followRepository) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.uidRepository = uidrepo;
		this.followRepository = followRepository;
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_valOps = stringRedisTemplate.opsForValue();
	}
	
	public void updateTimelines(String userIDAuthor, String postID) {
		String key = KEY_PREFIX_USER + userIDAuthor + KEY_SUFFIX_TIMELINE;
		srt_zSetOps.add(key, postID, Double.parseDouble(postID));
		
		ArrayList<String> follower = followRepository.getFollowers(userIDAuthor);
		for(String user: follower) {
			String id = uidRepository.getId(user);
			srt_zSetOps.add(KEY_PREFIX_USER + id + KEY_SUFFIX_TIMELINE, postID, Double.parseDouble(postID));
		}		
	}
	
	public ArrayList<String> getFullTimeline(String username) {
		String key = KEY_PREFIX_USER + uidRepository.getId(username) + KEY_SUFFIX_TIMELINE;
		Set<String> posts = srt_zSetOps.range(key, 0, srt_zSetOps.size(key) - 1);
		return new ArrayList<String>(posts);
	}
	
	public ArrayList<String> getTimelineByUserBefore(String user, String maxid, int numberOfPosts) {
		int max = Integer.valueOf(maxid);
		String key = KEY_PREFIX_USER + user + KEY_SUFFIX_TIMELINE;
		Set<String> posts = srt_zSetOps.reverseRangeByScore(key, 0, max - 1);
		
		ArrayList<String> result = new ArrayList<String>(posts);
		if(result.size() >= numberOfPosts) {
			return new ArrayList<>(result.subList(0, numberOfPosts));
		}
		else {
			return new ArrayList<>(result.subList(0, result.size()));
		}
		
	}

}
