package controller;

import dto.Car;
import dto.InsuranceStatement;
import utils.FileUtils;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import dto.ICarManager;
import dto.ICrud;
import dto.IInsuranceManager;


public class InsuranceController implements ICrud<InsuranceStatement, String>, IInsuranceManager {

    private final Map<String, InsuranceStatement> insuranceMap;
    private final ICarManager carManager;
    private boolean isChanged;

    public InsuranceController(ICarManager carManager) {
        this.carManager = carManager;
        this.insuranceMap = new LinkedHashMap<>();
        this.isChanged = false;
    }

    @Override
    public boolean add(InsuranceStatement item) {
        if (!isValidStatement(item) || isInsuranceIdExisted(item.getInsuranceId())) {
            return false;
        }
        insuranceMap.put(item.getInsuranceId().trim(), item);
        this.isChanged = true;
        return true;
    }

    @Override
    public InsuranceStatement findById(String id) {
        if (id == null) {
            return null;
        }
        return insuranceMap.get(id.trim());
    }

    @Override
    public List<InsuranceStatement> getAll() {
        return new ArrayList<>(insuranceMap.values());
    }

    @Override
    public boolean update(InsuranceStatement item) {
        if (!isValidStatement(item) || !isInsuranceIdExisted(item.getInsuranceId())) {
            return false;
        }
        insuranceMap.put(item.getInsuranceId().trim(), item);
        this.isChanged = true;
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (id == null || !isInsuranceIdExisted(id)) {
            return false;
        }
        insuranceMap.remove(id.trim());
        this.isChanged = true;
        return true;
    }

    @Override
    public boolean isInsuranceIdExisted(String insuranceId) {
        if (insuranceId == null) {
            return false;
        }
        return insuranceMap.containsKey(insuranceId.trim());
    }

    private boolean isValidStatement(InsuranceStatement item) {
        if (item == null || !ValidationUtils.isValidInsuranceId(item.getInsuranceId())) {
            return false;
        }
        int period = item.getInsurancePeriod();
        if (period != 12 && period != 24 && period != 36) {
            return false;
        }
        if (item.getEstablishedDate() == null
                || item.getCustomerName() == null
                || item.getCustomerName().trim().isEmpty()
                || !ValidationUtils.isValidPlate(item.getLicensePlate())) {
            return false;
        }
        Car car = carManager.findById(item.getLicensePlate());
        if (car == null) {
            return false;
        }
        double expectedFee = calculateFee(car.getValue(), period);
        double fee = item.getInsuranceFees();
        return Double.isFinite(fee) && fee > 0 && fee == Math.rint(fee)
                && Double.compare(fee, expectedFee) == 0;
    }

