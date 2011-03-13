grammar Stacktrace;

options { output=AST; }

tokens {
	DOT = '.';
	COLON = ':';
	PARENTHESIS_START = '(';
	PARENTHESIS_END = ')';
	CATCH = '[catch]';
	CAUSED_BY = 'Caused by';
	UNKNOWN_SOURCE = 'Unknown Source';
}

start	:	trace;

trace 	:	segment+;

segment:	CAUSED_BY? exception (LF (location|more))+;

exception
	:	class_name COLON description?;

location returns [Entry entry]
	:	{
		
			int lineNumber = -1;
			if ($lineNumberToken != null) {
				lineNumber = Integer.parseInt($lineNumberToken.text);
			}
			String fileName = null;
			if ($fileNameToken.tree != null) {
				fileName = $fileNameToken.value;
			} 
			$entry = new Entry($methodNameToken.text, $classNameToken.text, fileName, lineNumber); 
		}
		WS* LETTER LETTER WS+ classNameToken=class_name DOT methodNameToken=method_name PARENTHESIS_START (UNKNOWN_SOURCE | fileNameToken=file_name COLON lineNumberToken=DIGIT+) PARENTHESIS_END;
class_name	
	:	package_name DOT name;
	
package_name : 	name (DOT name)+;
file_name returns [Object value]
	:	name DOT name { $value = $file_name.text; };
method_name
	:	name;
more	:	WS* DOT DOT DOT WS DIGIT+ WS LETTER+;
	
name	:	LETTER (LETTER | DIGIT)*;

description 	
	:	(LETTER | DIGIT | DOT | SPECIAL_CHAR | WS | COLON | PARENTHESIS_START | PARENTHESIS_END)+;	
WS	:	(' '|'\t');
LF	:	'\n' '\r'?;
SPECIAL_CHAR
	:	('?' | '!' | '"' | '_' | '[' | ']'); 
LETTER
	:	('a'..'z'|'A'..'Z');
DIGIT 	:	'0'..'9';