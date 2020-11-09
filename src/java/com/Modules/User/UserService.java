package com.Modules.User;


import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.UpdatableBcrypt;
import com.Infrastructure.Base.BaseService;

import java.sql.*;
import java.util.ArrayList;

public class UserService extends BaseService {
    public UserService() throws SQLException, ClassNotFoundException {
        super();
    }



    protected String getTable() {
        return User.getTable();
    }

    public ArrayList<User> list() throws SQLException {
        ArrayList<User> users = this._list();
        this.closeStatement();

        return users;
    }

    public User getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = this._toObject(resultSet);
        this.closeStatement();

        return user;
    }


    public User login(User user) throws SQLException, NotFoundException {
        ResultSet resultSet = this.get("username = '" + user.getUsername() + "'");
        if (!resultSet.next()) {
           throw new NotFoundException();
           // System.out.println("wrong username");
        }

        String password = resultSet.getString("password");
//        if (!UpdatableBcrypt.verifyHash(user.getPassword(), password)) {
//            throw new NotFoundException();
//        }
        if (!(user.getPassword()).equalsIgnoreCase(password)){
            throw new NotFoundException();
        }


        user = this._toObject(resultSet);
        this.closeStatement();

        return user;
    }

    

    public User getUserByEmail(String email) throws SQLException, NotFoundException {
        ResultSet resultSet = this.get("email = '" + email + "'");
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = this._toObject(resultSet);
        this.closeStatement();

        return user;
    }

    public User getUser() throws SQLException, NotFoundException {
        ResultSet resultSet = this.get();
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = this._toObject(resultSet);
        this.closeStatement();

        return user;
    }

    public void getEmail(String email) throws SQLException, NotFoundException {
        ResultSet resultSet = this.get("email = '" + email + "'");
        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        User user = this._toObject(resultSet);
        email=user.getEmail();
        this.closeStatement();

    }

    public User create(User user) throws SQLException, NotFoundException {
        int id = this._create(user);

        return this.getDetail(id);
    }

    public void update(User user, int id) throws SQLException {
        this._update(user, id);
    }

    /*====================================================================================*/

    private ArrayList<User> _list() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        ResultSet resultSet = this.get();

        while (resultSet.next()) {
            users.add(this._toObject(resultSet));
        }

        return users;
    }

    public int _create(User user) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(username, email, password,created) VALUES (?, ?, ?,?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setNString(1, user.getUsername());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPassword());
        //statement.setString(3, UpdatableBcrypt.hash(user.getPassword()));
        statement.setTimestamp(4, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }


    private void _update(User user, int id) throws SQLException {
        String passwordStatement = "";
        int i = 2;

        if (!user.getPassword().equals("")) {
            passwordStatement = "password = ? ";
        }

        String statementString = "UPDATE " + getTable() + " SET username = ?, " + passwordStatement + " WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);

        if (!user.getPassword().equals("")) {
            statement.setString(i++, (user.getPassword()));
        }

        statement.setNString(1, user.getUsername());
        statement.setInt(i, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    @Override
    protected User _toObject(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));

        return user;
    }
}
