package io.github.jeff_zou.proxy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @Author: Jeff Zou @Date: 2022/10/21 13:46
 */
public class FlinkProxyServerMainOptions {
    public static final Option BOOTSTRAP_SERVERS_OPTIONS =
            new Option("bootstrapservers", "bootstrapservers", true, "the list of kafka servers");

    public static final Option KAFKA_USER_OPTIONS =
            new Option(
                    "kafkauser",
                    "kafkauser",
                    true,
                    "the user which can produce message to topic:flink_proxy_request and consumer message from topic:flink_proxy_result");

    public static final Option KAFKA_PASSWORD_OPTIONS =
            new Option("kafkapassword", "kafkapassword", true, "");

    static {
        BOOTSTRAP_SERVERS_OPTIONS.setRequired(true);
        KAFKA_USER_OPTIONS.setRequired(true);
        KAFKA_PASSWORD_OPTIONS.setRequired(true);
    }

    private static Options getRunCommandOptions() {
        return new Options()
                .addOption(BOOTSTRAP_SERVERS_OPTIONS)
                .addOption(KAFKA_USER_OPTIONS)
                .addOption(KAFKA_PASSWORD_OPTIONS);
    }

    public static CommandLine parse(String[] args) throws Exception {
        DefaultParser parser = new DefaultParser();
        try {
            return parser.parse(getRunCommandOptions(), args, true);
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setLeftPadding(5);
            formatter.setWidth(80);
            formatter.printHelp(" ", getRunCommandOptions());
            System.out.println();
            throw new Exception();
        }
    }
}
