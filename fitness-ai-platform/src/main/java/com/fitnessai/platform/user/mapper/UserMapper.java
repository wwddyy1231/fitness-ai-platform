package com.fitnessai.platform.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitnessai.platform.user.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("""
            select r.code from sys_role r
            join sys_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId} and r.deleted = 0
            """)
    List<String> selectRoleCodes(@Param("userId") Long userId);
}
