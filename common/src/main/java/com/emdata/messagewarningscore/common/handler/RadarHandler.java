package com.emdata.messagewarningscore.common.handler;/**
 * Created by zhangshaohu on 2021/1/15.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emdata.messagewarningscore.common.dao.entity.RadarEchoDO;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/15
 * @description: 雷达数据处理器
 */
public class RadarHandler extends BaseTypeHandler<List<RadarEchoDO>> {


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<RadarEchoDO> radarEchoBOS, JdbcType jdbcType) throws SQLException {
        String string = JSON.toJSONString(radarEchoBOS);
        preparedStatement.setString(i, string);
    }

    @Override
    public List<RadarEchoDO> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        List<RadarEchoDO> radarEchoDOS = JSONObject.parseArray(string, RadarEchoDO.class);
        return radarEchoDOS;
    }

    @Override
    public List<RadarEchoDO> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String string = resultSet.getString(i);
        List<RadarEchoDO> radarEchoDOS = JSONObject.parseArray(string, RadarEchoDO.class);
        return radarEchoDOS;
    }

    @Override
    public List<RadarEchoDO> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String string = callableStatement.getString(i);
        List<RadarEchoDO> radarEchoDOS = JSONObject.parseArray(string, RadarEchoDO.class);
        return radarEchoDOS;
    }
}