package com.fun;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class Backend {
    private Parser parser;
    
    public Backend() {
        this.parser = new Parser();
    }

    public void execute(String code) {
        GlobalVariables g = new GlobalVariables();
        SourceCode ast = this.parser.parse(code);
        resolveAst(g,ast);
    }

    private void resolveAst(GlobalVariables g, SourceCode ast) {
        if (ast.getStatements().size() == 0) {
            log.error("resolveAST error");
            System.exit(1);
        }
        for (Statement statement: ast.getStatements()
             ) {
           resolveStatement(g, statement);
        }
    }

    private void resolveStatement(GlobalVariables g, Statement statement) {
        if (statement instanceof Assignment) {
            resolveAssignment(g, (Assignment) statement);
        } else if (statement instanceof Print) {
            resolvePrint(g, (Print) statement);
        } else {
            log.error("undefined statement type");
            System.exit(0);
        }
    }

    private void resolvePrint(GlobalVariables g, Print print) {
        String variableName = print.getVariable().getName();
        if (Objects.isNull(variableName) || Objects.equals("", variableName)) {
            log.error("resolve print error");
            System.exit(0);
        }
        String variableValue = g.getVariables().get(variableName);
        log.info(variableValue);
    }

    private void resolveAssignment(GlobalVariables g, Assignment assignment) {
        g.getVariables().put(assignment.getVariable().getName(), assignment.getValue());
    }
}
