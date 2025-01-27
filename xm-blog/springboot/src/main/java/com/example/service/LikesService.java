package com.example.service;

import com.example.entity.Account;
import com.example.entity.Likes;
import com.example.exception.CustomException;
import com.example.mapper.LikesMapper;
import com.example.utils.TokenUtils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikesService {

    @Resource
    LikesMapper likesMapper;
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "blog", key = "#likes.fid", beforeInvocation = false)
    public void set(Likes likes) {
        try{
        Account currentUser = TokenUtils.getCurrentUser();
        likes.setUserId(currentUser.getId());
        Likes dblLikes = likesMapper.selectUserLikes(likes);
        
        // Database operation first
        if (dblLikes == null) {
            likesMapper.insert(likes);
        } else {
            likesMapper.deleteById(dblLikes.getId());
        }
        // Cache will be evicted after successful transaction
    }
    catch(Exception e){
        throw e;

    }
    }
    // @CacheEvict(value = "blog", key = "#likes.fid")
    // public void set(Likes likes) {
    //     Account currentUser = TokenUtils.getCurrentUser();
    //     likes.setUserId(currentUser.getId());
    //     Likes dblLikes = likesMapper.selectUserLikes(likes);
    //     if (dblLikes == null) {
    //         likesMapper.insert(likes);
    //     } else {
    //         likesMapper.deleteById(dblLikes.getId());
    //     }
    // }

    /**
     * 查询当前用户是否点过赞
     */
    public Likes selectUserLikes(Integer fid, String module) {
        Account currentUser = TokenUtils.getCurrentUser();
        Likes likes = new Likes();
        likes.setUserId(currentUser.getId());
        likes.setFid(fid);
        likes.setModule(module);
        return likesMapper.selectUserLikes(likes);
    }

    public int selectByFidAndModule(Integer fid, String module) {
        return likesMapper.selectByFidAndModule(fid, module);
    }

}