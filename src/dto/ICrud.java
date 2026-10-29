package dto;

import java.util.List;

public interface ICrud<T, ID> {

    boolean add(T item);


    T findById(ID id);

    List<T> getAll();

    boolean update(T item);

    boolean delete(ID id);
}
