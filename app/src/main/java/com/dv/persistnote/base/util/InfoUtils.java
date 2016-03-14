package com.dv.persistnote.base.util;

import java.util.regex.Pattern;

public class InfoUtils {
    /**
     * Email format validation
     * @param email address
     * @return if valid return true，else return false
     */
    public static boolean isEmailValid(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }

    /**
     * Mobile validation(International:+86135xxxx...Mainland China, +00852137xxxx...(HongKong))
     * @param mobile Verify mobile number from China Mobile, China Unicom and China Telecom.
     *<p>China Mobile：134(0-8)、135、136、137、138、139、147
     *、150、151、152、157、158、159、187、188</p>
     *<p>China Unicom：130、131、132、155、156、185、186(3g)</p>
     *<p>China Telecom：133、153、180、189</p>
     * @return if valid return true，else return false
     */
    public static boolean isMobileValid(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }

    /**
     * ID card validation
     * @param idCard Resident ID card has 15 or 18 digit length. The last digit maybe a character.
     * @return if valid return true，else return false
     */
    public static boolean isIdCardValid(String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex,idCard);
    }

    /**
     * Check landline number.
     * @param phone format: country code + city code + phone number like +8601088888888 or 01066666666, 6666666, 88888888
     * @return if valid return true，else return false
     */
    public static boolean isPhoneNumberValid(String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * Check integer.
     * @param digit like 11, -20
     * @return if valid return true，else return false
     */
    public static boolean isDigitValid(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return Pattern.matches(regex,digit);
    }

    /**
     * Check integer and float(including positive and negative one)
     * @param decimals like 1.23,123.45,100
     * @return if valid return true，else return false
     */
    public static boolean isDecimalsValid(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return Pattern.matches(regex,decimals);
    }

    /**
     * Check space
     * @param blankSpace blank space including: space,\t,\n,\r,\f,\x0B
     * @return if valid return true，else return false
     */
    public static boolean isBlankSpaceValid(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex,blankSpace);
    }

    /**
     * Chinese validation.
     * @param chinese Chinese character
     * @return if valid return true，else return false
     */
    public static boolean isChineseValid(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex,chinese);
    }

    /**
     * Check Date.
     * @param birthday format like 1995-06-01 or 1995.11.13
     * @return if valid return true，else return false
     */
    public static boolean isBirthdayValid(String birthday) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex,birthday);
    }

    /**
     * Url address validation
     * @param url format: http://www.xsysigma.com
     * @return if valid return true，else return false
     */
    public static boolean isUrlValid(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * China Post Code validation
     * @param postcode Post Code
     * @return if valid return true，else return false
     */
    public static boolean isPostcodeValid(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }

    /**
     * Match IP address(check format like：192.168.1.1，127.0.0.1)
     * @param ipAddress IPv4 address
     * @return if valid return true，else return false
     */
    public static boolean isIpAddressValid(String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }

}
