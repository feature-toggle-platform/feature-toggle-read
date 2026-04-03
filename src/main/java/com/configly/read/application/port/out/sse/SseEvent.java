package com.configly.read.application.port.out.sse;

public record SseEvent(
        String name,
        Object data
) {

    private static final String CONN_CHECK_EVENT_KEY = "CONN_CHECK";
    private static final String REBUILD_REQUIRED_EVENT_KEY = "REBUILD_REQUIRED";

    public static SseEvent create(String name, Object data) {
        return new SseEvent(name, data);
    }

    public static SseEvent connectedEvent() {
        return new SseEvent(CONN_CHECK_EVENT_KEY, true);
    }

    public static SseEvent rebuildRequired(long revision) {
        return new SseEvent(REBUILD_REQUIRED_EVENT_KEY, SseRebuildRequiredEvent.create(revision));
    }
}
