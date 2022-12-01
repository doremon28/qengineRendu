package qengineRendu.program.service.impl;

import org.eclipse.rdf4j.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.utils.Dictionary;
import qengineRendu.program.utils.Index;
import qengineRendu.program.utils.TypeIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryIndexesServiceImpl implements IDictionaryIndexesService {
    Logger logger = LoggerFactory.getLogger(DictionaryIndexesServiceImpl.class);
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
    public String searchFromDictionaryByIndexesObjects(List<String> statement) {
        if(statement.size() != 3){
            return null;
        }
        Map<Long, Map<Long, List<Long>>> resIndex;
        StringBuilder result = new StringBuilder();

        if(statement.get(0) != null && statement.get(1) != null && statement.get(2) == null){
            resIndex = index.getIndexesByType(TypeIndex.SPO);
            Long subjectIndex = dictionary.encode(statement.get(0));
            Long predicateIndex = dictionary.encode(statement.get(1));
            List<Long> objects = resIndex.get(subjectIndex).get(predicateIndex);
            if(objects != null){
                objects.forEach(object -> result.append(dictionary.decode(object)).append(" "));
            }else{
                logger.error("No objects found for subject+"+statement.get(0)+" and predicate "+statement.get(1));
            }

        }else if(statement.get(0) != null && statement.get(1) == null && statement.get(2) != null) {
            resIndex = index.getIndexesByType(TypeIndex.SOP);
            Long subjectIndex = dictionary.encode(statement.get(0));
            Long objectIndex = dictionary.encode(statement.get(2));
            List<Long> predicates = resIndex.get(subjectIndex).get(objectIndex);
            if(predicates != null){
                predicates.forEach(predicate -> result.append(dictionary.decode(predicate)).append("\n"));
            }else{
                logger.error("No predicates found for subject "+statement.get(0)+" and object "+ statement.get(2));
            }

        }else if(statement.get(0) == null && statement.get(1) != null && statement.get(2) != null) {
            resIndex = index.getIndexesByType(TypeIndex.POS);
            Long predicateIndex = dictionary.encode(statement.get(1));
            Long objectIndex = dictionary.encode(statement.get(2));
            List<Long> subjects = resIndex.get(predicateIndex).get(objectIndex);
            if(subjects != null) {
                subjects.forEach(subject -> result.append(dictionary.decode(subject)).append("\n"));
            }else{
                logger.error("No subjects found for predicate " + statement.get(1) + " and object " + statement.get(2));
            }

        }
        return result.toString();
    }

//    public Map<Long, Map<Long, List<Long>>> getMapIndex(List<String> statement){
//        Map<Long, Map<Long, List<Long>>> resIndex = new HashMap<>();
//        if(statement.size() != 3){
//            return null;
//        }
//        if(statement.get(0) != null && statement.get(1) != null && statement.get(2) == null){
//            resIndex = index.getIndexesByType(TypeIndex.SPO);
//        }else if(statement.get(0) != null && statement.get(1) == null && statement.get(2) != null) {
//            resIndex = index.getIndexesByType(TypeIndex.SOP);
//        }else if(statement.get(0) == null && statement.get(1) != null && statement.get(2) != null) {
//            resIndex = index.getIndexesByType(TypeIndex.POS);
//        }
//        return resIndex;
//    }


    @Override
    public void generateSPOIndexes(TypeIndex typeIndex, Long[] indexes) {
        switch (typeIndex){
            case SPO:
                index.getIndexesByType(TypeIndex.SPO).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).putIfAbsent(indexes[1], new ArrayList<>());
                index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
            case SOP:
                index.getIndexesByType(TypeIndex.SOP).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.SOP).get(indexes[0]).putIfAbsent(indexes[1], new ArrayList<>());
                index.getIndexesByType(TypeIndex.SOP).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
            case POS:
                index.getIndexesByType(TypeIndex.POS).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.POS).get(indexes[0]).putIfAbsent(indexes[1], new ArrayList<>());
                index.getIndexesByType(TypeIndex.POS).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
        }
    }

    @Override
    public void generatePOSIndexes(Long[] indexes) {

    }

    @Override
    public void generateSOPIndexes(Long[] indexes) {

    }

    @Override
    public void addEntryFromStatement(TypeIndex typeIndex, Statement st) {
        Long [] triplet = new Long[3];
//        tripletSPO[0] = (dictionary.addEntry(st.getSubject().stringValue()));
//        tripletSPO[1] = (dictionary.addEntry(st.getPredicate().stringValue()));
//        tripletSPO[2] = (dictionary.addEntry(st.getObject().stringValue()));
        triplet = addEntryFromStatementDependingToType(typeIndex, st);
        generateSPOIndexes(typeIndex, triplet);
    }


    public Long[] addEntryFromStatementDependingToType(TypeIndex typeIndex, Statement st) {
        switch (typeIndex){
            case SPO:
                Long [] tripletSPO = new Long[3];
                tripletSPO[0] = (dictionary.addEntry(st.getSubject().stringValue()));
                tripletSPO[1] = (dictionary.addEntry(st.getPredicate().stringValue()));
                tripletSPO[2] = (dictionary.addEntry(st.getObject().stringValue()));
                return tripletSPO;
            case POS:
                Long [] tripletPOS = new Long[3];
                tripletPOS[0] = (dictionary.addEntry(st.getPredicate().stringValue()));
                tripletPOS[1] = (dictionary.addEntry(st.getObject().stringValue()));
                tripletPOS[2] = (dictionary.addEntry(st.getSubject().stringValue()));
                return tripletPOS;
            case SOP:
                Long [] tripletSOP = new Long[3];
                tripletSOP[0] = (dictionary.addEntry(st.getSubject().stringValue()));
                tripletSOP[1] = (dictionary.addEntry(st.getObject().stringValue()));
                tripletSOP[2] = (dictionary.addEntry(st.getPredicate().stringValue()));
                return tripletSOP;
            default:
                return null;
        }
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
