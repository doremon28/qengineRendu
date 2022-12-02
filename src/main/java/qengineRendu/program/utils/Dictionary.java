package qengineRendu.program.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * The Dictionary class.
 */
public class Dictionary {
    /**
     * The constant dictionaryMap.
     **/
    private static final BiMap<Long, String> dictionaryMap = HashBiMap.create();


    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Dictionary.class);


    public String decode(Long index) {
        return dictionaryMap.get(index);
    }

    public Long encode(String value) {
        return dictionaryMap.inverse().get(value);
    }

    public List<String> decodeList(HashSet<Long> indexes) {
        List<String> result = new ArrayList<>();
        indexes.forEach(index -> result.add(dictionaryMap.get(index)));
        return result;
    }

    /**
     * Add single entry subject | predict | object.
     *
     * @param stringValue the string value
     * @return the long
     */
    public Long addEntry(String stringValue) {
        LongAdder longAdder = new LongAdder();
        longAdder.add(dictionaryMap.size());
        longAdder.increment();
        if (!ifValueExist(stringValue)) {
            dictionaryMap.put(longAdder.longValue(), stringValue);
            return longAdder.longValue();
        } else {
            return getKeyFromValue(stringValue);
        }
    }

    /**
     * Gets dictionary.
     *
     * @return the dictionary
     */
    public Map<Long, String> getDictionary() {
        return dictionaryMap;
    }

    public boolean ifValueExist(String value) {
        return dictionaryMap.inverse().get(value) != null;
    }

    /**
     * Gets key from value.
     *
     * @param value the value
     * @return the key from value
     */
    private Long getKeyFromValue(String value) {
        return dictionaryMap.inverse().get(value);
    }




}
