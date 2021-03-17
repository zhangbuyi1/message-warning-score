package com.emdata.messagewarningscore.common.handler;/**
 * Created by zhangshaohu on 2021/1/4.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emdata.messagewarningscore.common.accuracy.entity.MainWeather;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: zhangshaohu
 * @date: 2021/1/4
 * @description:
 */
@MappedTypes(MainWeather.class)
public class MainWeatherHandler implements TypeHandler<MainWeather> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, MainWeather mainWeather, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSON.toJSONString(mainWeather));
    }

    @Override
    public MainWeather getResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        return JSONObject.parseObject(string, MainWeather.class);
    }

    @Override
    public MainWeather getResult(ResultSet resultSet, int i) throws SQLException {
        String string = resultSet.getString(i);
        return JSONObject.parseObject(string, MainWeather.class);
    }

    @Override
    public MainWeather getResult(CallableStatement callableStatement, int i) throws SQLException {
        String string = callableStatement.getString(i);
        return JSONObject.parseObject(string, MainWeather.class);
    }
}