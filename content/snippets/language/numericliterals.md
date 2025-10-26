---
title: Numeric Literals
---

Integer literals can be written in binary, octal, decimal or hexadecimal
representation. The `L` suffix denotes a long type, otherwise
`int` is assumed Floating point literals can be written in decimal
or as hexadecimal representation. The suffix `d` denotes type
`double` (default), the suffix `f` denotes
`float`.

Since [Java 7](/jdk/7/)

{{< sandbox version="java25" mainclass="NumericLiterals" >}}
{{< sandboxsource "NumericLiterals.java" >}}

public class NumericLiterals {

	// integer literals

	int bin = 0b10110110;

	int oct = 01234_567;

	int dec = 1_000_000;

	int dec2 = 4________2;

	int hex = 0xcafe_babe;

	long lhex = 0xcafe_babeL;

	// floating point literals

	double d1 = 1.0E3d;

	double d2 = .333_333;

	double dhex = 0xcafe.babeP3;

	float f = 1.0E3f;

	float fhex = 0xcafe.babeP3f;

	void main() {
		IO.println(bin);
		IO.println(oct);
		IO.println(dec);
		IO.println(dec2);
		IO.println(hex);
		IO.println(lhex);
		IO.println(d1);
		IO.println(d2);
		IO.println(dhex);
		IO.println(f);
		IO.println(fhex);
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/language/NumericLiterals.java)
