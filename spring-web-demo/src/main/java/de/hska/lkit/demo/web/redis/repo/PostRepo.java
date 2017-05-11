package de.hska.lkit.demo.web.redis.repo;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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
import de.hska.lkit.demo.web.redis.repo.TimelineRepo;

@Repository
public class PostRepo {
	
	private static String KEY_PREFIX_POST = "post:";
	private static String KEY_PREFIX_USER = "user:";
	private static String KEY_SUFFIX_POST = ":post";
	
	private RedisAtomicLong postid;
	private UIDRepo uidRepository;
	private TimelineRepo timeline;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	private ValueOperations<String, String> srt_valOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, UserData> rt_hashOps;
	
	@Autowired
	public PostRepo(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, UIDRepo uidrepo, TimelineRepo timeline) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.postid = new RedisAtomicLong("postid", stringRedisTemplate.getConnectionFactory());
		this.uidRepository = uidrepo;
		this.timeline = timeline;
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
		srt_valOps = stringRedisTemplate.opsForValue();
	}
	
	public void createPost(String name, String message) {
		String current = LocalDateTime.now().toString();
		
		String id = String.valueOf(postid.incrementAndGet());
		String key = KEY_PREFIX_POST + id;
		
		srt_hashOps.put(key, "time", current);
		srt_hashOps.put(key, "author", name);
		srt_hashOps.put(key, "message", message);
		
		String userPostKey = KEY_PREFIX_USER + uidRepository.getId(name) + KEY_SUFFIX_POST;
		srt_setOps.add(userPostKey, id);		
		
		timeline.updateTimelines(uidRepository.getId(name), id);
	
	}
	
	public Post getPost(String postID) {
		String key = KEY_PREFIX_POST + postID;
		if (stringRedisTemplate.hasKey(key)) {
			Post post = new Post();
		
			post.setTime(srt_hashOps.get(key, "time"));
			post.setName(srt_hashOps.get(key, "author"));
			post.setMessage(srt_hashOps.get(key, "message"));
		
			return post;
		}
		//TODO: find suitable return
		return new Post();
	}
	
	public ArrayList<String> getAllPostsByUser(String username) {
		String key = KEY_PREFIX_USER + uidRepository.getId(username) + KEY_SUFFIX_POST;
		Set<String> posts = srt_setOps.members(key);
		return new ArrayList<String>(posts);
	}
	
	public ArrayList<String> getPostsByUserBefore(String username, String id, int numberOfPosts) {
	
		int maxID = Integer.valueOf(id);
	
		String key = KEY_PREFIX_USER + uidRepository.getId(username) + KEY_SUFFIX_POST;
		Set<String> postIds = srt_setOps.members(key);
		ArrayList<String> result = new ArrayList<String>(postIds);
		
		result.sort(new Comparator<String>() {
		    public int compare(String o1, String o2) {
		        Integer i1 = Integer.parseInt(o1);
		        Integer i2 = Integer.parseInt(o2);
		        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
		    }
		});
		
		if(result.size() >= numberOfPosts) {
			return new ArrayList<>(result.subList(0, numberOfPosts));
		}
		else {
			return new ArrayList<>(result.subList(0, result.size()));
		}
		
		
	}
	
}
