package com.configly.read.application.port.out.sse;

record SseRebuildRequiredEvent(
        long revision
) {

    static SseRebuildRequiredEvent create(long revision) {
        return new SseRebuildRequiredEvent(revision);
    }

}
