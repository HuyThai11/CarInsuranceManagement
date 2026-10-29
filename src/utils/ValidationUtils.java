package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;


public class ValidationUtils {

    
    public static final String PLATE_REGEX = "^[0-9]{2}[A-Z][0-9]-[0-9]{3}\\.[0-9]{2}$";

    
    public static final String INSURANCE_ID_REGEX = "^[0-9]{4}$";

    
    private static final DateTimeFormatter STRICT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    
    public static boolean isValidPlate(String plate) {
        if (plate == null) {
            return false;
        }
        return plate.trim().matches(PLATE_REGEX);
    }

   
    public static boolean isValidInsuranceId(String id) {
        if (id == null) {
            return false;
        }
        return id.trim().matches(INSURANCE_ID_REGEX);
    }

    
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), STRICT_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_FORMATTER);
    }
}
