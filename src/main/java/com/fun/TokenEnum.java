package com.fun;

public enum TokenEnum {
    TOKEN_EOF,                 // end-of-file
    TOKEN_VAR_PREFIX,          // $
    TOKEN_LEFT_PAREN,          // (
    TOKEN_RIGHT_PAREN,         // )
    TOKEN_EQUAL,               // =
    TOKEN_QUOTE,               // "
    TOKEN_DUOQUOTE,            // ""
    TOKEN_NAME,                // Name ::= [_A-Za-z][_0-9A-Za-z]*
    TOKEN_PRINT,               // print
    TOKEN_IGNORED              // ignored
}
