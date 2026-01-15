CREATE TABLE IF NOT EXISTS aggregated_router_stats (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    domain TEXT NOT NULL,
    hostname TEXT NOT NULL,
    total_in_traffic BIGINT NOT NULL,
    total_out_traffic BIGINT NOT NULL,
    avg_score DOUBLE PRECISION,
    avg_latency DOUBLE PRECISION,
    unique_devices INTEGER,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS aggregated_device_stats (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    ip TEXT NOT NULL,
    brand TEXT,
    model TEXT,
    os_name TEXT,
    cpu_usage_avg DOUBLE PRECISION,
    memory_usage_avg DOUBLE PRECISION,
    services_count INTEGER,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS aggregated_service_stats (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    service_type TEXT NOT NULL,
    domain TEXT NOT NULL,
    name TEXT NOT NULL,
    avg_latency DOUBLE PRECISION,
    avg_score DOUBLE PRECISION,
    total_in_traffic BIGINT,
    total_out_traffic BIGINT,
    quality_counts TEXT,
    created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_router_stats_time ON aggregated_router_stats (timestamp);
CREATE INDEX IF NOT EXISTS idx_device_stats_time ON aggregated_device_stats (timestamp);
CREATE INDEX IF NOT EXISTS idx_service_stats_time ON aggregated_service_stats (timestamp);

CREATE INDEX IF NOT EXISTS idx_router_domain_hostname ON aggregated_router_stats (domain, hostname);
CREATE INDEX IF NOT EXISTS idx_device_ip ON aggregated_device_stats (ip);
CREATE INDEX IF NOT EXISTS idx_service_type_domain_name ON aggregated_service_stats (service_type, domain, name);