package view;

import dto.Car;
import dto.IMenu;
import dto.InsuranceStatement;
import utils.InputUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import dto.ICarManager;
import dto.IInsuranceManager;

/**
 * Lớp điều phối giao diện Console cho ứng dụng Quản lý bảo hiểm xe ô tô.
 */
public class InsuranceAppView {
    private static final String CAR_FILE = "carInfo.dat";
    private static final String INSURANCE_FILE = "insurances.dat";

    private final ICarManager carManager;
    private final IInsuranceManager insuranceManager;
    private final IMenu menu;

    private boolean isRunning;

    public InsuranceAppView(ICarManager carManager, IInsuranceManager insuranceManager, IMenu menu) {
        this.carManager = carManager;
        this.insuranceManager = insuranceManager;
        this.menu = menu;
        this.isRunning = true;
    }

    public void showMessage(String message) {
        System.out.println(">> " + message);
    }

    private Car inputNewCar() {
        System.out.println("\n--- ENTER NEW CAR INFORMATION ---");
        String plate;
        while (true) {
            plate = InputUtils.readPlate("Enter license plate (e.g., 50H3-123.45): ");
            if (carManager.isPlateExisted(plate)) {
                showMessage("Error: License plate already exists in the system! Please try another.");
            } else {
                break;
            }
        }

        String owner = InputUtils.readNonEmptyString("Enter vehicle owner (2-35 chars): ");
        while (owner.length() < 2 || owner.length() > 35) {
            showMessage("Error: Owner name must be between 2 and 35 characters!");
            owner = InputUtils.readNonEmptyString("Enter vehicle owner (2-35 chars): ");
        }

        String brand = InputUtils.readNonEmptyString("Enter car brand: ");
        double value = InputUtils.readDoubleMin("Enter car value (> 999 $): ", 999.0);
        LocalDate regDate = InputUtils.readLocalDate("Enter registration date (MM/dd/yyyy): ");
        String place = InputUtils.readNonEmptyString("Enter registration place: ");
        int type = InputUtils.readVehicleType("Enter vehicle type (5, 7, or 9 seats): ");

        return new Car(plate, owner, brand, value, regDate, place, type);
    }

    public void handleAddCar() {
        do {
            Car car = inputNewCar();
            boolean added = carManager.add(car);
            if (added) {
                showMessage("Car added successfully!");
            } else {
                showMessage("Failed to add car! Please check your input data.");
            }
        } while (InputUtils.readConfirmYesNo("Do you want to continue adding another car? (Y/N): "));
    }

    public void handleFindCar() {
        System.out.println("\n--- FIND CAR BY LICENSE PLATE ---");
        String plate = InputUtils.readPlate("Enter license plate to find: ");
        Car car = carManager.findById(plate);

        if (car == null) {
            showMessage("Unregistered vehicle");
        } else {
            TablePrinter.printCarList(Collections.singletonList(car));
        }
    }

    private Car inputUpdatedCar(Car existing) {
        System.out.println("\n--- UPDATE CAR INFORMATION FOR PLATE: " + existing.getLicensePlate() + " ---");
        System.out.println("(Note: Press Enter without typing to keep existing data)");

        String owner = InputUtils.readOptionalString("Enter vehicle owner", existing.getCarOwner());
        while (owner.length() < 2 || owner.length() > 35) {
            showMessage("Error: Owner name must be between 2 and 35 characters!");
            owner = InputUtils.readOptionalString("Enter vehicle owner", existing.getCarOwner());
        }

        String brand = InputUtils.readOptionalString("Enter car brand", existing.getCarBrand());
        double value = InputUtils.readOptionalDouble("Enter car value", 999.0, existing.getValue());
        LocalDate regDate = InputUtils.readOptionalLocalDate("Enter registration date", existing.getRegistrationDate());
        String place = InputUtils.readOptionalString("Enter registration place", existing.getRegistrationPlace());
        int type = InputUtils.readOptionalVehicleType("Enter vehicle type (5, 7, or 9)", existing.getVehicleType());

        return new Car(existing.getLicensePlate(), owner, brand, value, regDate, place, type);
    }

