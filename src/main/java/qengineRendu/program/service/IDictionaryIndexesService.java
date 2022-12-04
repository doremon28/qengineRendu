package qengineRendu.program.service;

import org.eclipse.rdf4j.model.Statement;
import qengineRendu.program.utils.TypeIndex;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDictionaryIndexesService {
    String searchFromDictionaryByIndexesObjects(String subject, String predicate);
    void searchFromDictionaryByIndexesObjects(String[] statement, Set<Long> res, boolean isFirstPattern);
    void generateSPOIndexes(TypeIndex typeIndex, Long[] indexes);
    void addEntryFromStatement(TypeIndex typeIndex, Statement st);
    Map<Long, String> getDictionary();
    Map<Long, Map<Long, Set<Long>>> getIndexesByType(TypeIndex typeIndex);


}
