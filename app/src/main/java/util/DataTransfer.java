package util;

import java.util.HashMap;

/**
 * Created by nobang on 15. 2. 9..
 */
public class DataTransfer {

    public HashMap<String, Object> hashMap;

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
