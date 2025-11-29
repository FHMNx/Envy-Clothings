package lk.jiat.envy.validation;

public class Validator {
    public static final String EMAIL_VALIDATION = "^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String PASSWORD_VALIDATION = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
    public static final String MOBILE_VALIDATION = "^(0{1})(7{1})([0|1|2|4|5|6|7|8]{1})([0-9]{7})";
    public static final String VERIFICATION_CODE_VALIDATION = "\\D{6}";
}
