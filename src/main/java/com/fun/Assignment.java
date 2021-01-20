package com.fun;

import lombok.Data;

@Data
public class Assignment implements Statement{
    private int lineNum;
    private String value;
    private Variable variable;
}
