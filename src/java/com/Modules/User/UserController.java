package com.Modules.User;


import com.Infrastructure.Exception.NotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserController {
    private UserService service;

    public UserController() throws SQLException, ClassNotFoundException {
        service = new UserService();
    }

    public ArrayList<User> list() throws SQLException {
        ArrayList<User> users = this.service.list();

        return users;
    }

    public User getDetail(int id) throws SQLException, NotFoundException {
        User user = this.service.getDetail(id);

        return user;
    }

    public User login(User user) throws SQLException, NotFoundException {
        User currentUser = this.service.login(user);

        return currentUser;
    }

    public User getUserByEmail(String email) throws NotFoundException, SQLException {
        User user = this.service.getUserByEmail(email);

        return user;
    }

    public User getUser() throws NotFoundException, SQLException {
        User user = this.service.getUser();

        return user;
    }

    public User create(User user) throws SQLException, NotFoundException {
        User newUser = this.service.create(user);

        return newUser;
    }

    public boolean update(User user, int id) throws SQLException {
        this.service.update(user, id);

        return true;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }

    public boolean existedUser(String username) throws SQLException {
        return this.service.existedUsername(username);
    }

    public boolean existedEmail(String email) throws SQLException {
        return this.service.existedEmail(email);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, NotFoundException {
        UserController a=new UserController();
        a.delete(60);
    }


}
