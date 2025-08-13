package com.smartitems.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.smartitems.dao.UserDao;
import com.smartitems.model.User;

import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public AuthService() {
        try {
            userDao.createTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init auth tables", e);
        }
    }

    public User register(String name, String email, String mobile, String password, String gender) throws SQLException {
        if (userDao.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setMobile(mobile);
        u.setPasswordHash(hash);
        u.setGender(gender);
        userDao.insert(u);
        return u;
    }

    public User login(String email, String password) throws SQLException {
        User u = userDao.findByEmail(email);
        if (u == null) return null;
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), u.getPasswordHash());
        return result.verified ? u : null;
    }
}



