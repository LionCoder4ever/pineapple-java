package com.fun;


import lombok.Data;

@Data
public class Print implements Statement{
    private int lineNum;
    private Variable variable;
}
