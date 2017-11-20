# Tensorflow on Android
>  运用 ssd_mobilenet_v1_coco 来完成功能，它的模型二进制文件最小，识别速度最快，识别率是最低的，但是也足够识别180多个常见物体，具体见App下的Assets的labels.txt文件。

* 准备好 Pre-trained model,加入assets中;
* libs上加入tensorflow.arr
* 下载了可以在 Android 上面运行 TensorFlow 并 Inference 的 TensorFlow Android 接口

### demo的效果

![](/Users/yifanyang/Desktop/picture/detection1.png)

  从相册里面选取一张照片，之后程序就会识别出图片中的物体，可以看到在该图上面能识别出多个人物、酒杯和餐桌，并用红色的框标识物体的位置，同时在边框的左上角有识别物体的名称。而作为一个人物被识别出来，这不是程序的 bug，只是因为这个模型没有办法识别，可能是缺乏相关的训练数据之类的。我们可以选用识别率更高的模型或者自己训练一个识别器来解决这个问题。
