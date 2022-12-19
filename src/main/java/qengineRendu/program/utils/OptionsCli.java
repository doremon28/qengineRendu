package qengineRendu.program.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;

/**
 * The type Options cli.
 */
public class OptionsCli {
    /**
     * The constant logger.
     */
    private static final  Logger logger = org.slf4j.LoggerFactory.getLogger(OptionsCli.class);
    /**
     * The Options.
     */
    private final Options options;

    /**
     * Instantiates a new Options cli.
     */
    public OptionsCli() {
        Option queries = new Option(OptionData.QUERIES_OPT, OptionData.QUERIES_LONG_OPT, true, OptionData.QUERIES_DESC);
        queries.setRequired(true);
        Option data = new Option(OptionData.DATA_OPT, OptionData.DATA_LONG_OPT, true, OptionData.DATA_DESC);
        data.setRequired(true);
        Option output = new Option(OptionData.OUTPUT_OPT, OptionData.OUTPUT_LONG_OPT, true, OptionData.OUTPUT_DESC);
        output.setRequired(true);
        Option jenaActivation = new Option(OptionData.JENA_ACTIVATION_OPT, OptionData.JENA_ACTIVATION_LONG_OPT, true, OptionData.JENA_ACTIVATION_DESC);
        jenaActivation.setRequired(true);
        Option warm = new Option(OptionData.WARM_OPT, OptionData.WARM_LONG_OPT, true, OptionData.WARM_DESC);
        warm.setRequired(false);
        Option shuffle = new Option(OptionData.SHUFFLE_OPT, OptionData.SHUFFLE_LONG_OPT, true, OptionData.SHUFFLE_DESC);
        shuffle.setRequired(false);
        this.options = new Options();
        options.addOption(queries);
        options.addOption(data);
        options.addOption(output);
        options.addOption(jenaActivation);
        options.addOption(warm);
        options.addOption(shuffle);
    }

    /**
     * Gets options.
     *
     * @return the options
     */
    public Options getOptions() {
        if (options == null) {
            logger.info("Options is null");
            OptionsCli optionsCli = new OptionsCli();
            logger.info("Options are created");
            return optionsCli.getOptions();
        } else {
            return options;
        }
    }

}
