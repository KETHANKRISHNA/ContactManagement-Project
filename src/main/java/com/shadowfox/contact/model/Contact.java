package com.shadowfox.contact.model;

import java.util.Objects;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * A Plain Old Java Object (POJO) representing a Contact.
 * Encapsulation is maintained using private fields and public getters/setters.
 */
public class Contact {
    private String name;
    private String phoneNumber;
    private String emailAddress;

    public Contact(String name, String phoneNumber, String emailAddress) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Uniqueness is often checked against phone numbers
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Contact contact = (Contact) o;
        return Objects.equals(phoneNumber, contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

    // Added a main method for a standalone CLI Contact Manager
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Contact> contacts = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Mini Contact Manager ---");
            System.out.println("1. Add Contact (Insert)");
            System.out.println("2. View All Contacts (Traversal)");
            System.out.println("3. Delete Contact");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Contact Number: ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    contacts.add(new Contact(name, phone, email));
                    System.out.println("Contact added successfully!");
                    break;
                case "2":
                    System.out.println("\n--- Contact List ---");
                    if (contacts.isEmpty()) {
                        System.out.println("No contacts to display.");
                    } else {
                        for (Contact c : contacts) {
                            System.out.println(c.toString());
                        }
                    }
                    break;
                case "3":
                    System.out.print("Enter Phone of contact to delete: ");
                    String deletePhone = scanner.nextLine();
                    boolean removed = contacts.removeIf(c -> c.getPhoneNumber().equals(deletePhone));
                    if (removed) {
                        System.out.println("Contact deleted successfully.");
                    } else {
                        System.out.println("Contact not found.");
                    }
                    break;
                case "4":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
