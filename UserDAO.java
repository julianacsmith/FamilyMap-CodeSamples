package DAO;

import java.sql.*;

import Models.User;

public class UserDAO {
    private Connection connection;
    /**
     * Creates a new UserDAO and takes in a new connection
     * @param connection
     */
    public UserDAO(Connection connection){
        this.connection = connection;
    }

    /**
     * Adds a new user to the table
     * @param user
     */
    public void insertUser(Models.User user) throws DataAccessException {
        //create a new row in the USer table
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Users (Username, Password, Email, FirstName, LastName, " +
                "Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting an user into the database");
        }
    }

    /**
     * Finds and returns the associated User given the user ID.
     *
     * Returns null if not found
     * @param username
     * @return User
     */
    public User findUser(String username) throws DataAccessException {
        User user;
        ResultSet rs;
        String sql = "SELECT * FROM Users WHERE username = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"));
                return user;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error while trying to find a user within the database");
        }
    }

    /**
     * Deletes a row from the User table
     *
     * Returns false if unable to delete
     */
    public void clearUser() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }
}
