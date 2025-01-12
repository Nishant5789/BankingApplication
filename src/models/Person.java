package models;

import java.util.Scanner;

public class Person {
    protected String name;
    protected String id;
    protected String username;
    private String password; // Storing password as String
    private String[] securityQuestions = {
            "What's your first watch movie?",
            "Who's your favorite sportsperson?",
            "What's your hobby?"
    };
    private int securityQuestionIndex; // Stores the chosen question index
    private String securityAnswer; // Stores the answer (lowercased for case-insensitivity)

    public Person(String name, String id, String username, String password, int securityQuestionIndex, String securityAnswer) {
        this.name = name;
        this.id = id;
        this.username = username;
        this.password = password; // Storing password as String
        this.securityQuestionIndex = securityQuestionIndex;
        this.securityAnswer = securityAnswer.toLowerCase().trim(); // Normalize the answer for comparison
    }

    public boolean checkAuthentication(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (this.password.equals(currentPassword)) {
            this.password = newPassword;
//            System.out.println("Password successfully updated!");
            return true;
        }
//        System.out.println("Incorrect current password. Password change failed.");
        return false;
    }

    public boolean forgotPassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Answer the following security question:");
        System.out.println(securityQuestions[securityQuestionIndex]);

        System.out.print("Your Answer: ");
        String answer = scanner.nextLine().toLowerCase().trim(); // Normalize the input

        if (securityAnswer.equals(answer)) {
//            System.out.print("Answer correct. Enter your new password: ");
            this.password = scanner.nextLine();
//            System.out.println("Password successfully reset!");
            return true;
        } else {
//            System.out.println("Incorrect answer. Cannot reset password.");
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}