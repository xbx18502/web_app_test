package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import com.example.common.enums.LikesModuleEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.BlogMapper;
import com.example.mapper.CollectMapper;
import com.example.repository.BlogRepository;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 博客信息业务处理
 **/
@Service
@Slf4j
public class BlogService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    UserService userService;

    @Resource
    LikesService likesService;

    @Resource
    CollectService collectService;

    @Value("${ip:216.238.80.124}")
    private String ip;

    

    // @Autowired
    // private BlogRepository blogRepository;

    /**
     * 新增
     */
    // @CacheEvict(value = "blog", allEntries = true)    // 清除缓存
    public void add(Blog blog) {
        blog.setDate(DateUtil.today());
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            blog.setUserId(currentUser.getId());
        }
        blogMapper.insert(blog);
    }

    /**
     * 删除
     */
    @CacheEvict(value = "blog", key = "#id")    // 清除缓存
    public void deleteById(Integer id) {
        blogMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            blogMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    @CachePut(value = "blog", key = "#blog.id")    // 清除缓存
    public void updateById(Blog blog) {
        blogMapper.updateById(blog);
    }

    //@Cacheable(value = "blog_basic", key = "#id")
    public Blog getBasicBlog(Integer id) {
        return blogMapper.selectById(id);
    }

    //@Cacheable(value = "blog_likes", key = "#id")
    public int getLikesCount(Integer id) {
        return likesService.selectByFidAndModule(id, LikesModuleEnum.BLOG.getValue());
    }
    
    //@Cacheable(value = "blog_collects", key = "#id")
    public int getCollectCount(Integer id) {
        return collectService.selectByFidAndModule(id, LikesModuleEnum.BLOG.getValue());
    }
    /**
     * 根据ID查询
     */
    @Cacheable(value = "blog", key = "#id", unless = "#result == null")
    public Blog selectById(Integer id) {
        Blog blog = getBasicBlog(id);
        if (blog == null) return null;
        
        User user = userService.selectById(blog.getUserId());
        blog.setUser(user);
        
        blog.setLikesCount(getLikesCount(id));
        Likes userLikes = likesService.selectUserLikes(id, LikesModuleEnum.BLOG.getValue());
        blog.setUserLike(userLikes != null);
        
        blog.setCollectCount(getCollectCount(id));
        Collect userCollect = collectService.selectUserCollect(id, LikesModuleEnum.BLOG.getValue());
        blog.setUserCollect(userCollect != null);
        
        return blog;
    }

    /**
     * 查询所有
     */
    @Cacheable(value = "blog_all", key = "#root.methodName")
    public List<Blog> selectAll(Blog blog) {
        return blogMapper.selectAll(blog);
    }

    /**
     * 分页查询
     */
    public PageInfo<Blog> selectPage(Blog blog, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectAll(blog);
        for (Blog b : list) {
            int likesCount = likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.BLOG.getValue());
            b.setLikesCount(likesCount);
                    // 替换 'localhost' 为配置的 ip
            String cover = b.getCover();
            if (cover != null && cover.contains("localhost")) {
                cover = cover.replace("localhost", ip);
                b.setCover(cover);
            }
        }
        return PageInfo.of(list);
    }

    /**
     * 博客榜单
     */
    public List<Blog> selectTop() {
        List<Blog> bloglist = this.selectAll(null);
        bloglist = bloglist.stream().sorted((b1, b2) -> b2.getReadCount()
                .compareTo(b1.getReadCount())).limit(10).collect(Collectors.toList());
        return bloglist;
    }

    public Set<Blog> selectRecommend(Integer blogId) {
        Blog blog = this.selectById(blogId);
        String tags = blog.getTags();
        Set<Blog> blogSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(tags)) {
            List<Blog> blogList = this.selectAll(null);
            JSONArray tagsArr = JSONUtil.parseArray(tags);
            for (Object tag : tagsArr) {
                String tagStr = tag.toString();
                // 筛选出包含当前博客标签的其他博客的博客列表
                // blogSet.addAll(blogList.stream().filter(b -> b.getTags()
                // .contains(tagStr)&& !blogId.equals(b.getId())).collect(Collectors.toSet()));
                // 添加null检查
                blogSet.addAll(blogList.stream()
                        .filter(b -> b != null && b.getTags() != null) // 添加空值检查
                        .filter(b -> b.getTags().contains(tagStr) && !blogId.equals(b.getId()))
                        .collect(Collectors.toSet()));
            }
        }
        blogSet = blogSet.stream().limit(5).collect(Collectors.toSet());
        blogSet.forEach(b -> {
            int likesCount = likesService.selectByFidAndModule(b.getId(), LikesModuleEnum.BLOG.getValue());
            b.setLikesCount(likesCount);
        });
        System.out.println("Recommend blogs for blogId " + blogId + ": " + JSONUtil.toJsonStr(blogSet));
        return blogSet;
    }
}
