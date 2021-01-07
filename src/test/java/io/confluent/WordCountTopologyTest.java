package io.confluent;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordCountTopologyTest {

  @Test
  void testWordCount() {
    final Properties props = new Properties();
    props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-testing");
    props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    final Topology topology = new WordCountTopology().buildTopology();
    final TopologyTestDriver testDriver = new TopologyTestDriver(topology, props);

    var inputTopic = testDriver.createInputTopic(WordCountTopology.WORDCOUNT_INPUT,
        Serdes.String().serializer(), Serdes.String().serializer());

    var outputTopic = testDriver. createOutputTopic(WordCountTopology.WORDCOUNT_OUTPUT,
        Serdes.String().deserializer(), Serdes.Long().deserializer());

    inputTopic.pipeInput("10", "The quick brown fox jumps over the lazy dog");

    var result = outputTopic.readKeyValuesToMap();

    assertEquals(2, result.get("the"));
    assertEquals(1, result.get("fox"));
  }
}