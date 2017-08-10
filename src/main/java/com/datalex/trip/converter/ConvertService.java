package com.datalex.trip.converter;


public interface ConvertService<T, S> {

    S convert(T t);
}
