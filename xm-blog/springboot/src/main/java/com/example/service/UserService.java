package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public void add(User user) {
        //1. username conflict?
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if(dbUser!=null){
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        //2. password empty?
        if(ObjectUtil.isEmpty(user.getPassword())){
            user.setPassword(Constants.USER_DEFAULT_PASSWORD); //default 123
        }
        //3. username empty?
        if(ObjectUtil.isEmpty(user.getName())){
            user.setName(user.getUsername());
        }
        //4. default user role
        user.setRole(RoleEnum.USER.name());
        userMapper.insert(user);
    }


    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    public void updateById(User user) {
        userMapper.updateById(user);
    }
}