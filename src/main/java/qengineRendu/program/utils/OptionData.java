package qengineRendu.program.utils;

public class OptionData {
    public static final String QUERIES_OPT = "q";
    public static final String DATA_OPT = "d";
    public static final String OUTPUT_OPT = "o";
    public static final String JENA_ACTIVATION_OPT = "j";
    public static final String WARM_OPT = "w";
    public static final String SHUFFLE_OPT = "s";
    public static final String EXPORT_QUERY_RESULT_OPT = "e";

    public static final String QUERIES_LONG_OPT = "queries";
    public static final String DATA_LONG_OPT = "data";
    public static final String OUTPUT_LONG_OPT = "output";
    public static final String JENA_ACTIVATION_LONG_OPT = "jena";
    public static final String WARM_LONG_OPT = "warm";
    public static final String SHUFFLE_LONG_OPT = "shuffle";
    public static final String EXPORT_QUERY_RESULT_LONG_OPT = "export-query-result";

    public static final String QUERIES_DESC = "chemin vers dossier requetes";
    public static final String DATA_DESC = "chemin vers fichier donnees";
    public static final String OUTPUT_DESC = "chemin vers dossier sortie";
    public static final String JENA_ACTIVATION_DESC = "active la vérification de la correction et complétude du système\n" +
            "en utilisant Jena comme un oracle";
    public static final String WARM_DESC = "utilise un échantillon des requêtes en entrée (prises au hasard) correspondant au pourcentage X pour chauffer le système";
    public static final String SHUFFLE_DESC = "considère une permutation aléatoire des requêtes en entrée";
    public static final String EXPORT_QUERY_RESULT_DESC = "exporte les résultats des requêtes dans un fichier";
    private OptionData () {}
}
