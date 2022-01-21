package com.epam.izh.rd.online.auction.mappers;

import com.epam.izh.rd.online.auction.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getLong("USER_ID"));
        user.setBillingAddress(resultSet.getString("BILLING_ADDRESS"));
        user.setFullName(resultSet.getString("FULL_NAME"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setPassword(resultSet.getString("PASSWORD"));
        return user;
    }
}
