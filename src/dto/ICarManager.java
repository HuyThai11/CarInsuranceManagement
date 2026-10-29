package dto;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public interface ICarManager extends ICrud<Car, String> {

   
    void setDeletionGuard(Predicate<String> guard);

    
    boolean canDelete(String licensePlate);

    boolean isPlateExisted(String licensePlate);
    
    boolean isValidCar(Car car);

    List<Car> sortCars(List<Car> list, String sortField, boolean isAsc);
    
    Map<String, Car> getCarMap();

    void replaceAllCars(Map<String, Car> newCars);

    boolean saveCars(String filename);

    boolean loadCars(String filename);

    boolean isChanged();

    void setChanged(boolean changed);
}
 