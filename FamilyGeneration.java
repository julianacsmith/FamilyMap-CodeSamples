package FamilyGeneration;

import DAO.DataAccessException;
import DAO.*;
import Models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

public class FamilyGeneration {
    private int numOfEvents; // Keeps track of the number of events being generated
    private int numOfPeople; // Keeps track of the number of people being generated

    public FamilyGeneration(){
        numOfEvents = 1;
        numOfPeople = 1;
    }

    /**
     * Generates a specified number of generations in a family tree for a user
     * @param currPerson
     * @param generations
     * @param birthYear
     * @param conn
     * @throws DataAccessException
     */

    public void generatePerson2(Person currPerson, int generations, int birthYear, Connection conn) throws DataAccessException {
        PersonDAO pDAO = new PersonDAO(conn);
        EventDAO eDAO = new EventDAO(conn);

        //Recurse on mother and father
        if(generations > 0) {
            //Create mom and dad
            String[] momName = getRandomName("f");
            String[] dadName = getRandomName("m");

            Person mom = new Person(UUID.randomUUID().toString(), currPerson.getAssociatedUsername(), momName[0], momName[1], "f");
            Person dad = new Person(UUID.randomUUID().toString(), currPerson.getAssociatedUsername(), dadName[0], dadName[1], "m");

            //Create events
            //Add marriage events to both. Their marriage events must be in sync with each other
            Location destination = getRandomLocation();
            Event motherMarriage = new Event(UUID.randomUUID().toString(), currPerson.getAssociatedUsername(), mom.getPersonID(), destination.getLatitude(), destination.getLongitude(), destination.getCountry(), destination.getCity(), "Marriage", birthYear-5);
            Event fatherMarriage = new Event(UUID.randomUUID().toString(), currPerson.getAssociatedUsername(), dad.getPersonID(), destination.getLatitude(), destination.getLongitude(), destination.getCountry(), destination.getCity(), "Marriage", birthYear-5);
            eDAO.insertEvent(motherMarriage);
            eDAO.insertEvent(fatherMarriage);
            numOfEvents+=2;

            //Adds birth and death events for mom and dad
            addEvents(mom, mom.getAssociatedUsername(), birthYear, eDAO);
            addEvents(dad, dad.getAssociatedUsername(), birthYear, eDAO);

            //Create spouseID's
            mom.setSpouseID(dad.getPersonID());
            dad.setSpouseID(mom.getPersonID());
            //set father and motherID of currPerson
            currPerson.setMotherID(mom.getPersonID());
            currPerson.setFatherID(dad.getPersonID());

            generatePerson2(mom, generations - 1, birthYear - 30, conn);
            generatePerson2(dad, generations - 1, birthYear - 30, conn);
            //Add mom and dad to the personDAO
            pDAO.insertPerson(mom);
            pDAO.insertPerson(dad);
            numOfPeople+=2;
        }
    }

    public int getNumOfEvents() {
        return numOfEvents;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    private String[] getFemaleNames() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Name fnames = gson.fromJson(new FileReader("json/fnames.json"), Name.class);
        return fnames.getData();
    }

    private String[] getMaleNames() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Name mnames = gson.fromJson(new FileReader("json/mnames.json"), Name.class);
        return mnames.getData();
    }

    private String[] getSurnames() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Name snames = gson.fromJson(new FileReader("json/snames.json"), Name.class);
        return snames.getData();
    }

    private Location[] getLocations() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Locations locations = gson.fromJson(new FileReader("json/locations.json"), Locations.class);
        return locations.getData();
    }

    private String[] getRandomName(String gender){
        Random rand = new Random();

        String[] name = new String[2];
        String[] firstNames;
        String[] surnames;
        try {
            if (gender.equalsIgnoreCase("f")) {
                firstNames = getFemaleNames();
            } else {
                firstNames = getMaleNames();
            }
            surnames = getSurnames();
            name[0] = firstNames[rand.nextInt(firstNames.length)];
            name[1] = surnames[rand.nextInt(surnames.length)];
        } catch (IOException e){
            e.printStackTrace();
        }
        return name;
    }

    public Location getRandomLocation(){
        Random rand = new Random();
        Location[] locations;
        Location destination = null;
        try{
            locations = getLocations();
            destination = locations[rand.nextInt(locations.length)];
        } catch (IOException e){
            e.printStackTrace();
        }
        return destination;
    }

    private void addEvents(Person person, String associatedUsername, int birthYear, EventDAO eDAO){
        try{
            //Add birth event
            Location birthDestination = getRandomLocation();
            Event personBirth = new Event(UUID.randomUUID().toString(), associatedUsername, person.getPersonID(), birthDestination.getLatitude(), birthDestination.getLongitude(), birthDestination.getCountry(), birthDestination.getCity(), "birth", birthYear-30);
            eDAO.insertEvent(personBirth);
            numOfEvents ++;

            //Add death event
            Location deathDestination = getRandomLocation();
            Event personDeath = new Event(UUID.randomUUID().toString(), associatedUsername, person.getPersonID(), deathDestination.getLatitude(), deathDestination.getLongitude(), deathDestination.getCountry(), deathDestination.getCity(), "death", (birthYear-30)+80);
            eDAO.insertEvent(personDeath);
            numOfEvents++;
        } catch (DataAccessException e){
            e.printStackTrace();
        }
    }

    public void reset(){
        numOfEvents = 1;
        numOfPeople = 1;
    }
}
