package dto;

import java.io.Serializable;
import java.time.LocalDate;


public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    private String licensePlate;
    private String carOwner;
    private String carBrand;
    private double value;
    private LocalDate registrationDate;
    private String registrationPlace;
    private int vehicleType;

    public Car() {
    }

    public Car(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Car(String licensePlate, String carOwner, String carBrand, double value,
               LocalDate registrationDate, String registrationPlace, int vehicleType) {
        this.licensePlate = licensePlate;
        this.carOwner = carOwner;
        this.carBrand = carBrand;
        this.value = value;
        this.registrationDate = registrationDate;
        this.registrationPlace = registrationPlace;
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(String carOwner) {
        this.carOwner = carOwner;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationPlace() {
        return registrationPlace;
    }

    public void setRegistrationPlace(String registrationPlace) {
        this.registrationPlace = registrationPlace;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Hai xe được coi là MỘT nếu trùng biển số (không phân biệt hoa/thường).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Car)) {
            return false;
        }
        Car other = (Car) obj;
        return licensePlate != null && licensePlate.equalsIgnoreCase(other.licensePlate);
    }

    /**
     * hashCode phải đồng bộ với equals: cùng biển số (bỏ qua hoa/thường) thì cùng hashCode.
     */
    @Override
    public int hashCode() {
        return licensePlate == null ? 0 : licensePlate.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return "Car{" + licensePlate + ", " + carOwner + ", " + carBrand + ", " + value + ", "
                + registrationDate + ", " + registrationPlace + ", " + vehicleType + " seats}";
    }
}
