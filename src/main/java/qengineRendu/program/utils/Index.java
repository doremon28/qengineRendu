package qengineRendu.program.utils;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Index.class);
    /**
     * The constant SPOindexes.
     */
    private static final Map<Long, Map<Long, List<Long>>> SPOindexes = new HashMap<>();

    private static final Map<Long, Map<Long, List<Long>>> POSindexes = new HashMap<>();

    private static final Map<Long, Map<Long, List<Long>>> OSPindexes = new HashMap<>();

    private static final Map<Long, Map<Long, List<Long>>> PSOindexes = new HashMap<>();

    private static final Map<Long, Map<Long, List<Long>>> OPSindexes = new HashMap<>();

    private static final Map<Long, Map<Long, List<Long>>> SOPindexes = new HashMap<>();

    public Map<Long, Map<Long, List<Long>>> getIndexesByType(TypeIndex typeIndex) {
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

}
