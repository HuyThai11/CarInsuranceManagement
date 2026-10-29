package utils;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Lớp tiện ích nhập liệu từ bàn phím (InputUtils).
 * Sử dụng một Scanner duy nhất, xử lý triệt để hiện tượng trôi lệnh (Buffer Flushing),
 * bắt buộc nhập đúng dữ liệu, và hỗ trợ cơ chế Enter để giữ nguyên giá trị cũ (Update).
 */
public class InputUtils {

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Nhập một chuỗi không rỗng từ bàn phím.
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(">> Error: Input cannot be empty! Please try again.");
        }
    }

    /**
     * Nhập chuỗi tùy chọn: nếu người dùng nhấn Enter (chuỗi rỗng), giữ nguyên giá trị cũ.
     */
    public static String readOptionalString(String prompt, String oldData) {
        System.out.print(prompt + " [" + oldData + "]: ");
        String input = SCANNER.nextLine().trim();
        if (input.isEmpty()) {
            return oldData;
        }
        return input;
    }

    /**
     * Nhập số nguyên nằm trong đoạn [min, max].
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(">> Error: Value must be an integer between " + min + " and " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid integer format! Please try again.");
            }
        }
    }

    /**
     * Nhập số nguyên tùy chọn: nhấn Enter giữ nguyên giá trị cũ.
     */
    public static int readOptionalInt(String prompt, int min, int max, int oldData) {
        while (true) {
            System.out.print(prompt + " [" + oldData + "]: ");
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return oldData;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(">> Error: Value must be between " + min + " and " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid integer format! Please try again.");
            }
        }
    }

    /**
     * Nhập số thực lớn hơn giá trị min quy định (ví dụ: giá trị xe > 999).
     */
    public static double readDoubleMin(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value > min) {
                    return value;
                }
                System.out.println(">> Error: Value must be strictly greater than " + min + "!");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid number format! Please try again.");
            }
        }
    }

    /**
     * Nhập số thực tùy chọn lớn hơn min: nhấn Enter giữ nguyên giá trị cũ.
     */
    public static double readOptionalDouble(String prompt, double min, double oldData) {
        while (true) {
            System.out.print(prompt + " [" + oldData + "]: ");
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return oldData;
            }
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value > min) {
                    return value;
                }
                System.out.println(">> Error: Value must be strictly greater than " + min + "!");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid number format! Please try again.");
            }
        }
    }

    /**
     * Nhập loại xe hợp lệ (chỉ chấp nhận 5, 7 hoặc 9 chỗ ngồi).
     */
    public static int readVehicleType(String prompt) {
        while (true) {
            int type = readIntInRange(prompt, 1, 100);
            if (type == 5 || type == 7 || type == 9) {
                return type;
            }
            System.out.println(">> Error: Vehicle type must be 5, 7, or 9 seats only!");
        }
    }

    /**
     * Nhập loại xe tùy chọn: nếu nhấn Enter giữ nguyên loại xe cũ.
     */
    public static int readOptionalVehicleType(String prompt, int oldType) {
        while (true) {
            System.out.print(prompt + " [" + oldType + "]: ");
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return oldType;
            }
            try {
                int type = Integer.parseInt(input);
                if (type == 5 || type == 7 || type == 9) {
                    return type;
                }
                System.out.println(">> Error: Vehicle type must be 5, 7, or 9 seats only!");
            } catch (NumberFormatException e) {
                System.out.println(">> Error: Invalid integer format! Please try again.");
            }
        }
    }

    /**
     * Nhập kỳ hạn bảo hiểm (chỉ chấp nhận 12, 24 hoặc 36 tháng).
     */
    public static int readInsurancePeriod(String prompt) {
        while (true) {
            int period = readIntInRange(prompt, 1, 100);
            if (period == 12 || period == 24 || period == 36) {
                return period;
            }
            System.out.println(">> Error: Insurance period must be 12, 24, or 36 months!");
        }
    }

    /**
     * Nhập ngày tháng theo định dạng MM/dd/yyyy.
     */
    public static LocalDate readLocalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            LocalDate date = ValidationUtils.parseDate(input);
            if (date != null) {
                return date;
            }
            System.out.println(">> Error: Invalid date! Format must be MM/dd/yyyy (e.g., 01/15/2023) and exist on calendar.");
        }
    }

    /**
     * Nhập ngày tháng tùy chọn: nhấn Enter giữ nguyên ngày cũ.
     */
    public static LocalDate readOptionalLocalDate(String prompt, LocalDate oldDate) {
        String formattedOld = ValidationUtils.formatDate(oldDate);
        while (true) {
            System.out.print(prompt + " [" + formattedOld + "]: ");
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                return oldDate;
            }
            LocalDate date = ValidationUtils.parseDate(input);
            if (date != null) {
                return date;
            }
            System.out.println(">> Error: Invalid date! Format must be MM/dd/yyyy and exist on calendar.");
        }
    }

    /**
     * Nhập biển số xe đúng định dạng Regex: 50H3-123.45
     */
    public static String readPlate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String plate = SCANNER.nextLine().trim().toUpperCase();
            if (ValidationUtils.isValidPlate(plate)) {
                return plate;
            }
            System.out.println(">> Error: Invalid license plate format! Example of valid format: 50H3-123.45");
        }
    }

    /**
     * Nhập mã bảo hiểm đúng định dạng Regex: 4 chữ số.
     */
    public static String readInsuranceId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String id = SCANNER.nextLine().trim();
            if (ValidationUtils.isValidInsuranceId(id)) {
                return id;
            }
            System.out.println(">> Error: Invalid insurance ID format! Must be exactly 4 digits (e.g., 0001, 1234).");
        }
    }

    /**
     * Nhập xác nhận Yes / No (chấp nhận Y/y hoặc N/n).
     * @return true nếu người dùng chọn Y/y, false nếu chọn N/n
     */
    public static boolean readConfirmYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) {
                return true;
            }
            if (input.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println(">> Error: Please enter only 'Y' or 'N'!");
        }
    }
}
