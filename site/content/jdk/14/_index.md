---
title: Java 14
---

{{< jdkdetails "14" >}}

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 14 snippets without a local Java installation.

{{< sandbox version="java14" mainclass="Java14" preview="false" >}}
{{< sandboxsource "Java14.java" >}}
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Java14 {
    
    public static void main(String[] args) {

        String when = switch(DayOfWeek.from(LocalDate.now())) {
            case MONDAY:
                yield "at the beginning of the week";
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
                yield "in the middle of the week";
            case FRIDAY:
                yield "at the end of the week";
            case SATURDAY:
            case SUNDAY:
                yield "at the weekend";
        };

        System.out.printf("Java 14 %s!", when);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

