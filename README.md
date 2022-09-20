# YahooNativeAd-thread-leak

To reproduce the leak run the app and observe displayed thread counter. 


- Code: https://github.com/linean/YahooNativeAd-thread-leak/blob/main/app/src/main/java/com/example/filestoragecachethreadleak/MainActivity.kt
- JDK-6399443 : ThreadPoolExecutor leak: https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6399443
- ThreadPoolExecutor docs: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
