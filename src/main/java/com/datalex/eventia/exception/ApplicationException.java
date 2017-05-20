package com.datalex.eventia.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by shaojie.xu on 20/05/2017.
 */
@Setter
@Getter
@Data
@AllArgsConstructor
public class ApplicationException extends RuntimeException{

    private String error;

}
