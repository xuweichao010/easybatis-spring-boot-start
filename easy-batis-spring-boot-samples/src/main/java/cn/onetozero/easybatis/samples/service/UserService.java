package cn.onetozero.easybatis.samples.service;

import cn.onetozero.easybatis.samples.entity.User;
import cn.onetozero.easybatis.samples.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类描述：
 * @author  徐卫超 (cc)
 * @since 2023/2/10 16:38
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;


    public void init() {
        User one = userMapper.findOne("123123");
        System.out.println(one);
    }
}
