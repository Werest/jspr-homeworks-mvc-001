package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryStubImpl implements PostRepository {

  private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();

  private final AtomicLong idPost = new AtomicLong();

  public List<Post> all() {
    //new ArrayList<>(posts.values())
    if (!posts.isEmpty()) {
      return posts.values().stream().filter(x -> !x.isRemoved()).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public Optional<Post> getById(long id) {
    //Optional.ofNullable(posts.get(id))
    if (!posts.get(id).isRemoved()) {
      return Optional.ofNullable(posts.get(id));
    } else {
      throw new NotFoundException("Пост был удалён!");
    }
  }

  public Post save(Post post) {
    Post tempPost;
    //Если от клиента приходит пост с id=0, значит, это создание нового поста.
    if (post.getId() == 0) {
      idPost.incrementAndGet();
      tempPost = new Post(idPost.get(), post.getContent());
      posts.put(idPost.get(), tempPost);
    }
    //Если от клиента приходит пост с id !=0, значит, это сохранение (обновление) существующего поста.
    else if (posts.containsKey(post.getId())) {
      tempPost = new Post(post.getId(), post.getContent());
      posts.replace(post.getId(), tempPost);
    } else {
      throw new NotFoundException("Не найден элемент с id - " + post.getId());
    }
    return tempPost;
  }

  public void removeById(long id) {
    if (!posts.get(id).isRemoved()) {
      posts.get(id).setRemoved(true);
    } else {
      throw new NotFoundException("Пост был удалён!");
    }
  }
}