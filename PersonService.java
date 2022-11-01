package Service;

import DAO.AuthTokenDAO;
import DAO.Database;
import DAO.PersonDAO;
import Models.Person;
import Result.PersonResult;

import java.sql.Connection;
import java.util.List;

public class PersonService {

    /**
     * Creates a new PersonService Object
     */
    public PersonService(){
    }

    /**
     * Returns a specific person given the user's authToken and the person's ID
     * @return PersonResult
     * @throws InternalError
     */
    public PersonResult person(String authToken) throws InternalError{
        PersonResult result = null;
        Database db = new Database();
        try{
            // Open connection and make DAO's
            db.openConnection();
            Connection conn = db.getConnection();
            PersonDAO pDAO = new PersonDAO(conn);
            AuthTokenDAO atDAO = new AuthTokenDAO(conn);

            // Get a list of People
            String associatedUsername = atDAO.findAuthToken(authToken).getUsername();
            List<Person> persons = pDAO.findPersonsFromUser(associatedUsername, conn);

            // Check the size of the list
            if(persons.size() == 0){ // Nothing is there! Make a fail result
                db.closeConnection(false);
                result = new PersonResult(null, "Error: No persons in PersonDAO", false);
            } else { // Something is there! Make success result
                db.closeConnection(true);
                Person[] personsArray = persons.toArray(new Person[0]);
                result = new PersonResult(personsArray, true);
            }
        } catch (Exception e){ // Something went seriously wrong
            db.closeConnection(false);
            result = new PersonResult(null, "Error: Unable to find events successfully", false);
        }
        return result; // Return result :D
    }
}
