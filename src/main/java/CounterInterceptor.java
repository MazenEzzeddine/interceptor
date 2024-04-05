import io.micrometer.core.instrument.Gauge;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.HashMap;
import java.util.Map;

public class CounterInterceptor implements
        ProducerInterceptor<String,Customer> {

    public static Map<String, Gauge> topicToGauge = new HashMap<>();
    public static Map<String, CountMeasure> topicToMeasure = new HashMap<>();
    public static Map<String, Double> topicToCount = new HashMap<>();


    @Override
    public ProducerRecord<String, Customer> onSend
            (ProducerRecord<String, Customer> producerRecord) {
        String topicto = producerRecord.topic();
        Gauge gauge=  topicToGauge.get(topicto);
        if(gauge == null) {
            CountMeasure measure = new CountMeasure(0.0);
            Gauge gauge1 = Gauge.builder(CountConsumerInterceptor.inputtopic+topicto,
                            measure, CountMeasure::getCount)
                    //.tag("topicFrom", CountConsumerInterceptor.inputtopic + "i")
                    .register(PrometheusUtils.prometheusRegistry);

            topicToGauge.put(topicto, gauge1);
            topicToMeasure.put(topicto, measure);
            topicToCount.put(topicto, 0.0);
        }else {
            topicToCount.put(topicto,topicToCount.get(topicto)+ 1);
        }
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