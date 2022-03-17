import java.util.Vector;

public interface Dao<T> {

    T getByParam(String nickName);

    Vector<T> getAll();

    void save(T t);

}
