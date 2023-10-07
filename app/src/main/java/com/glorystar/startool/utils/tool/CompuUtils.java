package com.glorystar.startool.utils.tool;




import com.glorystar.startool.utils.db.Student;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 *
 * @author jqlin
 */
public class CompuUtils {

    /**
     *
     */
    public static final int ZERO = 0;
    /**
     *
     */
    public static final int ONE = 1;
    /**
     *
     */
    public static final int MINUS_ONE = -1;



    public static int compareTo(String a, String b) {
        return new BigDecimal(a).compareTo(new BigDecimal(b));
    }


    public static BigDecimal add(String a, String b) {
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).add(new BigDecimal(b), mc);
    }



    public static int add(int... vals) {
        if (vals == null || vals.length <= 0) {
            return 0;
        }

        int sum = 0;
        for (int val : vals) {
            sum = sum + val;
        }

        return sum;
    }

    public static long add(long... vals) {
        if (vals == null || vals.length <= 0) {
            return 0L;
        }

        long sum = 0L;
        for (long val : vals) {
            sum = sum + val;
        }

        return sum;
    }


    public static BigDecimal subtract(String a, String b) {
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).subtract(new BigDecimal(b), mc);
    }


    public static BigDecimal multiply(String a, String b) {
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        return new BigDecimal(a).multiply(new BigDecimal(b), mc);
    }


    public static double mul(double v1, BigDecimal v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        //BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(v2).doubleValue();
    }


    public static double add(double v1, double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.add(b2).doubleValue();
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.subtract(b2).doubleValue();
    }



    public static double divide(String a, String b) {
        return new BigDecimal(a).divide(new BigDecimal(b), 20, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    public void Sefor(List<Student> arr , int i) {
        for (i=0;i < arr.size();i++){

        }
    }



}