package util;

import java.util.HashMap;

/**
 * Created by offon on 15. 2. 9..
 */
public class DataTransfer {

    private HashMap<String, Object> hashMap;

    private static DataTransfer object;

    public static DataTransfer getInstance() {
        if (object == null)
            object = new DataTransfer();

        return object;
    }

    public DataTransfer() {
        hashMap = new HashMap<>();
    }

}
