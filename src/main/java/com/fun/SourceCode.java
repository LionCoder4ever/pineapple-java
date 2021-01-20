package com.fun;

import lombok.Data;

import java.util.List;

@Data
public class SourceCode {
    private int lineNum;
    private List<Statement> statements;
}
