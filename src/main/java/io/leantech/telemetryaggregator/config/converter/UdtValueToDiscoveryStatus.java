package io.leantech.telemetryaggregator.config.converter;

import com.datastax.oss.driver.api.core.data.UdtValue;
import io.leantech.telemetryaggregator.model.cassandra.udt.DiscoveryStatusUdt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToDiscoveryStatus implements Converter<UdtValue, DiscoveryStatusUdt> {
    @Override
    public DiscoveryStatusUdt convert(UdtValue source) {
        return DiscoveryStatusUdt.builder()
                .agentVersion(source.getString("agent_version"))
                    .connectionDomain(source.getString("connection_domain"))
                    .inBitrate(source.getLong("in_bitrate"))
                    .outBitrate(source.getLong("out_bitrate"))
                    .inTraffic(source.getLong("in_traffic"))
                    .outTraffic(source.getLong("out_traffic"))
                    .inClassifiedTraffic(source.getLong("in_classified_traffic"))
                    .outClassifiedTraffic(source.getLong("out_classified_traffic"))
                    .pingToSystem(source.getInt("ping_to_system"))
                    .build();
    }
}
