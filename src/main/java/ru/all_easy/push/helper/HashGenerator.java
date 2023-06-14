package ru.all_easy.push.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

@Component
public class HashGenerator {

    private final DateTimeHelper dateTimeHelper;

    public HashGenerator(DateTimeHelper dateTimeHelper) {
        this.dateTimeHelper = dateTimeHelper;
    }

    public String generate() throws NoSuchAlgorithmException {
        return generate(dateTimeHelper.getCurrentTimestamp());
    }

    public String generate(String time) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashString = digest.digest(time.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hashString);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
