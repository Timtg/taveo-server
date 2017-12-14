package no.timesaver.tools;

public class StringChecker {

    public static boolean hasUpperCaseLetters(String in) {
        for(int i =0; i < in.length();i++) {
            if(Character.isUpperCase(in.charAt(i))){
                return true;
            }
        }
        return false;
    }
    public static boolean hasLowerCaseLetters(String in) {
        for(int i =0; i < in.length();i++) {
            if(Character.isLowerCase(in.charAt(i))){
                return true;
            }
        }
        return false;
    }
}
