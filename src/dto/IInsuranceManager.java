package dto;

import java.util.List;
import java.util.Map;


public interface IInsuranceManager extends ICrud<InsuranceStatement, String> {

    boolean isInsuranceIdExisted(String insuranceId);

    boolean isCarInsured(String licensePlate);

    double calculateFee(double carValue, int period);

    List<InsuranceStatement> filterByYear(int year);

    List<InsuranceStatement> sortStatements(List<InsuranceStatement> list, String sortField, boolean isAsc);

    List<Car> getUninsuredCars(Map<String, Car> carMap);

    Map<String, InsuranceStatement> getInsuranceMap();

    boolean saveInsurances(String filename);

    boolean loadInsurances(String filename);

    boolean saveData(String carFile, String insuranceFile);

    boolean loadData(String carFile, String insuranceFile);

    boolean isChanged();

    void setChanged(boolean changed);
}
