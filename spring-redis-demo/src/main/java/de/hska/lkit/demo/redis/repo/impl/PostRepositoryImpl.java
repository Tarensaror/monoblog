package de.hska.lkit.demo.redis.repo.impl;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.PostRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;


public class PostRepositoryImpl implements PostRepository {

	private static final String KEY_PREFIX_POST 	= "post:";
	
	private RedisAtomicLong postId;
	
	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, String> srt_hashOps;
	private SetOperations<String, String> srt_setOps;
	private ZSetOperations<String, String> srt_zSetOps;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Post> rt_hashOps;
	
	
	@Autowired
	public PostRepositoryImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		this.postId = new RedisAtomicLong("postId", stringRedisTemplate.getConnectionFactory());
	}
	
	@PostConstruct
	private void init() {
		srt_hashOps = stringRedisTemplate.opsForHash();
		srt_setOps = stringRedisTemplate.opsForSet();
		srt_zSetOps = stringRedisTemplate.opsForZSet();
	}
	
	
	
	@Override
	public void savePost(Post post) {
		String id = String.valueOf(postId.incrementAndGet());
		post.setPostId(id);
		
		String key = KEY_PREFIX_POST + id;
		
		srt_hashOps.put(key, "id", post.getPostId());
		srt_hashOps.put(key, "message", post.getMessage());
		srt_hashOps.put(key, "user", post.getUser().getUsername());
		srt_hashOps.put(key, "date", post.getDate().toString());
		
		rt_hashOps.add("all:posts", key, post);
		
	}
	
}
