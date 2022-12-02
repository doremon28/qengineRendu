package qengineRendu.program.service;

import org.eclipse.rdf4j.model.Statement;
import qengineRendu.program.utils.TypeIndex;

import java.util.List;
import java.util.Map;

public interface IDictionaryIndexesService {
    String searchFromDictionaryByIndexesObjects(String subject, String predicate);
    void searchFromDictionaryByIndexesObjects(List<String> statement,List<Long> res, boolean isFirstPattern);
    void generateSPOIndexes(TypeIndex typeIndex, Long[] indexes);
    void generatePOSIndexes(Long[] indexes);
    void generateSOPIndexes(Long[] indexes);

    void addEntryFromStatement(TypeIndex typeIndex, Statement st);
    Map<Long, String> getDictionary();
    Map<Long, Map<Long, List<Long>>> getIndexesByType(TypeIndex typeIndex);


}
