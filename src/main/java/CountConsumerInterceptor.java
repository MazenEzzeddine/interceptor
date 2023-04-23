import io.micrometer.core.instrument.Gauge;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

public class CountConsumerInterceptor implements
        ConsumerInterceptor<String, Customer> {

   public static String inputtopic;
    public static CountMeasure measure;
    public static Gauge gauge1;

    static {
        inputtopic = System.getenv("TOPIC");
    }

    public CountConsumerInterceptor() {
        measure = new CountMeasure(0.0);
        gauge1 = Gauge.builder(inputtopic + "Total", measure,
                        CountMeasure::getCount)
               // .tag("topicTo", "NA")
                .register(PrometheusUtils.prometheusRegistry);
    }

    @Override
    public ConsumerRecords<String, Customer> onConsume
            (ConsumerRecords<String, Customer> consumerRecords) {

       for (Map.Entry<String, Double> e: CounterInterceptor.topicToCount.entrySet()) {
           CounterInterceptor.topicToMeasure.get(e.getKey()).setCount(e.getValue()/ measure.getCount());
       }
        measure.setCount(consumerRecords.count());

        for (Map.Entry<String, Double> e: CounterInterceptor.topicToCount.entrySet()) {
            CounterInterceptor.topicToCount.put(e.getKey(),0.0);
        }
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
    }

    @Override
    public void close() {

    }
    @Override
    public void configure(Map<String, ?> map) {

    }
}