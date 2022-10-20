---
title: Text Blocks
type: sandbox
---

Text blocks allow multiline strings literals.

Since [Java 15](/jdk/15/)

{{< sandbox version="java17" mainclass="TextBlocks" >}}
{{< sandboxsource "TextBlocks.java" >}}

public class TextBlocks {

	static final String LINK_TEMPLATE = """
			<a href="%s">Click here!</a>""";

	public static void main(String... args) {

		// Text blocks always start with three double quotes and a new line:
		String greeting = """
				Hello
				"World"!
				""";

		System.out.println(greeting);

		System.out.println(LINK_TEMPLATE.formatted("https://javaalmanac.io/"));
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/language/TextBlocks.java)
