# JMH benchmarks for Apache Tomcat classes

This project benchmarks some classes used in Apache Tomcat server against (popular) alternatives

## FastHttpDateFormat

org.apache.tomcat.util.http.FastHttpDateFormat uses a pool of java.text.SimpleDateFormat instances which is not 
thread-safe.
Java 8 introduced the new Date Time API with the thread-safe java.time.format.DateTimeFormatter.
info.mgsolutions.tomcat.benchmarks.datetime.HttpDateFormatBenchmark busts some myths!
 
