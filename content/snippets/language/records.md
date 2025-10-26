---
title: Records
---

Records are Java classes which allows simple definition of immutable data
structures.

Since [Java 16](/jdk/16/)

{{< sandbox version="java25" mainclass="Records" >}}
{{< sandboxsource "Records.java" >}}

public class Records {

	record Point(double x, double y) {

		// We can add additional methods
		double dist(Point p2) {
			var dx = x - p2.x;
			var dy = y - p2.y;
			return Math.sqrt(dx * dx + dy * dy);
		}

	}

	interface Shape {
		double area();
	}

	record Circle(Point center, double radius) implements Shape {

		// we can add overloaded versions of the constructor
		Circle(Point center, Point pointoncircle) {
			this(center, center.dist(pointoncircle));
		}

		// we can implement an interface
		@Override
		public double area() {
			return Math.PI * radius * radius;
		}

	}

	void main() {

		var p = new Point(3, 5);

		// We have
		IO.println(p.x());

		// The compiler creates a toString() method for us
		IO.println(p);

		// As well as equals() and hashcode() methods
		IO.println(p.equals(new Point(3, 5)));
		IO.println(p.hashCode());

		var c = new Circle(p, new Point(6, 8));

		IO.println(c.area());

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/language/Records.java)
