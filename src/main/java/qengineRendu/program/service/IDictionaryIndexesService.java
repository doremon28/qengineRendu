package qengineRendu.program.service;

import org.eclipse.rdf4j.model.Statement;
import qengineRendu.program.utils.TypeIndex;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface Dictionary indexes service.
 */
public interface IDictionaryIndexesService {
    /**
     * Search from dictionary by indexes objects string.
     *
     * @param subject   the subject
     * @param predicate the predicate
     * @return the string
     */
    String searchFromDictionaryByIndexesObjects(String subject, String predicate);

    /**
     * Search from dictionary by indexes objects.
     *
     * @param statement      the statement
     * @param res            the res
     * @param isFirstPattern the is first pattern
     */
    void searchFromDictionaryByIndexesObjects(String[] statement, Set<Long> res, boolean isFirstPattern);

    /**
     * Generate indexes.
     *
     * @param typeIndex the type index
     * @param indexes   the indexes
     */
    void generateIndexes(TypeIndex typeIndex, Long[] indexes);

    /**
     * Add entry from statement.
     *
     * @param typeIndex the type index
     * @param st        the st
     */
    void addEntryFromStatement(TypeIndex typeIndex, Statement st);

    /**
     * Gets dictionary.
     *
     * @return the dictionary
     */
    Map<Long, String> getDictionary();

    /**
     * Gets indexes by type.
     *
     * @param typeIndex the type index
     * @return the indexes by type
     */
    Map<Long, Map<Long, Set<Long>>> getIndexesByType(TypeIndex typeIndex);

    /**
     * Count all indexes int.
     *
     * @return the int
     */
    int countAllIndexes();

    /**
     * Decode list of indexes
     */
    Set<String> decodeListOfIndexes(Set<Long> indexes);


}
