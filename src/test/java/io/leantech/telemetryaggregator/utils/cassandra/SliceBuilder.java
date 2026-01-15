package io.leantech.telemetryaggregator.utils.cassandra;

import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

public class SliceBuilder<T>{
    private List<T> content = new ArrayList<>();
    private boolean hasNext = false;

    public static <T> SliceBuilder<T> slice(List<T> content) {
        SliceBuilder<T> builder = new SliceBuilder<>();
        builder.content = content;
        return builder;
    }

    public SliceBuilder<T> hasNext(boolean hasNext) {
        this.hasNext = hasNext;
        return this;
    }

    public Slice<T> build() {
        Pageable pageable = PageRequest.of(0, content.size());
        Page<T> page = new PageImpl<>(content, pageable, content.size());
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
