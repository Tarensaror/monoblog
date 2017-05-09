package de.hska.lkit.demo.redis.repo;

import de.hska.lkit.demo.redis.model.Post;

public interface PostRepository {
	
	public void savePost(Post post);

}
