package engsoc.qlife.utility;

/**
 * Created by cookc on 2018-01-18.
 * Interface that allows a callback with an object parameter in the callback function
 */
public interface CallableObj<T> {
    T call(Object obj) throws Exception;
}
