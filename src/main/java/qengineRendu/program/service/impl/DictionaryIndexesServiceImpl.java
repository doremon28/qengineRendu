package qengineRendu.program.service.impl;

import org.eclipse.rdf4j.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.utils.*;
import qengineRendu.program.utils.Dictionary;

import java.util.*;

/**
 * The type Dictionary indexes service.
 */
public class DictionaryIndexesServiceImpl implements IDictionaryIndexesService {
    Logger logger = LoggerFactory.getLogger(DictionaryIndexesServiceImpl.class);
    Dictionary dictionary = new Dictionary();
    Index index = new Index();

    @Override
    public String searchFromDictionaryByIndexesObjects(String subject, String predicate) {
        Long subjectIndex = dictionary.encode(subject);
        Long predicateIndex = dictionary.encode(predicate);
        Set<Long> objects = index.getIndexesByType(TypeIndex.SPO).get(subjectIndex).get(predicateIndex);
        StringBuilder result = new StringBuilder();
        objects.forEach(object -> result.append(dictionary.decode(object)).append(" "));
        return result.toString();
    }

    @Override
    public void searchFromDictionaryByIndexesObjects(String[] statement, Set<Long> res, boolean isFirstPattern) {
        if (statement.length != 3) {
            return;
        }
        Map<Long, Map<Long, Set<Long>>> resIndex;
        if (statement[0] != null && statement[1] != null && statement[2] == null) {
            resIndex = index.getIndexesByType(TypeIndex.SPO);
            Long subjectIndex = dictionary.encode(statement[0]);
            getFromIndex(statement, res, isFirstPattern,resIndex, subjectIndex);
        } else if (statement[0] != null && statement[1] == null && statement[2] != null) {
            resIndex = index.getIndexesByType(TypeIndex.SOP);
            Long subjectIndex = dictionary.encode(statement[0]);
            getFromIndex(statement, res, isFirstPattern, resIndex, subjectIndex);

        } else if (statement[0] == null && statement[1] != null && statement[2] != null) {
            resIndex = index.getIndexesByType(TypeIndex.POS);
            Long predicateIndex = dictionary.encode(statement[1]);
            getFromIndex(statement, res, isFirstPattern, resIndex, predicateIndex);
        }
    }

    /**
     * Gets from index.
     *
     * @param statement      the statement
     * @param res            the res
     * @param isFirstPattern the is first pattern
     * @param resIndex       the res index
     * @param subjectIndex   the subject index
     */
    private void getFromIndex(String[] statement, Set<Long> res, boolean isFirstPattern,
                              Map<Long, Map<Long, Set<Long>>> resIndex, Long subjectIndex) {
        Long objectIndex = dictionary.encode(statement[2]);
        Set<Long> predicates = resIndex.get(subjectIndex).get(objectIndex);
        if (predicates != null && !predicates.isEmpty()) {
            if (isFirstPattern) {
                res.addAll(predicates);
            } else {
                res.retainAll(predicates);
            }
        } else {
            StatisticQuery.incrementQueriesNumberWithoutResponsesByOne();
            res.clear();
        }
    }
    @Override
    public void generateIndexes(TypeIndex typeIndex, Long[] indexes) {
        switch (typeIndex) {
            case SPO:
                index.getIndexesByType(TypeIndex.SPO).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).putIfAbsent(indexes[1], new HashSet<>());
                index.getIndexesByType(TypeIndex.SPO).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
            case SOP:
                index.getIndexesByType(TypeIndex.SOP).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.SOP).get(indexes[0]).putIfAbsent(indexes[1], new HashSet<>());
                index.getIndexesByType(TypeIndex.SOP).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
            case POS:
                index.getIndexesByType(TypeIndex.POS).putIfAbsent(indexes[0], new HashMap<>());
                index.getIndexesByType(TypeIndex.POS).get(indexes[0]).putIfAbsent(indexes[1], new HashSet<>());
                index.getIndexesByType(TypeIndex.POS).get(indexes[0]).get(indexes[1]).add(indexes[2]);
                break;
            default:
                logger.error("TypeIndex not found or not implemented");
                break;
        }
    }

    @Override
    public void addEntryFromStatement(TypeIndex typeIndex, Statement st) {
        long startDocCreation = System.nanoTime();
        Long[] triplet = addEntryFromStatementDependingToType(typeIndex, st);
        long endDocCreation = System.nanoTime();
        StatisticData.creatingDictionary = StatisticData.creatingDictionary + (endDocCreation - startDocCreation) / 1_000_000.0;
        long startIndexCreation = System.nanoTime();
        generateIndexes(typeIndex, triplet);
        long endIndexCreation = System.nanoTime();
        StatisticData.creatingIndexes = StatisticData.creatingIndexes + (endIndexCreation - startIndexCreation) / 1_000_000.0;
    }


    public Long[] addEntryFromStatementDependingToType(TypeIndex typeIndex, Statement st) {
        switch (typeIndex) {
            case SPO:
                Long[] tripletSPO = new Long[3];
                tripletSPO[0] = (dictionary.addEntry(st.getSubject().stringValue()));
                tripletSPO[1] = (dictionary.addEntry(st.getPredicate().stringValue()));
                tripletSPO[2] = (dictionary.addEntry(st.getObject().stringValue()));
                return tripletSPO;
            case POS:
                Long[] tripletPOS = new Long[3];
                tripletPOS[0] = (dictionary.addEntry(st.getPredicate().stringValue()));
                tripletPOS[1] = (dictionary.addEntry(st.getObject().stringValue()));
                tripletPOS[2] = (dictionary.addEntry(st.getSubject().stringValue()));
                return tripletPOS;
            case SOP:
                Long[] tripletSOP = new Long[3];
                tripletSOP[0] = (dictionary.addEntry(st.getSubject().stringValue()));
                tripletSOP[1] = (dictionary.addEntry(st.getObject().stringValue()));
                tripletSOP[2] = (dictionary.addEntry(st.getPredicate().stringValue()));
                return tripletSOP;
            default:
                logger.error("TypeIndex not found or not implemented");
                return new Long[0];
        }
    }

    @Override
    public Map<Long, String> getDictionary() {
        return dictionary.getDictionary();
    }

    @Override
    public Map<Long, Map<Long, Set<Long>>> getIndexesByType(TypeIndex typeIndex) {
        return index.getIndexesByType(typeIndex);
    }

    @Override
    public int countAllIndexes() {
        return index.countAllIndexes();
    }
}
