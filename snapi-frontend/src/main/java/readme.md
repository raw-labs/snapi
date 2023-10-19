# Installation

- Download the latest version from http://www.antlr.org/download/antlr-4.13.0-complete.jar

- Move it to a directory of your choice, e.g. `/usr/local/lib/antlr-4.13.0-complete.jar`
- Add the jar file to your classpath: `export CLASSPATH=".:/usr/local/lib/antlr-4.13.0-complete.jar:$CLASSPATH"`
  The `.` is important, as it adds the current directory to the classpath.
- Create an alias for the jar file, e.g. `alias antlr4='java -jar /usr/local/lib/antlr-4.13.0-complete.jar'`
- Create an alias for the ANTLR tool, e.g. `alias grun='java org.antlr.v4.gui.TestRig'`

# Usage in terminal

- Create a grammar file, e.g. `Hello.g4`
```
grammar Hello;
// Define a grammar called Hello
r : 'hello' ID ;
// match keyword hello followed by an identifier
ID : [a-z]+ ;
// match lower-case identifiers
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines, \r (Windows)
```
- Generate lexer and parser: `antlr4 Hello.g4`
- Compile generated code: `javac Hello*.java`
- Test the lexer `grun Hello r -tokens` and type `hello world` in the terminal
- Test the parser: `grun Hello r -tree` and type `hello world` in the terminal


# General terminal test usage
`antlr4 Snapi.g4 && javac *.java && grun Snapi -prog -gui`
It generates files, compiles them and runs the `-prog` rule with gui tree result.

# Usage in Intellij
- Install the ANTLR v4 grammar plugin
  Now you can right click on the grammar file and generate the lexer and parser as java files.
  They come with Lexer, Parser, Listener(similar to visitor but event driven) and Visitor classes.
  Some default implementations are provided for Visitor and Listener, override only the methods you need.

# Comments
## Lexer
- Lexer rules start with capital letters.
- Lexer rules are matched in the order they are defined, so if you have two rules that match the same input, the first one will be used.
- Lexer rules can be used in parser rules, but not the other way around.
- `fragment` keyword is used to define a rule that is used by other rules, but not by itself.

## Parser
- Parser rules start with lower case letters.
- Parser rules are matched in the order they are defined, so if you have two rules that match the same input, the first one will be used.
- Parser resolves ambiguity that top-down parsers have with left recursion, by using the first matching rule. That way we can have left recursive rules that feel more natural.
- By default, the rules are left associative, but we can change it with `<assoc=right>`

## Remarks
in ~["]