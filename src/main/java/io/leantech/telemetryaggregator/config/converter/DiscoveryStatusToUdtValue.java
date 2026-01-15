package io.leantech.telemetryaggregator.config.converter;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import io.leantech.telemetryaggregator.model.cassandra.udt.DiscoveryStatusUdt;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@RequiredArgsConstructor
@WritingConverter
public class DiscoveryStatusToUdtValue implements Converter<DiscoveryStatusUdt, UdtValue> {
    private final CqlSession session;

    @Override
    public UdtValue convert(DiscoveryStatusUdt source) {
        UserDefinedType udtType = session.getMetadata()
                .getKeyspace(session.getKeyspace().orElseThrow())
                .flatMap(ks -> ks.getUserDefinedType("discovery_status"))
                .orElseThrow(() -> new IllegalStateException("UDT 'discovery_status' not found"));

        return udtType.newValue()
                .setString("agent_version", source.getAgentVersion())
                .setString("connection_domain", source.getConnectionDomain())
                .setLong("in_bitrate", source.getInBitrate())
                .setLong("out_bitrate", source.getOutBitrate())
                .setLong("in_traffic", source.getInTraffic())
                .setLong("out_traffic", source.getOutTraffic())
                .setLong("in_classified_traffic", source.getInClassifiedTraffic())
                .setLong("out_classified_traffic", source.getOutClassifiedTraffic())
                .setInt("ping_to_system", source.getPingToSystem());
    }
}
