package com.emdata.messagewarningscore.common.handler;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.emdata.messagewarningscore.common.accuracy.entity.ForcastWeather;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description:
 */
@MappedTypes(value = ForcastWeather.class)
public class ArrayHandler implements TypeHandler<List<ForcastWeather>> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, List<ForcastWeather> forcastWeathers, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(forcastWeathers));
    }

    @Override
    public List<ForcastWeather> getResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        List<ForcastWeather> forcastWeathers = JSONArray.parseArray(string, ForcastWeather.class);
        return forcastWeathers;
    }

    @Override
    public List<ForcastWeather> getResult(ResultSet resultSet, int i) throws SQLException {
        String string = resultSet.getString(i);
        List<ForcastWeather> forcastWeathers = JSONArray.parseArray(string, ForcastWeather.class);
        return forcastWeathers;
    }

    @Override
    public List<ForcastWeather> getResult(CallableStatement callableStatement, int i) throws SQLException {
        String string = callableStatement.getString(i);
        List<ForcastWeather> forcastWeathers = JSONArray.parseArray(string, ForcastWeather.class);
        return forcastWeathers;
    }
}