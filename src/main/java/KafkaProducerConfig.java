import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Properties;
import java.util.StringTokenizer;


public class KafkaProducerConfig {
    private static final Logger log = LogManager.getLogger(KafkaProducerConfig.class);

    private final String bootstrapServers;
    private final int delay;
    private final String acks;
    private final String additionalConfig;
    public KafkaProducerConfig(String bootstrapServers,
                               int delay,
                               String acks, String additionalConfig) {
        this.bootstrapServers = bootstrapServers;
        this.delay = delay;

        this.acks = acks;
        //this.headers = headers;
        this.additionalConfig = additionalConfig;
    }
    public static KafkaProducerConfig fromEnv() {
        String bootstrapServers = "my-cluster-kafka-bootstrap:9092";//System.getenv("BOOTSTRAP_SERVERS");
        int delay = 1;
        String acks = System.getenv().getOrDefault("PRODUCER_ACKS", "1");
        //String headers = System.getenv("HEADERS");
        String additionalConfig = System.getenv().getOrDefault("ADDITIONAL_CONFIG", "");
        return new KafkaProducerConfig(bootstrapServers, delay,
                acks, additionalConfig);
    }

    public static Properties createProperties(KafkaProducerConfig config) {
        log.info("==================================================");
        log.info("Creating Properties");
        log.info("==================================================");
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        //props.put(ProducerConfig.ACKS_CONFIG, config.getAcks());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "0");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "0");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
               CustomerSerializer.class.getName());


        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, Collections.singletonList(CounterInterceptor.class));
        //props.put(ProducerConfig., "org.apache.kafka.common.serialization.StringSerializer");
        if (!config.getAdditionalConfig().isEmpty()) {
            StringTokenizer tok =
                    new StringTokenizer(config.getAdditionalConfig(), ", \t\n\r");
            while (tok.hasMoreTokens()) {
                String record = tok.nextToken();
                int endIndex = record.indexOf('=');
                if (endIndex == -1) {
                    throw new RuntimeException("Failed to parse Map from String");
                }
                String key = record.substring(0, endIndex);
                String value = record.substring(endIndex + 1);
                props.put(key.trim(), value.trim());
            }
        }
        return props;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }





    public String getAdditionalConfig() {
        return additionalConfig;
    }
    @Override
    public String toString() {
        return "KafkaProducerConfig{" +
            "bootstrapServers='" + bootstrapServers + '\'' +
            ", delay=" + delay +
            ", acks='" + acks + '\'' +
            ", additionalConfig='" + additionalConfig + '\'' +
            '}';
    }
}
