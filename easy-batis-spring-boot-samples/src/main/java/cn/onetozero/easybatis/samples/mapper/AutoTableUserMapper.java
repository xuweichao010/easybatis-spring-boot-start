package cn.onetozero.easybatis.samples.mapper;

import cn.onetozero.easybatis.BaseMapper;
import cn.onetozero.easybatis.samples.entity.AutoTableUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 类描述：
 * 作者：徐卫超 (cc)
 * 时间 2023/3/24 10:43
 */
@Mapper
public interface AutoTableUserMapper extends BaseMapper<AutoTableUser, String> {
}
