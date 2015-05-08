" Vim syntax file
" Language:	SIMPLE
" Maintainer:	phf@acm.org (Peter Froehlich)
" Last Change:	2013 Mar 05

if exists('b:current_syntax')
  finish
endif

" SIMPLE is a basic imperative programming language I tend to use in
" my 'Compilers and Interpreters' course. It is very much a language
" in the Wirthian Pascal/Modula/Oberon tradition.

" Identifiers and literals.

syn match simpleIdentifier '[A-Za-z][A-Za-z0-9]*' contained
syn match simpleLiteral '[0-9][0-9]*'
syn match simpleLiteral '\'[\\].\{1}\''
syn match simpleLiteral '\".*\"'

" Keywords and types for the basic language.

syn keyword simpleKeyword PROGRAM BEGIN END
syn keyword simpleKeyword CONST TYPE VAR
syn keyword simpleKeyword ARRAY OF RECORD
syn keyword simpleKeyword DIV MOD
syn keyword simpleKeyword IF THEN ELSE WHILE DO REPEAT UNTIL
syn keyword simpleKeyword READ WRITE

syn keyword simpleType INTEGER

" Keywords, types, and literals for the extended languages.
" (Not all of these are implemented every semester.)
" TODO real number literals, set literals not quite correct

syn keyword simpleKeyword ELSIF FOR TO BY

syn keyword simpleKeyword PROCEDURE RETURN

syn keyword simpleType REAL

syn keyword simpleType CHARACTER

syn keyword simpleType BOOLEAN
syn keyword simpleKeyword AND OR NOT
syn keyword simpleLiteral TRUE FALSE

syn keyword simpleKeyword POINTER NEW DISPOSE
syn keyword simpleLiteral NIL

syn keyword simpleType SET
syn keyword simpleKeyword IN
syn region simpleSet start='{' end='}'

" Various forms of comments used over the years.

syn match simpleComment '//.*$' contains=simpleTodo
syn match simpleComment '--.*$' contains=simpleTodo
syn region simpleComment start='(\*' end='\*)' contains=simpleComment,simpleTodo

" vim-ism

syn keyword simpleTodo contained TODO FIXME XXX

" Highlighting. (Comment, Constant, Identifier, Statement, Type, Special,
" Ignore, Error, Todo)

hi def link simpleKeyword Statement
hi def link simpleType Type
hi def link simpleIdentifier Identifier
hi def link simpleLiteral Constant
hi def link simpleComment Comment
hi def link simpleTodo Todo
hi def link simpleSet Constant

let b:current_syntax = 'simple'

" vim: ts=8
