package com.spider.mapper;

import com.spider.vo.KtggVo;
import com.spider.web.entity.Ktgg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 开庭公告--Mapper
 * @Author: fk
 * @Date: 2020/2/29 13:15
 */
@Mapper
public interface KtggMapper {

    /**
     * @Author: fk
     * @Description: 查询所有数据
     * @Date: 13:32 2020/2/29
     * @Param: []
     * @return: java.util.List<com.spider.web.entity.Ktgg>
     **/
    List<Ktgg> findAll();

    /**
     * @Author: fk
     * @Description: 批量插入数据
     * @Date: 14:22 2020/2/29
     * @Param: [list]
     * @return: int
     **/
    int batchInsert(List<Ktgg> list);

    /**
     * @Author: fk
     * @Description: 根据条件查询数据
     * @Date: 14:46 2020/3/1
     * @Param: [ktggVo]
     * @return: java.util.List<com.spider.web.entity.Ktgg>
     **/
    List<Ktgg> findByConditions(KtggVo ktggVo);
}
