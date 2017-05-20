package com.datalex.eventia.converter;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
public interface ConvertService<T, S> {

    S convert(T t);
}
