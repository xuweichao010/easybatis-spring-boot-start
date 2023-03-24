package cn.onetozero.easybatis.test;

import cn.onetozero.easybatis.samples.EasyBatisApplication;
import cn.onetozero.easybatis.samples.entity.User;
import cn.onetozero.easybatis.samples.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 类描述：
 * 作者：徐卫超 (cc)
 * 时间 2023/3/24 10:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EasyBatisApplication.class})
public class RunnerTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void config() {
        User one = userMapper.findOne("1L");

    }
}
