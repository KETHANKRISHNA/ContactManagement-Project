package com.shadowfox.contact.service;

import com.shadowfox.contact.model.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContactManager {

    private final List<Contact> contacts;
    private final Set<String> existingPhoneNumbers;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String PHONE_REGEX = "^[+]?[0-9]{7,15}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public ContactManager() {
        this.contacts = new ArrayList<>();
        this.existingPhoneNumbers = new HashSet<>();
    }

    public boolean addContact(Contact contact) throws IllegalArgumentException {
        validateInputs(contact);
        if (existingPhoneNumbers.contains(contact.getPhoneNumber())) {
            throw new IllegalArgumentException("A contact with this phone number already exists.");
        }
        contacts.add(contact);
        existingPhoneNumbers.add(contact.getPhoneNumber());
        return true;
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    public List<Contact> searchContacts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllContacts();
        }

        String lowerCaseTerm = searchTerm.toLowerCase();
        return contacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerCaseTerm))
                .collect(Collectors.toList());
    }

    public boolean updateContact(String oldPhoneNumber, Contact newContactDetails) throws IllegalArgumentException {
        validateInputs(newContactDetails);
        int indexToUpdate = -1;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getPhoneNumber().equals(oldPhoneNumber)) {
                indexToUpdate = i;
                break;
            }
        }

        if (indexToUpdate == -1) {
            throw new IllegalArgumentException("Contact not found.");
        }
        if (!oldPhoneNumber.equals(newContactDetails.getPhoneNumber())) {
            if (existingPhoneNumbers.contains(newContactDetails.getPhoneNumber())) {
                throw new IllegalArgumentException("A contact with the new phone number already exists.");
            }
            existingPhoneNumbers.remove(oldPhoneNumber);
            existingPhoneNumbers.add(newContactDetails.getPhoneNumber());
        }
        contacts.set(indexToUpdate, newContactDetails);
        return true;
    }

    public boolean deleteContact(String phoneNumber) {
        boolean removed = contacts.removeIf(c -> c.getPhoneNumber().equals(phoneNumber));
        if (removed) {
            existingPhoneNumbers.remove(phoneNumber);
        }
        return removed;
    }

    private void validateInputs(Contact contact) throws IllegalArgumentException {
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (contact.getPhoneNumber() == null || !PHONE_PATTERN.matcher(contact.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Invalid phone number. Must contain digits only.");
        }

        if (contact.getEmailAddress() == null || !EMAIL_PATTERN.matcher(contact.getEmailAddress()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }
}