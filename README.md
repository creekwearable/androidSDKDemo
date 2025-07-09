## andriod Bluetooth Management SDK

### 一、development guide
  
 language support：Java kotlin
  
 minSdk：26
  
 compileSdk：32
    

#### Step 1: Add dependencies

##### build.gradle
    
    dependencies {
    ///************Key library files
    implementation("com.example.creek_sdk:flutter_release:6.7")
    implementation("creek_aar:creek_aar_release:6.7")
    ///*********************
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.protobuf:protobuf-javalite:4.0.0-rc-2")
    }
##### settings.gradle

       String storageUrl = System.env.FLUTTER_STORAGE_BASE_URL ?: "https://
       storage.googleapis.com"
      repositories {
        maven {
            url "$storageUrl/download.flutter.io"
        }
        maven {
            url 'https://creekwearable.github.io/static/repo'
        }
      }

● Add resource pack CreekSDK.aar

   [CreekSDK5.2.aar](https://creekwearable.github.io/static/andriodSDKVersion/5.2/CreekSDK5.2.aar)
   
After version 5.3, the CreekSDK.aar file has been moved to the git repository. You only need to implement("creek_aar:creek_aar_release:#viersin"), and the CreekSDK.aar file does not need to be downloaded to the project.

   


#### Step 2：Rights Profile

    <uses-permission
        android:name="android.permission.BLUETOOTH"
        android:maxSdkVersion="30" />
    <uses-permission
        android:name="android.permission.BLUETOOTH_ADMIN"
        android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"
        android:usesPermissionFlags="neverForLocation"
        tools:targetApi="s" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />


### 二、SDK usage

#### initialization
    CreekManager.sInstance.creekRegister(this)
    CreekManager.sInstance.initSDK()


## SDK Usage document
Chinese document version：<https://xiaochey.feishu.cn/docx/L7Zyd5ZYwoH90FxNcCycfM3Kn7T?from=from_copylink> 
     
English document version：<https://xiaochey.feishu.cn/docx/W95CduAStoi7nNxQQVxcl19Fnrh>            
                            
                            
## Custom dial

Chinese document version：<https://xiaochey.feishu.cn/docx/MokFdDOjUoNOSfxnHeqcUQIwnwe?from=from_copylink>

English document version：<https://xiaochey.feishu.cn/docx/FSmWd2bopo8jwyxiPdAcTr3anih?from=from_copylink>                      
                            

## Other documents

Chinese document version：<https://xiaochey.feishu.cn/docx/JUVzdesZVow1Rkx278Rcq6ZLnId?from=from_copylink>

English document version：<https://xiaochey.feishu.cn/docx/JksfdjOlWoVFaFx1LZncGM6SnxJ?from=from_copylink>                           
                            
                            
                            