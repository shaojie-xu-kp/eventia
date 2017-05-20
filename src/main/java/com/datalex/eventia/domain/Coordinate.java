package com.datalex.eventia.domain;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by shaojie.xu on 19/05/2017.
 */
@Immutable
@Getter
@AllArgsConstructor
public class Coordinate {

    private String longitude;

    private String latitude;

}
