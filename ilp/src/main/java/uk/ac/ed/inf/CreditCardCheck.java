package uk.ac.ed.inf;

/**
 * Credit card number validity check using Luhn Algorithm
 * Website: https://www.tutorialspoint.com/java-program-for-credit-card-number-validation#
 */
public class CreditCardCheck {
    /**
     * Check the validity of a card number
     * @param cnumber the input card number
     * @return Return true if the card number is valid. In PizzaDronz, we assume the number of digits can only be 16.
     */
    // Return true if the card number is valid. In PizzaDronz, we assume the number of digits can only be 16.
    public static boolean validitychk(long cnumber) {
        return (thesize(cnumber) == 16) && (multiPrefixMatch(cnumber, "Visa-16", 4) ||
                multiPrefixMatch(cnumber, "MC", 2) || multiPrefixMatch(cnumber, "MC", 5))
                && ((sumdoubleeven(cnumber) + sumodd(cnumber)) % 10 == 0);
    }

    /**
     * Sum the even digits
     * @param cnumber card number
     * @return the summation of even digits
     */
    // Get the result from Step 2
    public static int sumdoubleeven(long cnumber) {
        int sum = 0;
        String num = cnumber + "";
        for (int i = thesize(cnumber) - 2; i >= 0; i -= 2)
            sum += getDigit(Integer.parseInt(num.charAt(i) + "") * 2);
        return sum;
    }

    /**
     * Get each digit for a card number
     * @param cnumber card number
     * @return this cnumber if it is a single digit, otherwise, return the sum of the two digits
     */
    // Return this cnumber if it is a single digit, otherwise,
    // return the sum of the two digits
    public static int getDigit(int cnumber) {
        if (cnumber < 9)
            return cnumber;
        return cnumber / 10 + cnumber % 10;
    }

    /**
     * Sum the odd digits
     * @param cnumber card number
     * @return sum of odd-place digits in cnumber
     */
    // Return sum of odd-place digits in cnumber
    public static int sumodd(long cnumber) {
        int sum = 0;
        String num = cnumber + "";
        for (int i = thesize(cnumber) - 1; i >= 0; i -= 2)
            sum += Integer.parseInt(num.charAt(i) + "");
        return sum;
    }

    // Return true if the digit d is a prefix for cnumber
    public static boolean prefixmatch(long cnumber, int d) {
        return getprefx(cnumber, thesize(d)) == d;
    }

    /**
     * Match the card number according to the given card type
     * @param cnumber card number
     * @param cardType card type
     * @param d the correct first digit of the given card type
     * @return true if match correctly
     */
    public static boolean multiPrefixMatch(long cnumber, String cardType, int d) {
        if (cardType.equals("Visa-16")) {
            return prefixmatch(cnumber, 4);
        }
        if (cardType.equals("MC")) {
            // MasterCard starts with 2
            if (d == 2) {
                for (int i = 2221; i < 2721; i++){
                    if (prefixmatch(cnumber, i)){
                        return true;
                    }
                }
            }
            // MasterCard starts with 5
            if (d == 5) {
                return prefixmatch(cnumber, 51) || prefixmatch(cnumber, 52) || prefixmatch(cnumber, 53) ||
                         prefixmatch(cnumber, 54) || prefixmatch(cnumber, 55);
            }
        }
        return false;
    }

    /**
     * Calculate the size of the card number
     * @param d card number
     * @return the length of the card number
     */
    // Return the number of digits in d
    public static int thesize(long d) {
        String num = d + "";
        return num.length();
    }

    /**
     * Get the prefix of the given length
     * @param cnumber card number
     * @param k given length
     * @return the first k number of digits from number. If the number of digits in number is less than k, return number.
     */
    // Return the first k number of digits from
    // number. If the number of digits in number
    // is less than k, return number.
    public static long getprefx(long cnumber, int k) {
        if (thesize(cnumber) > k) {
            String num = cnumber + "";
            return Long.parseLong(num.substring(0, k));
        }
        return cnumber;
    }
}
