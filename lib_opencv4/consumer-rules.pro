-keep class org.opencv.* {*;}
-dontwarn org.opencv.*

-keepclasseswithmembernames class * {
    native <methods>;
}