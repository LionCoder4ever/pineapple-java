package com.fun;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchedToken {
    private int lineNum;
    private TokenEnum tokenType;
    private String token;
}
