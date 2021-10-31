package utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

// Fix ClassNotFoundError
public class JapaneseConverter {

    public JapaneseConverter() {}

    public String encodeJapanese(String word) {
        byte[] x = word.getBytes(StandardCharsets.UTF_8);
        return new String(x, StandardCharsets.ISO_8859_1);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        JapaneseConverter converter = new JapaneseConverter();
        System.out.println(converter.encodeJapanese("たびに")) ;
    }

}
