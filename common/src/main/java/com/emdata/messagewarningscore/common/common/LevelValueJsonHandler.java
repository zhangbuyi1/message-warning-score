package com.emdata.messagewarningscore.common.common;

import com.alibaba.fastjson.JSONObject;
import com.emdata.messagewarningscore.common.dao.entity.ImportantWeatherDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhangshaohu on 2020/3/5.
 */
@Slf4j
@MappedTypes(ImportantWeatherDO.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
@Component
public class LevelValueJsonHandler extends BaseTypeHandler<List<ImportantWeatherDO>> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<ImportantWeatherDO> parameter, JdbcType jdbcType) throws SQLException {
//        log.info("当前对象:{}",parameter);
        if (parameter.size() != 0) {
            ps.setString(i, String.valueOf(JSONObject.toJSON(parameter)));
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public List<ImportantWeatherDO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlJson = rs.getString(columnName);
        if (null != sqlJson) {
            return JSONObject.parseArray(sqlJson, ImportantWeatherDO.class);
        }
        return null;
    }

    @Override
    public List<ImportantWeatherDO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlJson = rs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONObject.parseArray(sqlJson, ImportantWeatherDO.class);
        }
        return null;
    }

    @Override
    public List<ImportantWeatherDO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlJson = cs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONObject.parseArray(sqlJson, ImportantWeatherDO.class);
        }
        return null;
    }
}
