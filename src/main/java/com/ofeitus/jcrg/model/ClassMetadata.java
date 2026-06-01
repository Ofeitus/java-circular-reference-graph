package com.ofeitus.jcrg.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClassMetadata {

    @EqualsAndHashCode.Include
    private final String fullName;
}
