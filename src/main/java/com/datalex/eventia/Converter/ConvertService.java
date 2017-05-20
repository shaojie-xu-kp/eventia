package com.datalex.eventia.Converter;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
public interface ConvertService<T, S> {

    S convert(T t);
}
