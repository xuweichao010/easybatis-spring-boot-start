package cn.onetozero.easybatis.samples.entity;

import cn.onetozero.easy.parse.annotations.Id;
import cn.onetozero.easy.parse.annotations.Table;
import cn.onetozero.easy.parse.enums.IdType;
import lombok.Data;

/**
 * 类描述：
 * 作者：徐卫超 (cc)
 * 时间 2023/3/24 10:42
 */
@Data
@Table("t_user")
public class AutoTableUser {

    /**
     * 用户id
     */
    @Id(type = IdType.INPUT)
    private String id;

    /**
     * 机构编码
     */
    private String orgCode;
}
