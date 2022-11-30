package qengineRendu.program.service.impl;

import org.eclipse.rdf4j.model.Statement;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.utils.Dictionary;
import qengineRendu.program.utils.Index;
import qengineRendu.program.utils.TypeIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryIndexesServiceImpl implements IDictionaryIndexesService {
    Dictionary dictionary = new Dictionary();
    Index index = new Index();

    @Override
    public String searchFromDictionaryByIndexesObjects(String subject, String predicate) {
        Long subjectIndex = dictionary.encode(subject);
        Long predicateIndex = dictionary.encode(predicate);
        List<Long> objects = index.getIndexesByType(TypeIndex.SPO).get(subjectIndex).get(predicateIndex);
        StringBuilder result = new StringBuilder();
        objects.forEach(object -> result.append(dictionary.decode(object)).append(" "));
        return result.toString();
    }

    @Override
    public void generateIndexes(Long[] indexes) {
        index.getIndexesByType(TypeIndex.SPO).putIfAbsent(indexes[0], new HashMap<>());
        index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).putIfAbsent(indexes[1], new ArrayList<>());
        index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).get(indexes[1]).add(indexes[2]);
    }
    @Override
    public void addEntryFromStatement(Statement st) {
        Long [] tripletSPO = new Long[3];
        tripletSPO[0] = (dictionary.addEntry(st.getSubject().stringValue()));
        tripletSPO[1] = (dictionary.addEntry(st.getPredicate().stringValue()));
        tripletSPO[2] = (dictionary.addEntry(st.getObject().stringValue()));
        generateIndexes(tripletSPO);
    }

    @Override
    public Map<Long, String> getDictionary() {
        return dictionary.getDictionary();
    }

    @Override
    public Map<Long, Map<Long, List<Long>>> getIndexesByType(TypeIndex typeIndex) {
        return index.getIndexesByType(typeIndex);
    }
}
