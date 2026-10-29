package dto;

import java.io.Serializable;
import java.time.LocalDate;


public class InsuranceStatement implements Serializable {

    private static final long serialVersionUID = 1L;

    private String insuranceId;
    private LocalDate establishedDate;
    private String licensePlate;
    private String customerName;
    private int insurancePeriod;
    private double insuranceFees;

    public InsuranceStatement() {
    }

    public InsuranceStatement(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public InsuranceStatement(String insuranceId, LocalDate establishedDate, String licensePlate,
                              String customerName, int insurancePeriod, double insuranceFees) {
        this.insuranceId = insuranceId;
        this.establishedDate = establishedDate;
        this.licensePlate = licensePlate;
        this.customerName = customerName;
        this.insurancePeriod = insurancePeriod;
        this.insuranceFees = insuranceFees;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }

    public LocalDate getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getInsurancePeriod() {
        return insurancePeriod;
    }

    public void setInsurancePeriod(int insurancePeriod) {
        this.insurancePeriod = insurancePeriod;
    }

    public double getInsuranceFees() {
        return insuranceFees;
    }

    public void setInsuranceFees(double insuranceFees) {
        this.insuranceFees = insuranceFees;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InsuranceStatement)) {
            return false;
        }
        InsuranceStatement other = (InsuranceStatement) obj;
        return insuranceId != null && insuranceId.equals(other.insuranceId);
    }

   
    @Override
    public int hashCode() {
        return insuranceId == null ? 0 : insuranceId.hashCode();
    }

    @Override
    public String toString() {
        return "InsuranceStatement{" + insuranceId + ", " + establishedDate + ", " + licensePlate + ", "
                + customerName + ", " + insurancePeriod + " months, " + insuranceFees + "}";
    }
}
