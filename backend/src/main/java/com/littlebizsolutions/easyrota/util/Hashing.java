// src/main/java/.../util/Hashing.java
package com.littlebizsolutions.easyrota.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Hashing {
    private Hashing() {}
    public static String sha256Hex(String input) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder(bytes.length*2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
