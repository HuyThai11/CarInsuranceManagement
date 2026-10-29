package view;

import controller.CarController;
import controller.InsuranceController;
import controller.Menu;
import dto.IMenu;
import dto.ICarManager;
import dto.IInsuranceManager;


public class Main {

    private static final String CAR_FILE = "carInfo.dat";
    private static final String INSURANCE_FILE = "insurances.dat";

    public static void main(String[] args) {
        ICarManager carManager = new CarController();
        IInsuranceManager insuranceManager = new InsuranceController(carManager);

        carManager.setDeletionGuard(plate -> !insuranceManager.isCarInsured(plate));

        insuranceManager.loadData(CAR_FILE, INSURANCE_FILE);

        IMenu menu = new Menu("CAR INSURANCE MANAGEMENT SYSTEM");
        menu.addItem("1. Add car information");
        menu.addItem("2. Find a car");
        menu.addItem("3. Update a car");
        menu.addItem("4. Delete a car");
        menu.addItem("5. Add an insurance statement");
        menu.addItem("6. List of insurance statements");
        menu.addItem("7. Report on uninsured cars");
        menu.addItem("8. Save data");
        menu.addItem("9. Load data");
        menu.addItem("10. Quit");

        InsuranceAppView appView = new InsuranceAppView(carManager, insuranceManager, menu);
        appView.run();
    }
}
