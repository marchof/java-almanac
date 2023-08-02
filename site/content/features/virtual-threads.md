---
title: Virtual Threads (JEP 444)
copyright: Cay S. Horstmann 2023. All rights reserved.
jep: 444
jdkversion: 21
---


## Why Virtual Threads?

When Java 1.0 was released in 1995, its API had about a hundred classes, among them `java.lang.Thread`. Java was the first mainstream programming language that directly supported concurrent programming. 

Since Java 1.2, each Java thread runs on a *platform thread* supplied by the underlying operating system. (Up to Java 1.1, on some platforms, all Java threads were executed by a single platform thread.)

Platform threads have nontrivial costs. They require a few thousand CPU instructions to start, and they consume a few megabytes of memory. Server applications typically serve so many concurrent requests that it becomes infeasible to have each of them execute on a separate platform thread. In a typical server application, these requests spend much of their time *blocking*, waiting for a result from a database or another service. 

The classic remedy for increasing throughput is a non-blocking API. Instead of waiting for a result, the programmer indicates which method should be called when the result has become available, and perhaps another method that is called in case of failure. This gets unpleasant quickly, as the callbacks nest ever more deeply. 

JEP 425 introduced *virtual threads* in Java 19. Many virtual threads run on a platform thread. Whenever a virtual thread blocks, it is *unmounted*, and the platform thread runs another virtual thread. (The name “virtual thread” is supposed to be reminiscent of virtual memory that is mapped to actual RAM.)  Virtual threads became a preview feature in Java 20 (JEP 436) and are final in Java 21.

Virtual threads are useful when the number of concurrent tasks is large, and the tasks mostly block on network I/O. They offer no benefit for CPU-intensive tasks. For such tasks, consider parallel streams or [recursive fork-join tasks](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/RecursiveTask.html). 

## Creating Virtual Threads

For demonstration programs, one often constructs a thread with the `Thread` constructor and then starts it. Application programs should use an `ExecutorService`, or, at a lower level, a `ThreadFactory`. 

A new factory method `Executors.newVirtualThreadPerTaskExecutor()` yields an `ExecutorService` that runs each task in a separate virtual thread. For example:

{{< sandbox version=java21 preview="true" mainclass="VirtualThreadDemo" >}}{{< sandboxsource "VirtualThreadDemo.java" >}}
import java.util.concurrent.*;

public class VirtualThreadDemo {
   public static void main(String[] args) {
      final int NTASKS = 10; // Be kind to the sandbox
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      for (int i = 0; i < NTASKS; i++) {
         service.submit(() -> {
            long id = Thread.currentThread().threadId(); 
            Thread.sleep(1000);
            System.out.println(id);
            return id;
         });
      }
      service.close();
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

As an aside, here is a pro tip for shorter demo programs: Make the lambda expression return a value. Then it is converted to a `Callable`, not a `Runnable`. The single abstract method `call` of a `Callable` is declared to throw any exception, which saves you from having to catch the `InterruptedException` thrown by `Thread.sleep`. In contrast, `Runnable::run` cannot throw a checked exception.

Perhaps you are using a lower-level API that asks for a thread factory. To obtain a factory for virtual threads, use the new `Thread.Builder` class:

```
Thread.Builder builder = Thread.ofVirtual().name("request-", 1);
ThreadFactory factory = builder.factory();
```

Now, calling `factory.newThread(myRunnable)` creates a new (unstarted) virtual thread. The `name` method configures the builder to set thread names `request-1`, `request-2`, and so on.

You can also use a builder to create a single virtual thread: 

```
Thread t = builder.unstarted(myRunnable);
```

Alternatively, if you want to start the thread right away:

```
Thread t = builder.started(myRunnable);
```

Finally, for a quick demo, there is a convenience method:

```
Thread t = Thread.startVirtualThread(myRunnable);
```

Note that only the first approach, with an executor service, works with result-bearing tasks (callables).

## Thread API Changes

After a series of experiments with different APIs, the designers of Java virtual threads decided to simply reuse the familiar `Thread` API. A virtual thread is an instance of `Thread`. Cancellation works the same way as for platform threads, by calling `interrupt`. As always, the thread code must check the “interrupted” flag or call a method that does. (Most blocking methods do.)

There are a few differences. In particular, all virtual threads:

* Are in a single thread group 
* Have priority `NORM_PRIORITY`
* Are daemon threads

There is no API for constructing a virtual thread with another thread group. Trying to call `setPriority` or `setDaemon` on a virtual thread has no effect.

The static `Thread::getAllStackTraces` method returns a map of stack traces of all *platform* threads. Virtual threads are not included.

A new `Thread::isVirtual` instance method tells whether a thread is virtual.

Note that there is no way to find the platform thread on which a virtual thread executes.

Java 19 has a couple of changes to the `Thread` API that have nothing to do with virtual threads:

* There are now instance methods `join(Duration)` and `sleep(Duration)`.
* The non-final `getId` method is deprecated since someone might override it to return something other than the thread ID. Call the final `threadId` method instead.

As of Java 20, the `stop`, `suspend`, `resume`, `countStackFrames` methods throw an `UnsupportedOperationException` for both platform and virtual threads. These methods have been deprecated since Java 1.2 and deprecated for removal since Java 18.

## Capturing Task Results

You often want to combine the results of multiple concurrent tasks:

```
Future<T1> f1 = service.submit(callable1);
Future<T2> f2 = service.submit(callable2);
result = combine(f1.get(), f2.get());
```

Before virtual threads, you might have felt bad about the blocking `get` calls. But now blocking is cheap. Here is a sandbox with a more concrete example:

{{< sandbox version=java21 preview="true" mainclass="VirtualThreadDemo" >}}{{< sandboxsource "VirtualThreadDemo.java" >}}
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.concurrent.*;

public class VirtualThreadDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      Future<String> f1 = service.submit(() -> get("https://horstmann.com/random/adjective"));
      Future<String> f2 = service.submit(() -> get("https://horstmann.com/random/noun"));
      String result = f1.get() + " " + f2.get();
      System.out.println(result);
      service.close();
   }

