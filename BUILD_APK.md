# بناء ملف APK 📦

هذا الملف يشرح كيفية بناء ملف APK نهائي جاهز للتثبيت على أي هاتف أندرويد.

## المتطلبات

- Android Studio (آخر إصدار)
- Java 17+
- Gradle 8.0+
- 2 GB مساحة حرة على القرص

## طريقة 1: بناء APK من Android Studio (الأسهل)

### الخطوات:

1. **فتح المشروع**
   - افتح Android Studio
   - اختر "Open"
   - انتقل إلى مجلد `koroko_ai_companion`

2. **الانتظار للمزامنة**
   - انتظر حتى ينتهي Gradle من المزامنة
   - قد يستغرق 2-5 دقائق

3. **بناء APK**
   - اذهب إلى `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - أو اضغط `Ctrl+Shift+A` وابحث عن "Build APK"

4. **الانتظار للبناء**
   - سيظهر شريط التقدم
   - قد يستغرق 3-10 دقائق

5. **الحصول على الملف**
   - بعد الانتهاء، سيظهر إشعار
   - انقر على "Locate" للعثور على الملف
   - أو اذهب إلى: `app/build/outputs/apk/debug/app-debug.apk`

## طريقة 2: بناء APK من سطر الأوامر

### الخطوات:

```bash
# الانتقال إلى مجلد المشروع
cd /path/to/koroko_ai_companion

# بناء APK للتطوير (أسرع)
./gradlew assembleDebug

# أو بناء APK للإصدار (أصغر حجم، أسرع)
./gradlew assembleRelease
```

### الملفات الناتجة:

```
# Debug APK
app/build/outputs/apk/debug/app-debug.apk

# Release APK
app/build/outputs/apk/release/app-release.apk
```

## الفرق بين Debug و Release

| الميزة | Debug | Release |
| :--- | :--- | :--- |
| **الحجم** | 50-80 MB | 20-30 MB |
| **السرعة** | أسرع في البناء | أبطأ في البناء |
| **الأداء** | أبطأ قليلاً | أسرع |
| **الاستخدام** | التطوير والاختبار | النشر النهائي |

## تثبيت APK على الهاتف

### الطريقة 1: من Android Studio

1. وصّل هاتفك بـ USB
2. فعّل "Developer Mode" على الهاتف
3. اضغط على "Run" أو `Shift+F10`
4. اختر جهازك من القائمة

### الطريقة 2: من سطر الأوامر

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### الطريقة 3: يدوياً

1. انسخ ملف APK إلى الهاتف
2. افتح مدير الملفات
3. ابحث عن الملف
4. اضغط عليه للتثبيت
5. اضغط "Install"

## استكشاف الأخطاء

### ❌ خطأ: "Build failed"

**الحل:**
```bash
# نظّف المشروع
./gradlew clean

# أعد البناء
./gradlew assembleDebug
```

### ❌ خطأ: "Gradle sync failed"

**الحل:**
1. اذهب إلى `File > Sync Now`
2. أو استخدم: `./gradlew sync`

### ❌ خطأ: "Out of memory"

**الحل:**
```bash
# زيادة ذاكرة Gradle
export GRADLE_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
./gradlew assembleDebug
```

### ❌ خطأ: "Installation failed"

**الحل:**
1. تأكد من تفعيل "Developer Mode"
2. تأكد من توصيل الهاتف بـ USB
3. جرّب إعادة تشغيل الهاتف
4. جرّب: `adb kill-server && adb start-server`

## تحسينات الحجم

### تقليل حجم APK:

```gradle
// في build.gradle.kts
android {
    bundle {
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}
```

### استخدام ProGuard:

```gradle
buildTypes {
    release {
        minifyEnabled = true
        shrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

## معلومات الملف النهائي

**اسم الملف:** `app-debug.apk` أو `app-release.apk`

**الحجم:** 20-80 MB (حسب النوع)

**الإصدار:** 1.0.0

**الحد الأدنى:** Android 7.0 (API 24)

**الحد الأقصى:** Android 14+ (API 34+)

## التوقيع الرقمي

### للإصدار Release:

```bash
# إنشاء keystore
keytool -genkey -v -keystore my-release-key.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias

# التوقيع
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore my-release-key.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  my-key-alias
```

## الخلاصة

**الخطوات السريعة:**
1. افتح المشروع في Android Studio
2. اذهب إلى `Build > Build APK(s)`
3. انتظر الانتهاء
4. انسخ الملف إلى الهاتف
5. ثبّت التطبيق

---

**استمتع بتطبيقك! 🚀**
