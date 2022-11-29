package qengineRendu.program.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.eclipse.rdf4j.model.Statement;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * The Dictionary class.
 */
public class Dictionary {
    /**
     * The constant dictionaryMap.
     */
    /*
    * TODO:
    *  * Transform this Map to biMap and work directly with it
     */
    private static final BiMap<Long, String> dictionaryMap = HashBiMap.create();
    /**
     * The constant SPOindexes.
     */

    /*
    * TODO
    *  * Change the data structure to map<subject,map<predicat,List<Object>> HashMap
     */
    private static final Map<Long, Map<Long, List<Long>>> SPOindexes = new HashMap<>();

    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Dictionary.class);

    /**
     * Add entry from statement.
     *
     * @param st the st
     */
    public void addEntryFromStatement(Statement st) {
        Long [] tripletSPO = new Long[3];
        tripletSPO[0] = (addEntry(st.getSubject().stringValue()));
        tripletSPO[1] = (addEntry(st.getPredicate().stringValue()));
        tripletSPO[2] = (addEntry(st.getObject().stringValue()));
        generateIndexes(tripletSPO);

        // System.out.println(indexes);
    }

    /**
     * Generate indexes.
     *
     * @param indexes the indexes
     */
    private void generateIndexes(Long[] indexes) {
        SPOindexes.putIfAbsent(indexes[0], new HashMap<>());
        SPOindexes.get(indexes[0]).putIfAbsent(indexes[1], new ArrayList<>());
        SPOindexes.get(indexes[0]).get(indexes[1]).add(indexes[2]);
    }

    private String decode(Long index) {
        return dictionaryMap.get(index);
    }

    private Long encode(String value) {
        return dictionaryMap.inverse().get(value);
    }

    public String searchFromDictionaryByIndexesObjects(String subject, String predicate) {
        Long subjectIndex = encode(subject);
        Long predicateIndex = encode(predicate);
        List<Long> objects = SPOindexes.get(subjectIndex).get(predicateIndex);
        StringBuilder result = new StringBuilder();
        objects.forEach(object -> result.append(decode(object)).append(" "));
        return result.toString();
    }

    /**
     * Add single entry subject | predict | object.
     *
     * @param stringValue the string value
     * @return the long
     */
    private Long addEntry(String stringValue) {
        LongAdder longAdder = new LongAdder();
        longAdder.add(dictionaryMap.size());
        longAdder.increment();
        try {
            dictionaryMap.put(longAdder.longValue(), stringValue);
            return longAdder.longValue();
        } catch (IllegalArgumentException ex) {
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


    /**
     * Gets key from value.
     *
     * @param value the value
     * @return the key from value
     */
    private Long getKeyFromValue(String value) {
        return dictionaryMap.inverse().get(value);
    }

    public Map<Long, Map<Long, List<Long>>> getSPOindexes() {
        return SPOindexes;
    }


}
