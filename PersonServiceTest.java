package passoff.DAOandService.ServiceTesters;

import DAO.*;
import Models.AuthToken;
import Models.Person;
import Models.User;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;
import Service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {
    private User user;
    private User user2;
    private Person p1;
    private Person p2;
    private AuthToken at1;
    private AuthTokenDAO atDAO;
    private PersonDAO pDAO;
    private PersonService service;
    private Database db;


    @BeforeEach
    @DisplayName("Setting up the userDao")
    void setUp() throws DataAccessException {
            db = new Database();
            db.openConnection();
            Connection connection = db.getConnection();
            user = new User("Username", "password", "email", "firstName", "lastName", "f", "PersonID");
            user2 = new User("CompletelyDifferentPerson", "password", "email", "firstName", "lastName", "f", "PersonID");
            p1 = new Person("PersonID", "Username", "FirstName", "LastName", "f");
            p2 = new Person("PersonID2", "Username", "FirstName", "LastName", "f", "FatherID", "MotherID", "SpouseID");
            at1 = new AuthToken("AuthTokenForUser2", "CompletelyDifferentPerson");
            service = new PersonService();
            atDAO = new AuthTokenDAO(connection);
            pDAO = new PersonDAO(connection);
            (new ClearService()).clear();
    }

    @AfterEach
    void tearDown() {
        db.closeConnection(false);
        (new ClearService()).clear();
    }

    @Test
    @DisplayName("Person Success!")
    public void personSuccess() throws DataAccessException{
        try {
            db.closeConnection(true);
            RegisterResult rResult = (new RegisterService()).register(new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getGender()));
            assertTrue(rResult.isSuccess());
            LoginResult lResult = (new LoginService()).login(new LoginRequest(user.getUsername(), user.getPassword()));
            db.openConnection();
            pDAO = new PersonDAO(db.getConnection());
            pDAO.insertPerson(p1);
            pDAO.insertPerson(p2);
            db.closeConnection(true);
            PersonResult pResult = service.person(lResult.getAuthToken());

            db.openConnection();
            assertNotNull(pResult.getData());
            assertNull(pResult.getMessage());
            assertTrue(pResult.isSuccess());
        } catch (Exception e){
            e.printStackTrace();
            fail("Error thrown while registering");
        }
    }

    @Test
    @DisplayName("Person Fail!")
    public void personFail() throws DataAccessException{
        try {
            pDAO.insertPerson(p1);
            pDAO.insertPerson(p2);
            atDAO.insertToken(at1);
            db.closeConnection(true);
            PersonResult eResult = service.person(at1.getAuthToken());

            db.openConnection();
            assertNull(eResult.getData());
            assertNotNull(eResult.getMessage());
            assertFalse(eResult.isSuccess());
        } catch (Exception e){
            e.printStackTrace();
            fail("Error thrown while registering");
        }
    }
}
