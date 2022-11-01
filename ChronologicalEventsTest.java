package com.example.familyclient_julianasmith.ServerProxyTests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.ServerProxy;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Locale;
import java.util.Map;

import Models.Event;
import Models.Person;
import Request.RegisterRequest;
import Result.ClearResult;
import Result.EventResult;
import Result.PersonResult;
import Result.RegisterResult;

public class ChronologicalEventsTest {
    @Test
    public void successChronologically(){
        DataCache cache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy();
        String username = "GoodUsername";
        String password = "GoodPassword";
        String localHost = "localhost";
        String localPort = "8080";

        ClearResult clearResult = proxy.clear(localHost, localPort); // Clears the server to start
        assertTrue(clearResult.isSuccess());

        // Registers the good user
        RegisterRequest registerRequest = new RegisterRequest(username, password, "goodEmail", "goodFirstName", "goodLastName", "m");
        RegisterResult registerResult = proxy.register(localHost, localPort, registerRequest);
        assertTrue(registerResult.isSuccess());

        PersonResult personResult = proxy.getPeople(localHost, localPort, registerResult.getAuthToken());
        EventResult eventResult = proxy.getEvents(localHost, localPort, registerResult.getAuthToken());

        cache.setPeople(personResult);
        cache.populateEvents(eventResult);
        cache.setUserPersonID(registerResult.getPersonID());
        Person user = cache.getPersonByID(registerResult.getPersonID());

        // TEST THE USER'S EVENTS (NOT REALLY NECESSARY)
        Map<Integer,Event> sortedEvents = null;
        sortedEvents = cache.getPersonEvents(registerResult.getPersonID());
        assertNotNull(sortedEvents);
        assertEquals("birth", sortedEvents.get(0).getEventType().toLowerCase(Locale.ROOT));

        int latestYear = 0;
        for(Event event: sortedEvents.values()){
            assertTrue(event.getYear() >= latestYear); // Check if this event's year is greater than the last
            latestYear = event.getYear();
        }

        // TEST THE MOTHER'S EVENTS
        sortedEvents = cache.getPersonEvents(user.getMotherID());
        assertNotNull(sortedEvents);
        assertEquals("birth", sortedEvents.get(0).getEventType().toLowerCase(Locale.ROOT)); // Assert Birth is First
        assertEquals("death", sortedEvents.get(sortedEvents.size()-1).getEventType().toLowerCase(Locale.ROOT)); // Assert Death is Last

        latestYear = 0;
        for(Event event: sortedEvents.values()){
            assertTrue(event.getYear() >= latestYear); // Check if this event's year is greater than the last
            latestYear = event.getYear();
        }

        // TEST THE FATHER'S EVENTS
        sortedEvents = cache.getPersonEvents(user.getFatherID());
        assertNotNull(sortedEvents);
        assertEquals("birth", sortedEvents.get(0).getEventType().toLowerCase(Locale.ROOT)); // Assert birth is first
        assertEquals("death", sortedEvents.get(sortedEvents.size()-1).getEventType().toLowerCase(Locale.ROOT)); // Assert death is last

        latestYear = 0;
        for(Event event: sortedEvents.values()){
            assertTrue(event.getYear() >= latestYear); // Check if this event's year is greater than the last
            latestYear = event.getYear();
        }
    }
}
