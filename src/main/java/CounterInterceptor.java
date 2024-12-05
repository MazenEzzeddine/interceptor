import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.HashMap;
import java.util.Map;

public class CounterInterceptor implements
        ProducerInterceptor<String,Customer> {

    public static Map<String, DistributionSummary> topicToDist= new HashMap<>();
    @Override
    public ProducerRecord<String, Customer> onSend
            (ProducerRecord<String, Customer> producerRecord) {
        String topicto = producerRecord.topic();

        DistributionSummary dist  =  topicToDist.get(topicto);

        if(dist == null) {
            DistributionSummary dist1 = DistributionSummary.builder(System.getenv("TOPIC") + topicto)
                    .register(PrometheusUtils.prometheusRegistry);
            topicToDist.put(topicto, dist1);
        }
        dist.record(1.0);

        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}