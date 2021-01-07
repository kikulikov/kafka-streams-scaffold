package io.confluent;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Locale;

public class WordCountTopology {

  public static final String WORDCOUNT_INPUT = "streams-plaintext-input";
  public static final String WORDCOUNT_OUTPUT = "streams-wordcount-output";

  public Topology buildTopology() {

    final StreamsBuilder builder = new StreamsBuilder();

    builder.<String, String>stream(WORDCOUNT_INPUT)
        .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
        .groupBy((key, value) -> value)
        .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
        .toStream()
        .to(WORDCOUNT_OUTPUT, Produced.with(Serdes.String(), Serdes.Long()));

    return builder.build();
  }
}