   private static HttpClient client = HttpClient.newHttpClient();

   public static String get(String url) throws InterruptedException, IOException, URISyntaxException {
      var request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
      return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
   }   
}
{{< /sandboxsource >}}
{{< /sandbox >}}

If you have a list of tasks with the same result type, you can use the `invokeAll` method and then call `get` on each `Future`:

```
List<Callable<T>> callables = ...;
List<T> results = new ArrayList<>();
for (Future<T> f : service.invokeAll(callables))
  results.add(f.get());
```

Again, a more concrete sandbox:

{{< sandbox version=java21 preview="true" mainclass="VirtualThreadDemo" >}}{{< sandboxsource "VirtualThreadDemo.java" >}}
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;

public class VirtualThreadDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      List<Callable<String>> callables = new ArrayList<>();
      final int ADJECTIVES = 4;
      for (int i = 1; i <= ADJECTIVES; i++)
         callables.add(() -> get("https://horstmann.com/random/adjective"));
      callables.add(() -> get("https://horstmann.com/random/noun"));
      List<String> results = new ArrayList<>();
      for (Future<String> f : service.invokeAll(callables))
         results.add(f.get());
      System.out.println(String.join(" ", results));
      service.close();
   }

   private static HttpClient client = HttpClient.newHttpClient();

   public static String get(String url) throws InterruptedException, IOException, URISyntaxException {
      var request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
      return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
   }   
}
{{< /sandboxsource >}}
{{< /sandbox >}}

These happy-day snippets ignore the exceptions that `Future::get` might throw. They also don't provide a timeout. JEP 453 introduces an API for *structured concurrency* that makes it easier to write robust code:

```
try (var scope = StructuredTaskScope.ShutdownOnFailure()) {
   Supplier<T1> f1 = scope.fork(callable1);
   Supplier<T2> f2 = scope.fork(callable2);
   scope.joinUntil(deadline).throwIfFailed();
   result = combine(f1.get(), f2.get());
}
```

If an exception or timeout occurs, all tasks are canceled and the exception is rethrown.

As of Java 21, the API, about which I will write in detail elsewhere, is in preview.

## Rate Limiting

Virtual threads improve application throughput since you can have many more concurrent tasks than with platform threads. That can put pressure on the services that the tasks invoke. For example, a web service may not tolerate huge numbers of concurrent requests.

With platform threads, an easy (if crude) tuning factor is the size of the thread pool for those tasks. But you should not pool virtual threads. Scheduling tasks on virtual threads that are then scheduled on platform threads is clearly inefficient. And what is the upside? To limit the number virtual threads to the smallish number of concurrent requests that your service tolerates? Then why are you using virtual threads in the first place? 

