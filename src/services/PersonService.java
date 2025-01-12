package services;

import models.Person;

public class PersonService {
    private Person person;

    public PersonService(Person person) {
        this.person = person;
    }

    // Authenticate user
    public void authenticate(String username, String password) {
        if (!person.checkAuthentication(username, password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        System.out.println("Authentication successful!");
    }

    // Change password
    public void changePassword(String currentPassword, String newPassword) {
        if (!person.changePassword(currentPassword, newPassword)) {
            throw new AuthenticationException("Password change failed. Current password is incorrect.");
        }
    }

    // Forgot password flow
    public void forgotPassword() {
        if (!person.forgotPassword()) {
            throw new SecurityAnswerException("Invalid security answer provided.");
        }
    }

    public String getUsername() {
        return person.getUsername();
    }

    public String getName() {
        return person.getName();
    }

}
