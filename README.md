# FamilyMap-CodeSamples
Here are a couple of files from my Family Map Android App. This app was designed to register new users and display their family tree and life events on a Google Map. You can naviagte through the app, filter based on gender or types of relationships, as well as gather more information when selecting certain locations. These files include User Interface, Back-End, SQLite Database Code, and Unit Tests 

### MapFragment.java

MapFragment.java is a file from the front-end of my code. The front-end was developed in Android Studio using Java and XML. The OnCreateView Method is where the Google Map is loaded into the app and all of the markers are drawn at their specified locations. For every event with a valid eventID, I get the event, person, and person associated with those events and display the marker and set up a listerner that displays related events, connecting lines, and additional information about the event.

### PersonService.java

PersonService.java interacts with the SQLite database. PersonService checks if the database has a list of people for a given and returns a success result if there's a list with people found

### UserDAO.java

UserDAO.java is the inserting, finding, and deleting of a user from the SQLIte Database

### FamilyGeneration.java

FamilyGeneration.java is where I generated family trees for a given registered user. It uses recursion to populate parents for each geneerated person until the specifies generations is reached. This file also adds events for each person generated. Marriage is dealt with in generatePerson2 while birth and death dates are handled in addEvents.

### ChronologicalEventsTest.java and PersonServiceTest.java

These are some examples of my unit tests used throughout the project. ChronologicalEventsTest.java is a front-end test that makes sure my events are organized in chronological order (which is important when displaying a person's information). PersonServiceTest.java tests whether or not the Person Service is correctly identifying a list of people. 