With virtual threads, you should use alternative mechanisms for controlling access to limited resources. Instead of an overall limit on concurrent tasks, protect each resource in an appropriate way. For database connections, the connection pool may already do the right thing. When accessing a web service, you know your service, and can provide appropriate rate limiting.

As an example, on my personal web site, I provide demo services for producing random items. If a large number of requests comes at an instant from the same IP address, the hosting company blacklists the IP address. 

The following sandbox shows rate limiting with a simple semaphore that allows a small number of concurrent requests. When the maximum is exceeded, the `acquire` method blocks, but that is ok. With virtual threads, blocking is cheap.

{{< sandbox version=java21 preview="true" mainclass="RateLimitDemo" >}}{{< sandboxsource "RateLimitDemo.java" >}}
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;

public class RateLimitDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      List<Future<String>> futures = new ArrayList<>();
      final int TASKS = 250;
      for (int i = 1; i <= TASKS; i++)
         futures.add(service.submit(() -> get("https://horstmann.com/random/word")));
      for (Future<String> f : futures)
         System.out.print(f.get() + " ");
      System.out.println();
      service.close();
   }

   private static HttpClient client = HttpClient.newHttpClient();

   private static final Semaphore SEMAPHORE = new Semaphore(20);
   
   public static String get(String url) throws InterruptedException, IOException, URISyntaxException {
      var request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
      SEMAPHORE.acquire();
      try {
         Thread.sleep(100);
         return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
      } finally {
         SEMAPHORE.release();
      }
   }   
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Pinning

The virtual thread scheduler mounts virtual threads onto carrier threads. By default, there are as many carrier threads as there are CPU cores. You can tune that count with the `jdk.virtualThreadScheduler.parallelism` VM option.

When a virtual thread executes a blocking operation, it is supposed to be unmounted from its its carrier thread, which can then execute a different virtual thread. The Java libraries have been instrumented so that unmounting works for:

* Sleeping 
* Blocking on `ReentrantLock`
* Network I/O

In some situations, the virtual thread scheduler cannot unmount a thread that is about to block, but will compensate by starting another carrier thread. In JDK 21, this happens for synchronous file I/O operations, and when calling `Object.wait`. (This is an implementation detail that may change.) You can control the maximum number of carrier threads with the `jdk.virtualThreadScheduler.maxPoolSize` VM option.

A virtual thread can never be unmounted when it is *pinned*, which happens in the following two situations: 

1. When executing a `synchronized` method or block
2. When calling a native method or foreign function

The first cause for pinning is due to implementation issues in the JVM that will eventually be overcome. The second cause is unavoidable.

Being pinned is not bad in itself. But when a pinned thread blocks, there is a problem. The thread cannot be unmounted, and the carrier thread is blocked. That leaves fewer carrier threads for running virtual threads.

Pinning is harmless if `synchronized` is used to avoid a race condition in an in-memory operation. However, if there are blocking calls inside the `synchronized` method or block, it would be best to replace `synchronized` with a `ReentrantLock`. This is of course only an option if you have control over the source code. 

You always have the option whether or not to use virtual threads. In particular, heavy file I/O work may be better suited for platform threads.

To find out whether pinned threads are blocked, start the JVM with one of the options

```
-Djdk.tracePinnedThreads=short
-Djdk.tracePinnedThreads=full
```

You get a stack trace that shows when a pinned thread blocks:

```
...
org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) <== monitors:1
...
```

Note that you get only one warning per pinning location!

Alternatively, record with Java Flight Recorder, view with your favorite mission control viewer, and look for `VirtualThreadPinned` and `VirtualThreadSubmitFailed` events.

The following sandbox shows pinning in action. We launch a number of virtual threads that sleep in a synchronized method, blocking their carrier threads. A number of virtual threads are added that do no work at all. But they can't be scheduled until the blocking threads complete, because the carrier thread pool has been completely exhausted. Try modifying the program in one of two ways:

* use a `ReentrantLock` (by calling `rblock` instead of `block`)
* don't use virtual threads

Then the problem goes away, and the second group of threads can run while the first group is sleeping.

By the way, the code uses `LockSupport.parkNanos` instead of `Thread.sleep` so that we don't have to catch the pesky `InterruptedException`. 

