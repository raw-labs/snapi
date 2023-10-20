package raw.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RecordFieldsNaming {

  public static Vector<String> makeDistinct(Vector<String> keys) {
    Vector<String> distinct = new Vector<>();
    Map<String, Boolean> keySet = new HashMap<>();
    keys.forEach(k -> keySet.put(k, false));
    // add all keys in the order they appear in the keys vector
    for (String key : keys) {
      String newKey = key;
      if (keySet.get(newKey)) {
        // the key was seen already, find a new key by enumerating other keys.
        int n = 1;
        do {
          newKey = key + '_' + n++;
        } while (keySet.containsKey(newKey));
        keySet.put(newKey, true);
      } else {
        // else, keep the original name
        // but keep track of the fact that we saw it
        keySet.put(newKey, true);
      }
      distinct.add(newKey);
    }
    return distinct;
  }

}
