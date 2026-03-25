package com.shadowfox.contact.util;

import com.shadowfox.contact.model.Contact;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for Tier 2: VCard Export/Import.
 */
public class VCardUtil {

    /**
     * Exports a Contact to standard .vcf file.
     * Pitch for file parsing strategy: 
     * We follow the standard RFC 6350 (vCard 4.0) roughly, but simplified for our properties.
     * We serialize the Contact POJO properties into line-delimited key:value pairs.
     */
    public static void exportToVCard(Contact contact, File directory) throws IOException {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Safe filename replacing spaces and special characters
        String fileName = contact.getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".vcf";
        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("BEGIN:VCARD\n");
            writer.write("VERSION:4.0\n");
            writer.write("FN:" + contact.getName() + "\n");
            writer.write("TEL:" + contact.getPhoneNumber() + "\n");
            writer.write("EMAIL:" + contact.getEmailAddress() + "\n");
            writer.write("END:VCARD\n");
        }
    }

    /**
     * Imports a Contact from a .vcf file.
     * Line-by-line reading to extract FN, TEL, and EMAIL.
     */
    public static Contact importFromVCard(File file) throws IOException {
        String name = "";
        String phone = "";
        String email = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("FN:")) {
                    name = line.substring(3).trim();
                } else if (line.startsWith("TEL:")) {
                    phone = line.substring(4).trim();
                } else if (line.startsWith("EMAIL:")) {
                    email = line.substring(6).trim();
                }
            }
        }

        if (name.isEmpty() || phone.isEmpty()) {
            throw new IOException("Invalid VCard format: Missing Name or Phone number.");
        }

        return new Contact(name, phone, email);
    }
}
