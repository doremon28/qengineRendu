package qengineRendu.program.utils;

import org.slf4j.Logger;

import java.util.*;

/**
 * The type Index.
 */
public class Index {

    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Index.class);
    /**
     * The constant SPOindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> SPOindexes = new HashMap<>();

    /**
     * The constant POSindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> POSindexes = new HashMap<>();

    /**
     * The constant OSPindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> OSPindexes = new HashMap<>();

    /**
     * The constant PSOindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> PSOindexes = new HashMap<>();

    /**
     * The constant OPSindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> OPSindexes = new HashMap<>();

    /**
     * The constant SOPindexes.
     */
    private static final Map<Long, Map<Long, Set<Long>>> SOPindexes = new HashMap<>();

    /**
     * Gets indexes by type.
     *
     * @param typeIndex the type index
     * @return the indexes by type
     */
    public Map<Long, Map<Long, Set<Long>>> getIndexesByType(TypeIndex typeIndex) {
        switch (typeIndex) {
            case SPO:
                return SPOindexes;
            case POS:
                return POSindexes;
            case OSP:
                return OSPindexes;
            case PSO:
                return PSOindexes;
            case OPS:
                return OPSindexes;
            case SOP:
                return SOPindexes;
            default:
                logger.error("TypeIndex not found");
                return Collections.emptyMap();
        }
    }

    /**
     * Count all indexes.
     *
     * @return the int
     */
    public int countAllIndexes() {
        return countIndexesByType(TypeIndex.SPO) + countIndexesByType(TypeIndex.POS) + countIndexesByType(TypeIndex.OSP) + countIndexesByType(TypeIndex.PSO) + countIndexesByType(TypeIndex.OPS) + countIndexesByType(TypeIndex.SOP);
    }

    /**
     * Count indexes by type.
     *
     * @param typeIndex the type index
     * @return the int
     */
    private int countIndexesByType(TypeIndex typeIndex) {
        int count = 0;
        for (Map.Entry<Long, Map<Long, Set<Long>>> entry : getIndexesByType(typeIndex).entrySet()) {
            count++;
            for (Map.Entry<Long, Set<Long>> entry1 : entry.getValue().entrySet()) {
                count++;
                count += entry1.getValue().size();
            }
        }
        return count;
    }

}
