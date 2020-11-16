# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
# Android包
-libraryjars /Users/bai/Downloads/sdk/sdk/platforms/android-19/android.jar

# 忽略警告
-ignorewarnings

# 不需要优化
-dontoptimize

# 记录混淆时与源文件产生的映射文件
#-printmapping out.map

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod
-dontusemixedcaseclassnames

# 混淆第三方jar包
-dontskipnonpubliclibraryclasses

# 混淆时记录日志
-verbose

# 注释不被混淆
-keepattributes *Annotation*

# Android 组件不被混淆
-keep public interface com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-dontnote com.android.vending.licensing.ILicensingService

# 保持所有类中public和protected的字段和方法不被混淆
-keep public class * {
    public protected *;
}

# Preserve all .class method names.
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 枚举类不被混淆
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# 序列化对象不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 控件类不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
	void set*(***);
    *** get*();
}


# 自定义控件类构造方法不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

-keep class * extends org.litepal.crud.LitePalSupport {
    *;
}