    public void handleUpdateCar() {
        System.out.println("\n--- UPDATE CAR INFORMATION ---");
        String plate = InputUtils.readPlate("Enter license plate to update: ");
        Car existing = carManager.findById(plate);

        if (existing == null) {
            showMessage("Unregistered vehicle");
            return;
        }

        Car updated = inputUpdatedCar(existing);
        boolean success = carManager.update(updated);
        if (success) {
            showMessage("Car information updated successfully!");
        } else {
            showMessage("Failed to update car information.");
        }
    }

    public void handleDeleteCar() {
        System.out.println("\n--- DELETE CAR INFORMATION ---");
        String plate = InputUtils.readPlate("Enter license plate to delete: ");
        Car car = carManager.findById(plate);

        if (car == null) {
            showMessage("Unregistered vehicle");
            return;
        }

        if (!carManager.canDelete(plate)) {
            showMessage("CANNOT DELETE: This car is currently linked to one or more insurance statements!");
            return;
        }

        boolean confirm = InputUtils.readConfirmYesNo("Are you sure you want to delete this car (" + plate + ")? (Y/N): ");
        if (!confirm) {
            showMessage("Deletion cancelled.");
            return;
        }

        boolean deleted = carManager.delete(plate);
        if (deleted) {
            showMessage("Car deleted successfully!");
        } else {
            showMessage("Failed to delete car.");
        }
    }

    private InsuranceStatement inputNewStatement() {
        System.out.println("\n--- ENTER NEW INSURANCE STATEMENT ---");

        String id;
        while (true) {
            id = InputUtils.readInsuranceId("Enter insurance ID (exactly 4 digits, e.g., 0001): ");
            if (insuranceManager.isInsuranceIdExisted(id)) {
                showMessage("Error: Insurance ID already exists! Please try another ID.");
            } else {
                break;
            }
        }

        LocalDate estDate = InputUtils.readLocalDate("Enter established date (MM/dd/yyyy): ");

        String plate;
        Car car;
        while (true) {
            plate = InputUtils.readPlate("Enter license plate for this insurance: ");
            car = carManager.findById(plate);
            if (car == null) {
                showMessage("Error: License plate does not exist in the car catalog! Please register the car first.");
            } else {
                break;
            }
        }

        String customer = InputUtils.readNonEmptyString("Enter customer name: ");
        int period = InputUtils.readInsurancePeriod("Enter insurance period (12, 24, or 36 months): ");

        // Tự động tính phí bảo hiểm thông qua Controller
        double fee = insuranceManager.calculateFee(car.getValue(), period);
        System.out.printf(">> Vehicle Value: %,.2f $ | Period: %d months -> Calculated Fee: %,.2f $\n",
                car.getValue(), period, fee);

        return new InsuranceStatement(id, estDate, plate, customer, period, fee);
    }

    public void handleAddInsurance() {
        do {
            InsuranceStatement stmt = inputNewStatement();
            boolean added = insuranceManager.add(stmt);
            if (added) {
                showMessage("Insurance statement added successfully!");
            } else {
                showMessage("Failed to add insurance statement.");
            }
        } while (InputUtils.readConfirmYesNo("Do you want to continue adding another insurance statement? (Y/N): "));
    }

    public void handleListInsuranceByYear() {
        System.out.println("\n--- LIST INSURANCE STATEMENTS BY YEAR ---");
        int year = InputUtils.readIntInRange("Enter year to filter (e.g., 2023): ", 1900, 2100);

        List<InsuranceStatement> filtered = insuranceManager.filterByYear(year);
        if (filtered.isEmpty()) {
            showMessage("No insurance statement found for year " + year + "!");
            return;
        }

        System.out.println("\nChoose sorting criteria:");
        System.out.println("  1. Sort by Statement ID");
        System.out.println("  2. Sort by Established Date");
        System.out.println("  3. Sort by License Plate");
        System.out.println("  4. Sort by Insurance Period");
        int sortChoice = InputUtils.readIntInRange(">> Select field (1-4): ", 1, 4);

        System.out.println("Choose sorting order:");
        System.out.println("  1. Ascending (ASC)");
        System.out.println("  2. Descending (DESC)");
        int orderChoice = InputUtils.readIntInRange(">> Select order (1-2): ", 1, 2);
        boolean isAsc = (orderChoice == 1);

        String[] fieldNames = {"Insurance Id", "Established Date", "License plate", "Insurance period"};
        String fieldName = fieldNames[sortChoice - 1];
        String orderName = isAsc ? "ASC" : "DESC";

        List<InsuranceStatement> sorted = insuranceManager.sortStatements(filtered, String.valueOf(sortChoice), isAsc);
        TablePrinter.printInsuranceList(sorted, year, fieldName, orderName);
    }

