package com.fun;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Parser {

    private Print parsePrint(Lexer lexer) {
        Print p = new Print();
        p.setLineNum(lexer.getLineNum());
        lexer.nextTokenIs(TokenEnum.TOKEN_PRINT);
        lexer.nextTokenIs(TokenEnum.TOKEN_LEFT_PAREN);
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        p.setVariable(this.parseVariable(lexer));
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        lexer.nextTokenIs(TokenEnum.TOKEN_RIGHT_PAREN);
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        return p;
    }

    private String parseName(Lexer lexer) {
        MatchedToken m = lexer.nextTokenIs(TokenEnum.TOKEN_NAME);
        return m.getToken();
    }

    private Statement parseAssignment(Lexer lexer) {
        Assignment assignment = new Assignment();
        assignment.setLineNum(lexer.getLineNum());
        assignment.setVariable(parseVariable(lexer));
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        lexer.nextTokenIs(TokenEnum.TOKEN_EQUAL);
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        assignment.setValue(parseString(lexer));
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        return assignment;
    }

    private String parseString(Lexer lexer) {
        String str = "";
        switch (lexer.lookAhead()) {
            case TOKEN_DUOQUOTE:
                lexer.nextTokenIs(TokenEnum.TOKEN_DUOQUOTE);
                lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
                return str;
            case TOKEN_QUOTE:
                lexer.nextTokenIs(TokenEnum.TOKEN_QUOTE);
                str = lexer.scanBeforeToken(lexer.getTokenNameMap().get(TokenEnum.TOKEN_QUOTE));
                lexer.nextTokenIs(TokenEnum.TOKEN_QUOTE);
                lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
                return str;
            default:
                log.error("parse String error");
                System.exit(-1);
                return "";
        }
    }

    private boolean isSourceCodeEnd(TokenEnum tokenEnum) {
        return Objects.equals(tokenEnum,TokenEnum.TOKEN_EOF);
    }

    private Variable parseVariable(Lexer lexer) {
        Variable variable = new Variable();
        variable.setLineNum(lexer.getLineNum());
        lexer.nextTokenIs(TokenEnum.TOKEN_VAR_PREFIX);
        String name = parseName(lexer);
        variable.setName(name);
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        return variable;
    }
    
    private Statement parseStatement(Lexer lexer) {
        lexer.lookAheadAndSkip(TokenEnum.TOKEN_IGNORED);
        switch (lexer.lookAhead()) {
            case TOKEN_PRINT:
                return parsePrint(lexer);
            case TOKEN_VAR_PREFIX:
                return parseAssignment(lexer);
            default:
                log.error("unknown statement");
                System.exit(-1);
                return null;
        }
    }

    public SourceCode parse(String code) {
       Lexer lexer = new Lexer(code);
       SourceCode sourceCode = parseSourceCode(lexer);
       lexer.nextTokenIs(TokenEnum.TOKEN_EOF);
       return sourceCode;
    }

    private List<Statement> parseStatements(Lexer lexer) {
        List<Statement> statements = new ArrayList<>();
        while (!isSourceCodeEnd(lexer.lookAhead())) {
            Statement statement = parseStatement(lexer);
            if (Objects.isNull(statement)) {
                log.error("parse error");
                System.exit(-1);
            }
            statements.add(statement);
        }
        return statements;
    }

    private SourceCode parseSourceCode(Lexer lexer) {
        SourceCode sourceCode = new SourceCode();
        sourceCode.setLineNum(lexer.getLineNum());
        sourceCode.setStatements(parseStatements(lexer));
        return sourceCode;
    }
}
