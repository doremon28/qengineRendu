package qengineRendu.program.service;

import org.eclipse.rdf4j.model.Statement;
import qengineRendu.program.utils.TypeIndex;

import java.util.List;
import java.util.Map;

public interface IDictionaryIndexesService {
    String searchFromDictionaryByIndexesObjects(String subject, String predicate);
    String searchFromDictionaryByIndexesObjects(List<String> statement);
    void generateSPOIndexes(Long[] indexes);
    void generatePOSIndexes(Long[] indexes);
    void generateSOPIndexes(Long[] indexes);

    void addEntryFromStatement(Statement st);
    Map<Long, String> getDictionary();
    Map<Long, Map<Long, List<Long>>> getIndexesByType(TypeIndex typeIndex);

}
