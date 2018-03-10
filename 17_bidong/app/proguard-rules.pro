#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#忽略警告
-ignorewarning
#保护注解
-keepattributes *Annotation*
#避免混淆泛型
-keepattributes Signature

-keepattributes Exceptions,InnerClasses,EnclosingMethod

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends java.lang.annotation.Annotation { *; }
-keep public class com.android.vending.licensing.ILicensingService


##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######

#如果不想混淆 keep 掉
-keep class com.intexh.frd.PPStarApplication { *; }
-keep class com.intexh.frd.userentity.** { *; }

#我是以libaray的形式引用了一个图片加载框架,如果不想混淆 keep 掉
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.** {*;}
-keep class com.nostra13.universalimageloader.** { *; }
-keep class org.jivesoftware.** { *; }
-keep class org.apache.** { *; }
-keep class com.easemob.** { *; }
-keep class com.umeng.** {*;}
-keep class com.tencent.mm.** {*;}
-keep class com.shizhefei.** {*;}

#支付宝
-keep class com.alipay.** { *; }
-keep class com.ta.utdid2.** { *; }
-keep class com.ut.device.** { *; }
-keep class org.json.alipay.** { *; }

#android
-keep class android.support.** { *; }
-keep class com.android.app.view.** { *; }
-keep class com.android.app.Platform$* { *; }     # 保持内部类不被混*;

-keep class com.android.app.CommonCallback{    # 保持某个类或接口的公有和保护类型的成员和方法不被混淆
    public protected <fields>;
    public protected <methods>;
}

#忽略警告
#如果引用了v4或者v7包
-dontwarn android.support.**

-dontwarn com.alibaba.**
-keep class com.alibaba.**
-keepclassmembers class com.alibaba.** {
    *;
}
-keep class com.taobao.**
-keepclassmembers class com.taobao.** {
    *;
}

-dontwarn com.google.common.**
-dontwarn com.fasterxml.jackson.**
-dontwarn com.amap.api.**
-dontwarn net.jcip.annotations.**

-keepattributes Annotation,EnclosingMethod,Signature
-keep class com.fasterxml.jackson.**
-keepclassmembers class com.fasterxml.jackson.** {
    *;
}

-keep class com.duanqu.**
-keepclassmembers class com.duanqu.** {
    *;
}

-keep class com.ut.mini.** { *; }

####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####
-keep public class * extends android.view.View {
	public <init>(android.content.Context);
	public <init>(android.content.Context, android.util.AttributeSet);
	public <init>(android.content.Context, android.util.AttributeSet, int);
	public void set*(...);
}
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
	native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers class * {
	public void onEvent(com.pwittchen.network.events.library.event.ConnectivityStatusChangedEvent);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
   *;
}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
	static final long serialVersionUID;
	private static final java.io.ObjectStreamField[] serialPersistentFields;
	!static !transient <fields>;
	private void writeObject(java.io.ObjectOutputStream);
	private void readObject(java.io.ObjectInputStream);
	java.lang.Object writeReplace();
	java.lang.Object readResolve();
}
#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
	public void *ButtonClicked(android.view.View);
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#gson
#-libraryjars libs/gson-2.2.2.jar
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#如果你使用了webview
# webview + js
-keepattributes *JavascriptInterface*
# keep 使用 webview 的类
-keepclassmembers class  com.veidy.activity.WebViewActivity {
   public *;
}
# keep 使用 webview 的类的所有的内部类
-keepclassmembers  class  com.veidy.activity.WebViewActivity$*{
    *;
}

#移除log 测试了下没有用还是建议自己定义一个开关控制是否输出日志
#-assumenosideeffects class android.util.Log {
#	public static boolean isLoggable(java.lang.String, int);
#	public static int v(...);
#	public static int i(...);
#	public static int w(...);
#	public static int d(...);
#	public static int e(...);
#}

#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt