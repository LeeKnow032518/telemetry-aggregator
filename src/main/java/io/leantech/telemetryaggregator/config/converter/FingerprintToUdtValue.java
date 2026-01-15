package io.leantech.telemetryaggregator.config.converter;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import io.leantech.telemetryaggregator.model.cassandra.udt.FingerprintUdt;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@RequiredArgsConstructor
@WritingConverter
public class FingerprintToUdtValue implements Converter<FingerprintUdt, UdtValue> {
    private final CqlSession session;

    @Override
    public UdtValue convert(FingerprintUdt source) {
        UserDefinedType udtType = session.getMetadata()
                .getKeyspace(session.getKeyspace().orElseThrow())
                .flatMap(ks -> ks.getUserDefinedType("fingerprint"))
                .orElseThrow(() -> new IllegalStateException("UDT 'fingerprint' not found"));

        return udtType.newValue()
                .setString("brand", source.getBrand())
                .setString("model", source.getModel())
                .setString("hostname", source.getHostname())
                .setString("os_name", source.getOsName())
                .setString("mac", source.getMac())
                .setString("type", source.getType());
    }
}