    @Override
    public boolean isCarInsured(String licensePlate) {
        if (licensePlate == null) {
            return false;
        }
        String searchPlate = licensePlate.trim();
        for (InsuranceStatement statement : insuranceMap.values()) {
            if (statement.getLicensePlate() != null && statement.getLicensePlate().equalsIgnoreCase(searchPlate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double calculateFee(double carValue, int period) {
        if (!Double.isFinite(carValue) || carValue <= 0) {
            return 0.0;
        }
        double rate;
        switch (period) {
            case 12:
                rate = 0.25;
                break;
            case 24:
                rate = 0.40;
                break;
            case 36:
                rate = 0.45;
                break;
            default:
                return 0.0;
        }
        double fee = carValue * rate;
        return fee <= Long.MAX_VALUE ? Math.round(fee) : 0.0;
    }

    @Override
    public List<InsuranceStatement> filterByYear(int year) {
        List<InsuranceStatement> result = new ArrayList<>();
        for (InsuranceStatement stmt : insuranceMap.values()) {
            if (stmt.getEstablishedDate() != null && stmt.getEstablishedDate().getYear() == year) {
                result.add(stmt);
            }
        }
        return result;
    }

    @Override
    public List<InsuranceStatement> sortStatements(List<InsuranceStatement> list, String sortField, boolean isAsc) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<InsuranceStatement> sortedList = new ArrayList<>(list);
        Comparator<InsuranceStatement> comparator;

        switch (sortField) {
            case "2":
                comparator = Comparator.comparing(InsuranceStatement::getEstablishedDate);
                break;
            case "3":
                comparator = Comparator.comparing(InsuranceStatement::getLicensePlate, String.CASE_INSENSITIVE_ORDER);
                break;
            case "4":
                comparator = Comparator.comparingInt(InsuranceStatement::getInsurancePeriod);
                break;
            case "1":
            default:
                comparator = Comparator.comparing(InsuranceStatement::getInsuranceId);
                break;
        }

        if (!isAsc) {
            comparator = comparator.reversed();
        }

        sortedList.sort(comparator);
        return sortedList;
    }

    @Override
    public List<Car> getUninsuredCars(Map<String, Car> carMap) {
        List<Car> uninsuredCars = new ArrayList<>();
        if (carMap == null || carMap.isEmpty()) {
            return uninsuredCars;
        }
        Set<String> insuredPlates = new HashSet<>();
        for (InsuranceStatement statement : insuranceMap.values()) {
            if (statement.getLicensePlate() != null) {
                insuredPlates.add(statement.getLicensePlate().trim().toUpperCase());
            }
        }
        for (Car car : carMap.values()) {
            if (car.getLicensePlate() != null
                    && !insuredPlates.contains(car.getLicensePlate().trim().toUpperCase())) {
                uninsuredCars.add(car);
            }
        }
        return uninsuredCars;
    }

    @Override
    public Map<String, InsuranceStatement> getInsuranceMap() {
        return Collections.unmodifiableMap(insuranceMap);
    }

    @Override
    public boolean saveInsurances(String filename) {
        boolean success = FileUtils.writeBinaryFile(filename, this.insuranceMap);
        if (success) {
            this.isChanged = false;
        }
        return success;
    }

    @Override
    public boolean loadInsurances(String filename) {
        Object data = FileUtils.readBinaryFile(filename);
        if (!(data instanceof Map)) {
            return false;
        }
        Map<String, InsuranceStatement> validated = readStatements((Map<?, ?>) data, carManager.getCarMap());
        if (validated == null) {
            return false;
        }
        this.insuranceMap.clear();
        this.insuranceMap.putAll(validated);
        this.isChanged = false;
        return true;
    }

    @Override
    public boolean saveData(String carFile, String insuranceFile) {
        boolean success = FileUtils.atomicSaveTwoFiles(carFile, carManager.getCarMap(), insuranceFile, this.insuranceMap);
        if (success) {
            carManager.setChanged(false);
            setChanged(false);
        } else {
            carManager.setChanged(true);
            setChanged(true);
        }
        return success;
    }

    @Override
    public boolean loadData(String carFile, String insuranceFile) {
        Object carData = FileUtils.readBinaryFile(carFile);
        Object insuranceData = FileUtils.readBinaryFile(insuranceFile);
        if (!(carData instanceof Map) || !(insuranceData instanceof Map)) {
            return false;
        }
        Map<String, Car> cars = readCars((Map<?, ?>) carData);
        if (cars == null) {
            return false;
        }
        Map<String, InsuranceStatement> statements = readStatements((Map<?, ?>) insuranceData, cars);
        if (statements == null) {
            return false;
        }
        carManager.replaceAllCars(cars);
        insuranceMap.clear();
        insuranceMap.putAll(statements);
        carManager.setChanged(false);
        setChanged(false);
        return true;
    }

    private Map<String, Car> readCars(Map<?, ?> source) {
        Map<String, Car> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Car)) {
                return null;
            }
            Car car = (Car) entry.getValue();
            String key = (String) entry.getKey();
            if (!carManager.isValidCar(car) || !key.equals(car.getLicensePlate().trim().toUpperCase())) {
                return null;
            }
            result.put(key, car);
        }
        return result;
    }

    private Map<String, InsuranceStatement> readStatements(Map<?, ?> source, Map<String, Car> cars) {
        Map<String, InsuranceStatement> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof InsuranceStatement)) {
                return null;
            }
            InsuranceStatement statement = (InsuranceStatement) entry.getValue();
            String key = (String) entry.getKey();
            if (!ValidationUtils.isValidInsuranceId(statement.getInsuranceId())
                    || !key.equals(statement.getInsuranceId().trim())
                    || statement.getEstablishedDate() == null
                    || statement.getCustomerName() == null
                    || statement.getCustomerName().trim().isEmpty()
                    || !ValidationUtils.isValidPlate(statement.getLicensePlate())) {
                return null;
            }
            String plateKey = statement.getLicensePlate().trim().toUpperCase();
            Car linkedCar = cars.get(plateKey);
            if (linkedCar == null) {
                return null;
            }
            int period = statement.getInsurancePeriod();
            if (period != 12 && period != 24 && period != 36) {
                return null;
            }
            double expectedFee = calculateFee(linkedCar.getValue(), period);
            double fee = statement.getInsuranceFees();
            if (!Double.isFinite(fee) || fee <= 0 || fee != Math.rint(fee)
                    || Double.compare(fee, expectedFee) != 0) {
                return null;
            }
            result.put(key, statement);
        }
        return result;
    }

    @Override
    public boolean isChanged() {
        return this.isChanged;
    }

    @Override
    public void setChanged(boolean changed) {
        this.isChanged = changed;
    }
}
