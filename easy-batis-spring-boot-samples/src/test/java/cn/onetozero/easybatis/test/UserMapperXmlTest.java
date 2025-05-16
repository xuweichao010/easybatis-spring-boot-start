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
 * @author  徐卫超 (cc)
 * @since 2023/3/29 13:48
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EasyBatisApplication.class})
public class UserMapperXmlTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void xmlApply() {
        User oneXml = userMapper.findOneXml("1");
        System.out.println(oneXml);
    }
}
