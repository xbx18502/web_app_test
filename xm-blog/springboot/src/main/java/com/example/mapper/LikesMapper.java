package com.example.mapper;

import com.example.entity.Likes;

public interface LikesMapper {
    void insert(Likes likes);

    Likes selectUserLikes(Likes likes);

    void deleteById(Integer id);
}
