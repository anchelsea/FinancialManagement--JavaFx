package com.Infrastructure.Handle;

import java.util.regex.Pattern;

public class HandleError {

    public static boolean isValidUsername(String username){
        String regex = "^[0-9a-zA-Z_].{3,}$";

        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(username).matches();
    }

    public static boolean isValidPassword(String pass){

        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";


        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(pass).matches();

    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
