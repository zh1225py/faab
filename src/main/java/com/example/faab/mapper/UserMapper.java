package com.example.faab.mapper;

import com.example.faab.domain.UserVO;
import com.example.faab.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 *
 */
public interface UserMapper extends BaseMapper<User> {
    public List<UserVO> getAllUsers();

    public UserVO loginUser(@Param("username")String username, @Param("password")String password);

    public UserVO getOneUser(String username);
}
