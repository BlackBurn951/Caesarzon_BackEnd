package org.caesar.productservice.utils;

import java.util.Base64;

public class ImageUtils {

    public static String convertByteArrayToBase64(byte[] byteArray) {
        if (byteArray == null) {
            System.out.println("Array nullo");
        }
        System.out.println("Array: " + byteArray.toString());
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static byte[] convertBase64ToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}
