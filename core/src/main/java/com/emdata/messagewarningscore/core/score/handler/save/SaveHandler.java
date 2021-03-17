package com.emdata.messagewarningscore.core.score.handler.save;/**
 * Created by zhangshaohu on 2021/1/12.
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emdata.messagewarningscore.common.common.utils.ClassHelper;
import com.emdata.messagewarningscore.common.dao.WarningAutoEvalInfosMapper;
import com.emdata.messagewarningscore.common.dao.WarningAutoEvalMapper;
import com.emdata.messagewarningscore.common.dao.entity.WarningAutoEvalDO;
import com.emdata.messagewarningscore.common.dao.entity.WarningAutoEvalInfosDO;
import com.emdata.messagewarningscore.core.score.handler.SaveHandlerService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: zhangshaohu
 * @date: 2021/1/12
 * @description: 插入数据处理器
 */
@Component
public class SaveHandler implements SaveHandlerService {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    private static Map<Class, Class> sourceElementDOMapperMap = new HashMap<Class, Class>() {
        {
            put(WarningAutoEvalDO.class, WarningAutoEvalMapper.class);
            put(WarningAutoEvalInfosDO.class, WarningAutoEvalInfosMapper.class);
        }
    };

    static {
        List<Class<?>> doMapperList = ClassHelper.getDOMapperList();
        Map<String, List<Class<?>>> collect = doMapperList.stream().collect(Collectors.groupingBy(s -> {
            String name = s.getName();
            String replace = name.replace("DO", "").replace("Mapper", "");
            return replace;
        }));
        collect.entrySet().stream().peek(s -> {
            List<Class<?>> value = s.getValue();
            Optional<Class<?>> mapper = value.stream().filter(v -> {
                return v.getName().endsWith("Mapper");
            }).findFirst();
            value.stream().filter(v -> {
                return v.getName().endsWith("DO");
            }).findFirst().ifPresent(Do -> {
                mapper.ifPresent(mapp -> {
                    sourceElementDOMapperMap.put(Do, mapp);
                });
            });
        });
    }

    @Override
    public <T> int insart(Class<T> clazz, T t) {
        Class<BaseMapper> aClass = sourceElementDOMapperMap.get(clazz);
        BaseMapper baseMapper = sqlSessionTemplate.getMapper(aClass);
        return baseMapper.insert(t);
    }
}