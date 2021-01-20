package com.fun;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GlobalVariables {
    private Map<String, String> variables;

    public GlobalVariables () {
       this.variables = new HashMap<>();
    }
}
