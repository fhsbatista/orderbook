package com.fhsbatista.orderbook;

import java.util.UUID;

public class UUIDGeneratorUtilAdapter implements UUIDGenerator {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
