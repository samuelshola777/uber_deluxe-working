package africa.semicolon.uberdeluxe.util;

import africa.semicolon.uberdeluxe.exception.BusinessLogicException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class AppUtilities {
    public static final int NUMBER_OF_ITEMS_PER_PAGE = 3;
    private static final String USER_VERIFICATION_BASE_URL="localhost:9090/api/v1/user/account/verify";

    public static String getMailTemplate(){
        try (BufferedReader reader = new BufferedReader(new FileReader(
          "/home/semicolon/Documents/spring-projects/uberdeluxe/src/main/resources/welcome.txt"))){
            return reader.lines().collect(Collectors.joining());
        }catch (IOException exception){
            throw new BusinessLogicException(exception.getMessage());
        }
    }

    public static String getAdminMailTemplate(){
        try (BufferedReader reader = new BufferedReader(new FileReader("/home/semicolon/Documents/spring-projects/uberdeluxe/src/main/resources/adminMail.txt"))){
            return reader.lines().collect(Collectors.joining());
        }catch (IOException exception){
            throw new BusinessLogicException(exception.getMessage());
        }
    }

    public static String generateVerificationLink(Long userId){
        return USER_VERIFICATION_BASE_URL+"?userId="+userId+"&token="+generateVerificationToken();
    }

    private static String generateVerificationToken() {
        return Jwts.builder()
                .setIssuer("uber_deluxe")
                .signWith(SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="))
                .setIssuedAt(new Date())
                .compact();
    }

    public static boolean isValidToken(String token){
        return Jwts.parser()
                .isSigned(token);
    }

}
