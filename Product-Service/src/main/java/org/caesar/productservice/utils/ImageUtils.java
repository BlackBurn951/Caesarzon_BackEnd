package org.caesar.productservice.utils;
import org.springframework.web.multipart.support.StringMultipartFileEditor;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

public class ImageUtils {

    public static String convertByteArrayToBase64(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static byte[] convertBase64ToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

}
