import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LabTestCase {

    public static class ValueError implements Comparable<ValueError> {
        public double value, error;
        public int labNumber;
        public ValueError(double value, double error) {
            this.value = value;
            this.error = error;
        }

        public ValueError(double value, double error, int labNumber) {
            this.value = value;
            this.error = error;
            this.labNumber = labNumber;
        }

        @Override
        public int compareTo(ValueError o) {
            if (value < o.value) {
                return -1;
            }
            if (value > o.value) {
                return 1;
            }
            return Double.compare(error, o.error);
        }
    }

    private ArrayList<ValueError> labMeasures = new ArrayList<>();

    private int pointNumber;
    private boolean calculated = false;
    private String md5String = "";
    private String md5Result = "";
    private double meanValue = 0;
    private double meanSquareError = 0;
    private double successCriteria;

    LabTestCase(double successCriteria) {
        this.successCriteria = successCriteria;
    }

    boolean isCalculated() {
        return calculated;
    }

    double getMeanValue() {
        return meanValue;
    }

    double getMeanSquareError() {
        return meanSquareError;
    }

    String getMd5String() {
        return md5String;
    }

    String getMd5Result() {
        return md5Result;
    }

    double getDiff(ValueError measure) {
        if (calculated)
            return Math.abs(measure.value - meanValue) / meanSquareError;
        return 0;
    }

    void setPointNumber(int number) {
        pointNumber = number;
    }
    int getPointNumber() {
        return pointNumber;
    }

    double getSuccessCriteria() {
        return successCriteria;
    }

    void setLabMeasure(ValueError measure) {
        labMeasures.add(measure);
    }

    ArrayList<ValueError> getLabMeasures() {
        return labMeasures;
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    void calculateResults() {
        md5String = "";
        md5Result = "";
        ArrayList<ValueError> valueErrors = new ArrayList<>();
        meanValue = 0;
        meanSquareError = 0;

        for (ValueError labMeasure : labMeasures) {
            meanValue += labMeasure.value;
            meanSquareError += labMeasure.error * labMeasure.error;
            valueErrors.add(new ValueError(labMeasure.value, labMeasure.error));
        }
        meanValue /= labMeasures.size();
        meanSquareError = Math.sqrt(meanSquareError);

        valueErrors.sort(ValueError::compareTo);
        for (ValueError valueError : valueErrors) {
            md5String += String.format("%.4f;%.4f;", valueError.value, valueError.error);
        }
        md5String += String.format("%.4f", successCriteria);
        try {
            md5Result = bytesToHex(MessageDigest.getInstance("MD5").digest(md5String.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        calculated = true;
    }


}
