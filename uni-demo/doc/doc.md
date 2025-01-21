# 前言
身份证相机YunCamerax是基于androidx原生Camerax制作的，主要用于身份证拍摄，裁剪。支持横屏竖屏（需安卓X支持）

# 功能
- 拍摄身份证照片
- 拍摄完进行预览确认
- 可自定义部分功能
- 自动裁剪身份证照片
- 自动闪光灯
- 手电筒
- 手动聚焦
- 支持横屏竖屏
- 支持自定义蒙版文件
- 支持关闭自动裁剪

# 参数

参数|类型|默认值|参数说明
--|:--:|:--:|--:
type|Integer|0|控制蒙版的显示，0：身份证人像面，1：身份证国徽面，2：自定义蒙版
imageIndex|Integer|0|控制蒙版的切换，0：蒙版第一张,1：蒙版第二张，2,蒙版第三张,正反面均支持
enableTorch|Boolean|true|开启手电筒，默认开启
text|String|触摸屏幕对焦|底部文字说明，内置触摸对焦，可考虑要不要显示
landscape|Boolean|true|是否横屏，默认true
backColor|String|#9a000000|背景色，默认半透明，全透明需不传
backgroundImage|String|null|自定义蒙版背景图片，type为2时启用
fullSrc|Boolean|false|是否返回全路径，默认false
isCut|Boolean|true|是否裁剪，默认true

# 自定义蒙版文件说明

```
	ps：启用自定义的蒙版需将type设置为2，并且将对应的文件存储在插件目录内
	若需要自行定义蒙版的话，请创建对应目录文件（分辨率均为890x560），详情请看示例项目
	自定义蒙版： /nativeplugins/yun-camerax/android/assets/自定义蒙版文件.png
```
# 使用实例

```
	//更多示例，请自行导入并查看示例项目
   var cameraModule = uni.requireNativePlugin('yun-camerax-module');
   //无需蒙版可将type设置为非参数值，例如 type：99
   cameraModule.takePhoto({ type: 0, imageIndex: 0, fullSrc: true }, res => {
        console.log(res);
        uni.showModal({
            title: '提示',
            content: JSON.stringify(res),
            success: function (res) {
                if (res.confirm) {
                    console.log('用户点击确定');
                } else if (res.cancel) {
                    console.log('用户点击取消');
                }
            }
        });
		
        //获取的地址需要加上file://才能在image上显示，若需要上传到服务器也需要带上这个前缀
		//默认不加该头部，如需要该头部，请将fullSrc设置为true，这时候返回的是带这个头部的路径
        //let src = "file://" + res.file;
		let src = res.file;
        /*uni.uploadFile({
            url: "https://www.test.com,
            formData: {},
            filePath: src,//上面拼接的地址
            name: name,
            method: "POST",
            header: {},
            success: (result) => {
                
            },
            fail: (e) => {
                
            }
        })*/


    });

```

# APP预览下载
```
夸克网盘：https://pan.quark.cn/s/4a6e1a51d2b1
提取码：m36r
```