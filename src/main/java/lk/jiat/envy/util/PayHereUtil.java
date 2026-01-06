package lk.jiat.envy.util;

import jakarta.ws.rs.core.MultivaluedMap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class PayHereUtil {

    private static final String MERCHANT_ID = "1227161"; // replace with your sandbox/live merchant ID
    private static final String MERCHANT_SECRET = "MjUyNzUxNTU2MTk0NTA2Mjk2NzM5MjQyMzQyMTQxOTI2ODU2Njg="; // exact secret
    public static final String APP_CURRENCY = "LKR";
    public static final String APP_COUNTRY = "Sri Lanka";
    public static final int PAYMENT_SUCCESS = 2;

    public static String getMerchantId(){
        return MERCHANT_ID;
    }

    public static String generateHash(String orderId, double amount){
        String formattedAmount = String.format(Locale.US, "%.2f", amount);
        String secretHash = md5(MERCHANT_SECRET).toUpperCase();
        String row = MERCHANT_ID + orderId + formattedAmount + APP_CURRENCY + secretHash;

        return md5(row).toUpperCase();
    }

    public static boolean validateNotify(MultivaluedMap<String, String> from){
        String merchantId = from.getFirst("merchant_id");
        String orderId = from.getFirst("order_id");
        String paymentAmount = from.getFirst("payment_amount");
        String paymentCurrency = from.getFirst("payment_currency");
        String statusCode = from.getFirst("status_code");
        String md5Sig =  from.getFirst("md5sig");
        String localSignature = md5(merchantId + orderId + paymentAmount + paymentCurrency + statusCode + md5(PayHereUtil.MERCHANT_SECRET).toUpperCase());

        return localSignature.equals(md5Sig);
    }

    private static String md5(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("MD5 ERROR: " + e);
        }
    }
}
