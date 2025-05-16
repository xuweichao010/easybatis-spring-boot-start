package cn.onetozero.easybatis.samples.entity;

import cn.onetozero.easy.annotations.models.Id;
import cn.onetozero.easy.annotations.models.Table;
import cn.onetozero.easy.annotations.enums.IdType;
import lombok.Data;

/**
 * 类描述：
 * @author  徐卫超 (cc)
 * @since 2023/3/24 10:42
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
