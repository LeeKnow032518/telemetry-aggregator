CREATE KEYSPACE IF NOT EXISTS telemetry
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE telemetry;

CREATE TYPE service
    (
        type            text, -- 'SOCIAL', 'MUSIC', 'VPN' è ò.ä.
        domain          text,
        name            text,
        start_time      bigint,
        in_traffic      bigint,
        out_traffic     bigint,
        score           int,
        latency         int,
        service_quality text  -- 'GOOD', 'WARNING', 'BAD'
    );

CREATE TYPE fingerprint
    (
        brand    text,
        hostname text,
        model    text,
        mac      text,
        os_name  text,
        type     text
    );

CREATE TYPE device
    (
        ip           list<text>,
        services     map<text, frozen<service>>,
        fingerprint  frozen<fingerprint>,
        cpu_usage    int,
        memory_usage int
    );

CREATE TYPE discovery_status
    (
        agent_version          text,
        connection_domain      text,
        in_bitrate             bigint,
        out_bitrate            bigint,
        in_traffic             bigint,
        out_traffic            bigint,
        in_classified_traffic  bigint,
        out_classified_traffic bigint,
        ping_to_system         int
    );

CREATE TABLE telemetry_event
(
    event_id         uuid,
    event_hour       text,
    devices          map<text, frozen<device>>,
    discovery_status frozen<discovery_status>,
    event_timestamp  timestamp,
    PRIMARY KEY ((event_hour), event_timestamp, event_id)
) WITH CLUSTERING ORDER BY (event_timestamp DESC, event_id ASC);

