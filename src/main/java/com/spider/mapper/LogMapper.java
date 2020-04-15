package com.spider.mapper;

import com.spider.web.entity.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description 日志--Mapper
 * @Author: fk
 * @Date: 2020/2/29 13:15
 */
@Mapper
public interface LogMapper {

    /**
     * @Author: fk
     * @Description: 查询所有数据
     * @Date: 13:32 2020/2/29
     * @Param: []
     * @return: java.util.List<com.spider.web.entity.Ktgg>
     **/
    List<Log> findAll();

    /**
     * @Author: fk
     * @Description: 插入单条数据
     * @Date: 14:22 2020/2/29
     * @Param: [data]
     * @return: java.lang.String：主键
     **/
    String insertData(Log data);

    /**
     * @Author: fk
     * @Description: 批量插入数据
     * @Date: 14:22 2020/2/29
     * @Param: [list]
     * @return: int
     **/
    int batchInsert(List<Log> list);

}
