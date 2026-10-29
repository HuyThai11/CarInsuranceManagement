package controller;

import dto.Car;
import utils.FileUtils;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import dto.ICarManager;
import dto.ICrud;


public class CarController implements ICrud<Car, String>, ICarManager {

    private final Map<String, Car> carMap;
    private boolean isChanged;
    private Predicate<String> deletionGuard;

    public CarController() {
        this.carMap = new LinkedHashMap<>();
        this.isChanged = false;
        this.deletionGuard = null;
    }

    @Override
    public boolean add(Car item) {
        if (!isValidCar(item)) {
            return false;
        }
        if (isPlateExisted(item.getLicensePlate())) {
            return false;
        }
        carMap.put(item.getLicensePlate().trim().toUpperCase(), item);
        this.isChanged = true;
        return true;
    }

    @Override
    public Car findById(String id) {
        if (id == null) {
            return null;
        }
        return carMap.get(id.trim().toUpperCase());
    }

    @Override
    public List<Car> getAll() {
        return new ArrayList<>(carMap.values());
    }

    @Override
    public boolean update(Car item) {
        if (item == null || !isValidCar(item)) {
            return false;
        }
        String key = item.getLicensePlate().trim().toUpperCase();
        Car existingCar = carMap.get(key);
        if (existingCar == null) {
            return false;
        }
        // Khóa biển số: Chỉ cập nhật các thông tin thuộc tính, không đổi khóa chính
        existingCar.setCarOwner(item.getCarOwner());
        existingCar.setCarBrand(item.getCarBrand());
        existingCar.setValue(item.getValue());
        existingCar.setRegistrationDate(item.getRegistrationDate());
        existingCar.setRegistrationPlace(item.getRegistrationPlace());
        existingCar.setVehicleType(item.getVehicleType());
        this.isChanged = true;
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (!canDelete(id)) {
            return false;
        }
        carMap.remove(id.trim().toUpperCase());
        this.isChanged = true;
        return true;
    }

    @Override
    public void setDeletionGuard(Predicate<String> guard) {
        this.deletionGuard = guard;
    }

    @Override
    public boolean canDelete(String licensePlate) {
        if (licensePlate == null || !isPlateExisted(licensePlate)) {
            return false;
        }
        if (deletionGuard != null && !deletionGuard.test(licensePlate.trim().toUpperCase())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPlateExisted(String licensePlate) {
        if (licensePlate == null) {
            return false;
        }
        return carMap.containsKey(licensePlate.trim().toUpperCase());
    }

    @Override
    public boolean isValidCar(Car car) {
        if (car == null) {
            return false;
        }
        return isValidPlate(car)
            && isValidOwner(car)
            && isValidBrand(car)
            && isValidValue(car)
            && isValidRegistration(car)
            && isValidVehicleType(car);
    }

    private boolean isValidPlate(Car car) {
        return ValidationUtils.isValidPlate(car.getLicensePlate());
    }

    private boolean isValidOwner(Car car) {
        String owner = car.getCarOwner();
        return owner != null && owner.trim().length() >= 2 && owner.trim().length() <= 35;
    }

    private boolean isValidBrand(Car car) {
        return car.getCarBrand() != null && !car.getCarBrand().trim().isEmpty();
    }

    private boolean isValidValue(Car car) {
        return Double.isFinite(car.getValue()) && car.getValue() > 999;
    }

    private boolean isValidRegistration(Car car) {
        return car.getRegistrationDate() != null
            && car.getRegistrationPlace() != null
            && !car.getRegistrationPlace().trim().isEmpty();
    }

    private boolean isValidVehicleType(Car car) {
        int type = car.getVehicleType();
        return type == 5 || type == 7 || type == 9;
    }

    @Override
    public List<Car> sortCars(List<Car> list, String sortField, boolean isAsc) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<Car> sortedList = new ArrayList<>(list);
        Comparator<Car> comparator;

        switch (sortField) {
            case "2":
                comparator = Comparator.comparing(Car::getCarOwner, String.CASE_INSENSITIVE_ORDER);
                break;
            case "3":
                comparator = Comparator.comparing(Car::getRegistrationDate);
                break;
            case "4":
                comparator = Comparator.comparingInt(Car::getVehicleType);
                break;
            case "1":
            default:
                comparator = Comparator.comparing(Car::getLicensePlate, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        if (!isAsc) {
            comparator = comparator.reversed();
        }

        sortedList.sort(comparator);
        return sortedList;
    }

    @Override
    public Map<String, Car> getCarMap() {
        return Collections.unmodifiableMap(carMap);
    }

    @Override
    public void replaceAllCars(Map<String, Car> newCars) {
        if (newCars != null) {
            this.carMap.clear();
            this.carMap.putAll(newCars);
            this.isChanged = false;
        }
    }

    @Override
    public boolean saveCars(String filename) {
        boolean success = FileUtils.writeBinaryFile(filename, this.carMap);
        if (success) {
            this.isChanged = false;
        }
        return success;
    }

    @Override
    public boolean loadCars(String filename) {
        Object data = FileUtils.readBinaryFile(filename);
        if (!(data instanceof Map)) {
            return false;
        }
        Map<?, ?> rawMap = (Map<?, ?>) data;
        Map<String, Car> validatedCars = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Car)) {
                return false;
            }
            Car car = (Car) entry.getValue();
            String key = ((String) entry.getKey()).trim().toUpperCase();
            if (!isValidCar(car) || !key.equals(car.getLicensePlate().trim().toUpperCase())) {
                return false;
            }
            validatedCars.put(key, car);
        }
        this.carMap.clear();
        this.carMap.putAll(validatedCars);
        this.isChanged = false;
        return true;
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
