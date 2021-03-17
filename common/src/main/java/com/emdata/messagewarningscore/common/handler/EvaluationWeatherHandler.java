package com.emdata.messagewarningscore.common.handler;/**
 * Created by zhangshaohu on 2020/12/31.
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.emdata.messagewarningscore.common.accuracy.enums.EvaluationWeather;
import com.emdata.messagewarningscore.common.dao.entity.WeatherAutoRecordDO;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: zhangshaohu
 * @date: 2020/12/31
 * @description:
 */
@MappedTypes(value = EvaluationWeather.class)
public class EvaluationWeatherHandler implements TypeHandler<EvaluationWeather> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, EvaluationWeather evaluationWeather, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, evaluationWeather.getCode());
    }

    @Override
    public EvaluationWeather getResult(ResultSet resultSet, String s) throws SQLException {
        int anInt = resultSet.getInt(s);
        return EvaluationWeather.build(anInt);
    }

    @Override
    public EvaluationWeather getResult(ResultSet resultSet, int i) throws SQLException {
        int anInt = resultSet.getInt(i);
        return EvaluationWeather.build(anInt);

    }

    @Override
    public EvaluationWeather getResult(CallableStatement callableStatement, int i) throws SQLException {
        int anInt = callableStatement.getInt(i);
        return EvaluationWeather.build(anInt);
    }
}