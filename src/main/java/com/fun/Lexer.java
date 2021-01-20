package com.fun;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class Lexer {
    private String sourceCode;
    private int lineNum;
    private String nextToken;
    private TokenEnum nextTokenType;
    private int nextTokenLineNum;
    private Map<String, TokenEnum> keywords;
    private Map<TokenEnum, String> tokenNameMap;
    private Pattern regexName = Pattern.compile("^[_\\d\\w]+");

    public Lexer(String sourceCode) {
        this(sourceCode, 1, "", TokenEnum.TOKEN_EOF, 0);
    }

    public Lexer(String sourceCode, int lineNum, String nextToken, TokenEnum nextTokenType, int nextTokenLineNum) {
        this.sourceCode = sourceCode;
        this.lineNum = lineNum;
        this.nextToken = nextToken;
        this.nextTokenType = nextTokenType;
        this.nextTokenLineNum = nextTokenLineNum;
        this.keywords = new HashMap<>();
        initKeyWords();
        initTokenNameMap();
    }

    private void initKeyWords() {
        this.keywords.put("print", TokenEnum.TOKEN_PRINT);
        this.tokenNameMap = new HashMap<>();
    }

    private void initTokenNameMap() {
        this.tokenNameMap.put(TokenEnum.TOKEN_EOF, "EOF");
        this.tokenNameMap.put(TokenEnum.TOKEN_VAR_PREFIX, "$");
        this.tokenNameMap.put(TokenEnum.TOKEN_LEFT_PAREN, "(");
        this.tokenNameMap.put(TokenEnum.TOKEN_RIGHT_PAREN, ")");
        this.tokenNameMap.put(TokenEnum.TOKEN_EQUAL, "=");
        this.tokenNameMap.put(TokenEnum.TOKEN_QUOTE, "\"");
        this.tokenNameMap.put(TokenEnum.TOKEN_DUOQUOTE,"\"\"");
        this.tokenNameMap.put(TokenEnum.TOKEN_NAME,  "Name");
        this.tokenNameMap.put(TokenEnum.TOKEN_PRINT, "print");
        this.tokenNameMap.put(TokenEnum.TOKEN_IGNORED, "Ignored");
    }

    private String scanName() {
        return this.scan(regexName);
    }

    private String scan(Pattern pattern) {
        Matcher m = pattern.matcher(this.sourceCode);
        if (m.find()) {
            String token = m.group();
            if (!Objects.equals("", token)) {
                this.skipSourceCode(token.length());
                return token;
            }
        }
        log.error("unreachable");
        System.exit(0);
        return "";
    }

    public MatchedToken nextTokenIs(TokenEnum tokenEnum) {
        MatchedToken matchedToken = this.getNextToken();
        if (!Objects.equals(matchedToken.getTokenType(), tokenEnum)) {
            log.error("next token type error");
            System.exit(0);
        }
        return matchedToken;
    }

    public TokenEnum lookAhead() {
        if (this.nextTokenLineNum > 0) {
            return this.nextTokenType;
        }
        int nowLineNum = this.lineNum;
        MatchedToken matchedToken = this.getNextToken();
        this.lineNum = nowLineNum;
        this.nextTokenLineNum = matchedToken.getLineNum();
        this.nextTokenType = matchedToken.getTokenType();
        this.nextToken = matchedToken.getToken();
        return matchedToken.getTokenType();
    }

    public void lookAheadAndSkip(TokenEnum tokenEnum) {
        int nowLineNum = this.lineNum;
        MatchedToken matchedToken = this.getNextToken();
        if (!Objects.equals(matchedToken.getTokenType(), tokenEnum)) {
           this.lineNum = nowLineNum;
           this.nextTokenLineNum = matchedToken.getLineNum();
           this.nextTokenType = matchedToken.getTokenType();
           this.nextToken = matchedToken.getToken();
        }
    }

    public MatchedToken getNextToken() {
        if (this.nextTokenLineNum > 0) {
            this.lineNum =  this.nextTokenLineNum;
            this.nextTokenLineNum = 0;
            return new MatchedToken(this.lineNum, this.nextTokenType, this.nextToken);
        }
        return this.matchToken();
    }

    public MatchedToken matchToken() {
        if (this.isIgnored()) {
            return new MatchedToken(this.lineNum, TokenEnum.TOKEN_IGNORED, "Ignored");
        }
        if (this.sourceCode.length() == 0) {
            return new MatchedToken(this.lineNum, TokenEnum.TOKEN_EOF, this.tokenNameMap.get(TokenEnum.TOKEN_EOF));
        }
        switch (this.sourceCode.charAt(0)) {
            case '$':
                this.skipSourceCode(1);
                return new MatchedToken(this.lineNum, TokenEnum.TOKEN_VAR_PREFIX, "$");
            case '(':
                this.skipSourceCode(1);
                return new MatchedToken(this.lineNum, TokenEnum.TOKEN_LEFT_PAREN, "(");
            case ')':
                this.skipSourceCode(1);
                return new MatchedToken(this.lineNum, TokenEnum.TOKEN_RIGHT_PAREN, ")");
            case '=':
                this.skipSourceCode(1);
                return new MatchedToken(this.lineNum, TokenEnum.TOKEN_EQUAL, "=");
            case '"':
                if (this.nextSourceCodeIs("\"\"")) {
                    this.skipSourceCode(2);
                    return new MatchedToken(this.lineNum, TokenEnum.TOKEN_DUOQUOTE, "\"\"");
                }
                this.skipSourceCode(1);
                return new MatchedToken(this.lineNum, TokenEnum.TOKEN_QUOTE, "\"");
            default:
                break;
        }
        if (this.sourceCode.charAt(0) == '_' || this.isLetter(this.sourceCode.charAt(0))) {
            String token = this.scanName();
            return new MatchedToken(this.lineNum, this.keywords.getOrDefault(token, TokenEnum.TOKEN_NAME), token);
        }
        log.error(String.format("MatchToken(): unexpected symbol near '%d'.", (int) this.sourceCode.charAt(0)));
        System.exit(0);
        return null;
    }

    private boolean isLetter(char c) {
        return (c >= 'a'  && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean nextSourceCodeIs(String s) {
        return this.sourceCode.startsWith(s);
    }

    private void skipSourceCode(int i) {
        this.sourceCode = this.sourceCode.substring(i);
    }

    private boolean isNewLine(char c) {
        return c == '\r'  || c == '\n';
    }

    private boolean isWhiteSpace(char c) {
        return c == '\t' || c == '\n' || c == '\f' || c == ' ';
    }

    private boolean isIgnored() {
        boolean result = false;
        while (this.sourceCode.length() > 0) {
            if (this.nextSourceCodeIs("\r\n") || this.nextSourceCodeIs("\n\r")) {
                this.skipSourceCode(2);
                this.lineNum += 1;
                result = true;
            } else if (isNewLine(this.sourceCode.charAt(0))) {
                this.skipSourceCode(1);
                this.lineNum += 1;
                result = true;
            } else if (isWhiteSpace(this.sourceCode.charAt(0))) {
                this.skipSourceCode(1);
                result = true;
            } else {
                break;
            }
        }
        return result;
    }

     protected  String scanBeforeToken(String token) {
        String[] result = this.sourceCode.split(token);
        if (result.length  < 2) {
            log.error("unreachable!");
            System.exit(-1);
        }
        this.skipSourceCode(result[0].length());
        return result[0];
    }
}