    public void handleReportUninsuredCars() {
        System.out.println("\n--- REPORT UNINSURED CARS ---");
        List<Car> uninsured = insuranceManager.getUninsuredCars(carManager.getCarMap());

        if (uninsured.isEmpty()) {
            showMessage("All registered cars have insurance statements!");
            return;
        }

        System.out.println("\nChoose sorting criteria:");
        System.out.println("  1. Sort by License Plate");
        System.out.println("  2. Sort by Vehicle Owner");
        System.out.println("  3. Sort by Registration Date");
        System.out.println("  4. Sort by Vehicle Type");
        int sortChoice = InputUtils.readIntInRange(">> Select field (1-4): ", 1, 4);

        System.out.println("Choose sorting order:");
        System.out.println("  1. Ascending (ASC)");
        System.out.println("  2. Descending (DESC)");
        int orderChoice = InputUtils.readIntInRange(">> Select order (1-2): ", 1, 2);
        boolean isAsc = (orderChoice == 1);

        String[] fieldNames = {"License plate", "Vehicle Owner", "Registration Date", "Vehicle type"};
        String fieldName = fieldNames[sortChoice - 1];
        String orderName = isAsc ? "ASC" : "DESC";

        List<Car> sorted = carManager.sortCars(uninsured, String.valueOf(sortChoice), isAsc);
        TablePrinter.printUninsuredCarList(sorted, fieldName, orderName);
    }

    public boolean handleSaveData() {
        System.out.println("\n--- SAVE DATA TO BINARY FILES ---");
        if (insuranceManager.saveData(CAR_FILE, INSURANCE_FILE)) {
            showMessage("All data saved successfully to carInfo.dat and insurances.dat!");
            return true;
        } else {
            showMessage("Warning: Some data could not be saved!");
            return false;
        }
    }

    public void handleLoadData() {
        System.out.println("\n--- LOAD DATA FROM BINARY FILES ---");
        if ((carManager.isChanged() || insuranceManager.isChanged())
                && !InputUtils.readConfirmYesNo("Unsaved changes will be discarded. Continue loading? (Y/N): ")) {
            showMessage("Loading cancelled.");
            return;
        }
        if (insuranceManager.loadData(CAR_FILE, INSURANCE_FILE)) {
            showMessage("Data loaded successfully! Loaded " + carManager.getAll().size()
                    + " car(s) and " + insuranceManager.getInsuranceMap().size() + " insurance statement(s).");
        } else {
            showMessage("Load failed: both valid data files are required. Current data was kept.");
        }
    }

    public void handleQuit() {
        boolean confirmExit = InputUtils.readConfirmYesNo("\nDo you really want to exit the application? (Y/N): ");
        if (!confirmExit) {
            showMessage("Return to main menu.");
            return;
        }

        boolean dataChanged = carManager.isChanged() || insuranceManager.isChanged();
        if (dataChanged && !handleSaveData()) {
            showMessage("Exit cancelled because changed data could not be saved.");
            return;
        }

        showMessage("Thank you for using Car Insurance Management System. Goodbye!");
        this.isRunning = false;
    }

    public void run() {
        while (this.isRunning) {
            menu.showMenu();
            int choice = menu.getChoice();

            switch (choice) {
                case 1:
                    handleAddCar();
                    break;
                case 2:
                    handleFindCar();
                    break;
                case 3:
                    handleUpdateCar();
                    break;
                case 4:
                    handleDeleteCar();
                    break;
                case 5:
                    handleAddInsurance();
                    break;
                case 6:
                    handleListInsuranceByYear();
                    break;
                case 7:
                    handleReportUninsuredCars();
                    break;
                case 8:
                    handleSaveData();
                    break;
                case 9:
                    handleLoadData();
                    break;
                case 10:
                    handleQuit();
                    break;
                default:
                    showMessage("Invalid option! Please choose between 1 and 10.");
                    break;
            }
        }
    }
}
