package com.example.service;

import com.example.entity.Account;
import com.example.entity.Likes;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikesServices {
    @Resource
    LikesMapper likesMapper;
    public void set(Likes likes){
        Account currentUser = TokenUtils.getCurrentUser();
        likes.setUserId(currentUser.getId());
        likes = likesMapper.selectUserLikes(likes);
        if(likes==null){
            likesMapper.insert(likes);
        }
        else{
            likesMapper.deleteById(likes.getId());
        }

    }

}