{{< sandbox version=java21 preview="true" mainclass="PinningDemo" >}}{{< sandboxsource "PinningDemo.java" >}}
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class PinningDemo {
   public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService service =
         Executors.newVirtualThreadPerTaskExecutor(); 
         // Executors.newCachedThreadPool();
      
      final int TASKS = 20;
      long start = System.nanoTime();
      for (int i = 1; i <= TASKS; i++) {
         service.submit(() -> block());
         // service.submit(() -> rblock());
      }
      for (int i = 1; i <= TASKS; i++) {    
         service.submit(() -> noblock());
      }
      service.close();
      long end = System.nanoTime();
      System.out.printf("%.2f%n", (end - start) * 1E-9);
   }

   public static synchronized void block() {
      System.out.println("Entering block " + Thread.currentThread());      
      LockSupport.parkNanos(100_000_000);
      System.out.println("Exiting block " + Thread.currentThread());
   }
   private static Lock lock = new ReentrantLock(); 
   public static void rblock() {
      lock.lock();
      try {
         System.out.println("Entering rblock " + Thread.currentThread());      
         LockSupport.parkNanos(100_000_000);
         System.out.println("Exiting rblock " + Thread.currentThread());
      } finally {
         lock.unlock();
      }
   }   
   public static void noblock() {
      System.out.println("Entering noblock " + Thread.currentThread());      
      LockSupport.parkNanos(100_000_000);
      System.out.println("Exiting noblock " + Thread.currentThread());
   }   
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Thread Locals

A *thread-local variable* is an object whose `get` and `set` methods access a value that depends on the current thread. Why would you want such a thing instead of using a global or local variable? The classic application is a service that is not threadsafe, such as `SimpleDateFormat`, or that would suffer from contention, such as a random number generator. With a per-thread instance, you don't have to use a lock. 

Another common use for thread locals is to provide “implicit” context, such as a database connection, that is properly configured for each task. Instead of passing the context from one method to another, the task code simply reads the thread-local variable whenever it needs to access the context. You can even propagate thread-local variables to subtasks by using an *inheritable thread-local variable*. When a new thread is started, it receives a copy of the parent's inheritable thread locals. This is just the behavior that you want for context information, but making these copies can be expensive. The copies are necessary because the child thread might mutate the thread-local values.

However, thread locals can be a problem when migrating to virtual threads. There will likely be far more of them than threads in a thread pool, and now you have many more instances. In such a situation, you should rethink your sharing strategy.

To find where your app uses thread locals, run with the VM flag `jdk.traceVirtualThreadLocals`. You will get a stack trace when a virtual thread mutates a thread-local variable.

Consider migrating to *scoped values*  instead. Scoped values have per-thread instances, but they are immutable and have a bounded lifetime. Scoped values are inherited in virtual threads created by a `StructuredTaskScope`. In Java 21, they are a preview feature, described in JEP 446. I don't discuss them in detail here, but the following sandbox shows how to use them:

{{< sandbox version=java21 preview="true" mainclass="ScopedValueDemo" >}}{{< sandboxsource "ScopedValueDemo.java" >}}
import java.util.*;
import java.util.concurrent.*;

public class ScopedValueDemo {
   private static final ScopedValue<Locale> LOCALE = ScopedValue.newInstance();
   public static void main(String[] args) {
      Random generator = new Random();
      Locale[] locales = { Locale.CANADA, Locale.CHINA, Locale.FRANCE, Locale.GERMANY, Locale.ITALY,
         Locale.JAPAN, Locale.KOREA, Locale.PRC, Locale.TAIWAN, Locale.UK, Locale.US };
      ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
      for (int i = 0; i < 100; i++) {
         service.submit(() -> handleRequest(locales[generator.nextInt(locales.length)]));
      }
      service.close();
   }
   public static void handleRequest(Locale localeOfUser) {
      try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
         ScopedValue.where(LOCALE, localeOfUser)
            .run(() -> doWork());
      }
   }
   public static void doWork() {
      Locale localeOfUser = LOCALE.get();
      System.out.println(localeOfUser.getDisplayCountry(localeOfUser));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Conclusion

* Use virtual threads to increase throughput when you have many tasks that mostly block on network I/O 
* The primary benefit is the familiar “synchronous” programming style, without callbacks
* Don't pool virtual threads; use other mechanisms for rate limiting
* Check for pinning and mitigate if necessary
* Minimize thread-local variables in virtual threads

## References

* [JEP 444: Virtual Threads, OpenJDK](https://openjdk.java.net/jeps/444)
* [JEP 446: Scoped Values (Preview), OpenJDK](https://openjdk.java.net/jeps/444)
* [JEP 453: Structured Concurrency (Preview), OpenJDK](https://openjdk.java.net/jeps/444)

