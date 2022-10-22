class: center, middle

# Java 11 - Das komplette Update

Marc Hoffmann

SBB IT Ausbildungscamp

Zürich, 17.04.2019


---
<img src="diagrams/res-link.svg" align="right" width="60%">
# Workshop Material

* https://bit.ly/2UoyYCC


---
# Some History of Java

![Release Timeline](diagrams/history.svg)

---


# 2016: "Java is doomed! It evolves so slowly! Every good idea takes ages to be available!"
--

# 2018: "Java is doomed! It evolves too quickly! Nobody will be able to keep pace!"

Daniel Fernández, https://twitter.com/danfenz/status/976376717811077121

---
# The New Release Cadence

![Release Timeline](diagrams/releases.svg)

---
# Long Term Support (LTS) Releases

* Current LTS release: Java 11
* Next LTS releases: Java 17, Java 23

---
# OpenJDK

* Java has been fully opensourced as OpenJDK
* License: GNU GPL + linking exception
* Source code only


--
Distributions:

* Oracle Commercial JDKs: Production usage only with commercial license
* Oracle OpenJDK builds: [jdk.java.net](https://jdk.java.net/)


--
Free Alternatives with LTS commitment:

* AdoptOpenJDK (Community): [adoptopenjdk.net](https://adoptopenjdk.net/)
* Zulu (Azul Systems)
* Corretto (Amazon)
* SapMachine (SAP)


---
# Alternative Implementations

JVMs
* Eclipse OpenJ9
* Oracle Graal

Compiler
* Eclipse Compiler for Java (ECJ)

---
# What makes an JDK Release?

* Java Language Specification (JLS)
* Java VM Specification
* Java API
* JVM
* Tools

---
# Java Development Process


Java Community Process (jcp.org)
* Java Specification Requests (JCR)

OpenJDK Process
* JDK Enhancement Proposal (JEP)

---
# Java Modules (JMS, Project Jigsaw)
.version[Java 9]

* New file format `*.jmod` (Ok, it's a ZIP)
* Metadata compiled from `module-info.java`
  ```java
  module org.hello {
        exports org.hello;
        requires org.output;
  }
  ```
* Goodbye class path...
  ```bash
  $ java --module-path mods -m org.hello/org.hello.World
  ```
* `jlink` for custom modular runtime images
---
# Private Methods in Interfaces
.version[Java 9]

* Why?
--

* Default methods may share common code!

```java
interface Controller {
    default void start(Engine e) {
        setStatus(e, true);
    } 
    default void stop(Engine e) {
        setStatus(e, false);
    }
    private void setStatus(Engine e, boolean running) {
        // ...
    }
}
```

---
# Reactive Streams
.version[Java 9]

* `java.util.concurrent.Flow`
* Java implementation of www.reactive-streams.org

---
# Object.finalize() deprecated
.version[Java 9]

Finally...

* Alternative: `PhantomReference`, `Cleaner`

---
# Enhanced Deprecation
.version[Java 9]

```Java
@Deprecated(since="1.2", forRemoval=true)
public final synchronized void stop(Throwable obj) {
    throw new UnsupportedOperationException();
}
```

* `jdeprscan`

---
# Local Variable Type Inference
.version[Java 10]

```Java
var names = new ArrayList<String>();
```

* `var` reserved variable name
* Initial initialization defines type
* Works for anonymous types

```Java
var anonymous = new Object() {
    void hello() { }
}
anonymous.hello();
```

---
# Graal VM
.version[Java 10]

* Virtual Machine written in Java
* Generic VM for different languages
  * LLVM
  * Java Script
  * Java Byte Code
  
`java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler`

---
# No more Java EE and CORBA
.version[Java 11]

* Removed Packages (marked for removal in Java 9)
  ```Java
  javax.activation  
  javax.activity  
  javax.annotation 
  javax.jws
  javax.jws.*
  javax.rmi.CORBA
  javax.transaction  
  javax.xml.bind.*
  javax.xml.soap
  javax.xml.ws.*
  ```

* Java EE moved to Eclipse as EE4J

---
# HTTP Client
.version[Java 11]

`UrlConnection` too abstract

* Package `java.net.http`
* HTTP/2
* Asynchronous Notifications
* Proxying, Cookies, Authentication


---
# Single Source File Launch
.version[Java 11]

`java Hello.java`
--


On Unix-like systems `*.java` files can become executable:

```Java
#!/usr/bin/java --source 10
public class Hello {
	public static void main(String... args) {
		System.out.println("Hello");
	}
}
```

---
# New Garbage Collectors
.version[Java 11]

* Low Latency Garbage Collector ZGC
* Epsilon GC - no garbage collection at all
* Based on Garbage Collector Interface (Java 10)

---
# Dynamic Class-File Constants
.version[Java 11]

* New constant pool type
* Value determined at runtime with "bootstrap method"

---
# Preview Features
.version[Java 11]

```bash
$ javac --release 11 --enable-preview Foo.java
```

---
# Switch Expressions (Preview Feature)
.version[Java 12]

```java
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    case THURSDAY, SATURDAY     -> 8;
    case WEDNESDAY              -> 9;
};
```

---
# JVM Constants API
.version[Java 12]

* Access constants in class files and at runtime


---
# Thank you! Questions?

![Questions](diagrams/questions.jpg)

* https://github.com/marchof/java-almanac