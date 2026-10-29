package view;

import dto.Car;
import dto.InsuranceStatement;
import utils.ValidationUtils;

import java.util.List;

public class TablePrinter {

    //In danh sach xe ra bang
    
    public static void printCarList(List<Car> cars) {
        if (cars == null || cars.isEmpty()) {
            System.out.println(">> No car found to display!");
            return;
        }

        String border = "+-----+---------------+---------------------------+---------------+--------------+--------------+----------------------+-------+";
        String header = "| No. | License Plate | Vehicle Owner             | Brand         | Value ($)    | Reg. Date    | Reg. Place           | Seats |";

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);

        int index = 1;
        for (Car car : cars) {
            String dateStr = ValidationUtils.formatDate(car.getRegistrationDate());
            System.out.printf("| %-3d | %-13s | %-25s | %-13s | %,12.2f | %-12s | %-20s | %-5d |\n",
                    index++,
                    car.getLicensePlate(),
                    car.getCarOwner(),
                    car.getCarBrand(),
                    car.getValue(),
                    dateStr,
                    car.getRegistrationPlace(),
                    car.getVehicleType()
            );
        }
        System.out.println(border);
        System.out.println(">> Total: " + cars.size() + " vehicle(s).");
    }

    // In danh sach hop dong bao hiem theo nam 
    public static void printInsuranceList(List<InsuranceStatement> stmts, int year, String sortField, String sortType) {
        if (stmts == null || stmts.isEmpty()) {
            System.out.println(">> No insurance statement found for year " + year + "!");
            return;
        }

        System.out.println("\nReport : INSURANCE STATEMENTS");
        System.out.printf("From  :   01/01/%d  To: 12/31/%d\n\n", year, year);
        System.out.println("Sorted by: " + sortField);
        System.out.println("Sort type : " + sortType + "\n");

        String border = "+-----+--------------+------------------+---------------+---------------------------+------------------+----------------+";
        String header = "| No. | Insurance Id | Established Date | License plate | Customer                  | Insurance period | Insurance fees |";

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);

        int index = 1;
        for (InsuranceStatement stmt : stmts) {
            String dateStr = ValidationUtils.formatDate(stmt.getEstablishedDate());
            double fees = stmt.getInsuranceFees();
            String feeStr = String.format("$ %,.0f", fees);
            System.out.printf("| %-3d | %-12s | %-16s | %-13s | %-25s | %-16d | %-14s |\n",
                    index++,
                    stmt.getInsuranceId(),
                    dateStr,
                    stmt.getLicensePlate(),
                    stmt.getCustomerName(),
                    stmt.getInsurancePeriod(),
                    feeStr
            );
        }
        System.out.println(border);
        System.out.println(">> Total: " + stmts.size() + " statement(s).");
    }

    // In bao cao danh sach xe chua co hop dong bao hiem
    public static void printUninsuredCarList(List<Car> cars, String sortField, String sortType) {
        if (cars == null || cars.isEmpty()) {
            System.out.println(">> No uninsured car found to display!");
            return;
        }

        System.out.println("\nReport: UNINSURED CARS\n");
        System.out.println("Sorted by : " + sortField);
        System.out.println("Sort type : " + sortType + "\n");

        String border = "+-----+---------------+-------------------+---------------------------+---------------+--------------+--------------+";
        String header = "| No. | License plate | Registration Date | Vehicle Owner             | Brand         | Vehicle type | Value        |";

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);

        int index = 1;
        for (Car car : cars) {
            String dateStr = ValidationUtils.formatDate(car.getRegistrationDate());
            String valueStr = String.format("$ %,.0f", car.getValue());
            System.out.printf("| %-3d | %-13s | %-17s | %-25s | %-13s | %-12d | %-12s |\n",
                    index++,
                    car.getLicensePlate(),
                    dateStr,
                    car.getCarOwner(),
                    car.getCarBrand(),
                    car.getVehicleType(),
                    valueStr
            );
        }
        System.out.println(border);
        System.out.println(">> Total: " + cars.size() + " vehicle(s).");
    }
}
