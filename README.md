# iTopicChat

### 安装体验：
<img src="http://qiniu.itopic.com.cn/itopicchat_11.0.png">

### 效果gif图：
![](http://qiniu.itopic.com.cn/chat1_final.gif)
![](http://qiniu.itopic.com.cn/chat4.gif)![](http://qiniu.itopic.com.cn/chat5.gif)
### 现功能：

- ✅ 一整套完善的聊天IM系统，包括ui，数据库，后台接口全开源
- ✅ 线上正式环境，不用假数据，ios和后台php接口也都提供（关注我，等更新）
- ✅ 长连接基于个推+华为push，全免费。不用融云环信腾讯等商业框架
- ✅ 键盘、表情栏、功能栏、语音栏 切换自然无闪烁
- ✅ 界面精致美观（仿微信）、流畅无卡顿
- ✅ 语音条 + 微信表情 + 图片 + 视频通话 + 语音通话
- ✅ 消息静音设置，黑名单设置
- ✅ 黑夜模式
- ✅ java+mvc+okhttp+glide，封装都较简易，各模块都可方便替换成你自己的项目
- ✅ 为方便测试，我额外集成了友盟的手机号一键登录，若无此需求，可删除

### 待完成功能：

- ☑️ 群聊，但是目前android端代码是完全适配群聊功能的，只是群聊的后台接口我还没写好
- ☑️ 音视频通话时候可用浮动小窗播放
- ☑️ 小米手机对接小米push；oppo，vivo同理，目前代码已经封装的很好，再添加或更换第3方push很简单
- ☑️ 聊天界面listView更换为recycleView
- ☑️ 聊天气泡布局再用ConstraintLayout降层级


### 看一下消息的Bean，就明白整体架构
```java

public class ChatBean {

    /**
     * 每一条字段后面的注释表明它的数据来源 ：
     *
     * sqlite表示该变量来自手机本地sqlite数据库，服务器上无此字段，比如 是否已读，是否发送成功
     * service表示该变量来自服务器接口返回
     * service+sqlite 表示服务器也会返回，手机端也会去存
     * temp表示不入库也不是网络请求，只是Activity为了临时处理
     */

    public final static int INPROGRESS = 0;//客户端自己定义的。发送中
    public final static int SUCCESS = 1;//客户端自己定义的。发送成功
    public final static int FAIL = 2;//客户端自己定义的。发送失败

    public final static int TYPE_CHAT_SINGLE = 1;//单聊
    public final static int TYPE_CHAT_GROUP = 2;//群聊

    public final static int SUBTYPE_TIPS = 99;//聊天时候的tips，不入库

    public final static int SUBTYPE_TEXT = 1;//文本消息
    public final static int SUBTYPE_IMAGE = 2;//图片消息
    public final static int SUBTYPE_AUDIO = 3;//语音条消息
    public final static int SUBTYPE_GIFT = 4;//礼物消息

    public final static int SUBTYPE_CALL_AUDIO = 10;//电话消息
    public final static int SUBTYPE_CALL_VIDEO = 11;//视频消息

    private int dbid;//手机端本地数据库主键（sqlite）
    private int msgid;//服务器返回的主键id（service + sqlite）

    //如果是群聊（type==2），这里是发布人id; 如果是单聊，这里是对方id
    //如果是服务器返回的ChatBean，这里是发布人id，也就是对方id；
    //如果是本地发消息时候创建的单聊ChatBean，就用代码设为对方id； 如果是本地发消息时候创建的群聊ChatBean，就用代码设为自己
    private String other_userid; //（service + sqlite）
    private String other_name; //（service + sqlite）
    private String other_photo; //（service + sqlite）

    //如果是群聊（type==2），这里是群id；如果是单聊，这里是对方的userid（同other_userid）
    private String targetid; //（service + sqlite）

    private String content;//消息文本内容 //（service + sqlite）
    private int create_time;//1491545686 //（service + sqlite）
    private int type;//1为单聊 2为群聊 3为聊天时候的tips 4系统通知 //（service + sqlite）
    private int subtype;//0为文本 1为图片 2为语音 目前只可能为0 //（service + sqlite）
    private int issender;//当前登录账号是这个消息的发送者 //（sqlite）
    private int hadread;//0默认，未读 //（sqlite）

    //extend = 服务器返回的json，如果subtype是1图片，那么extend是{"height":2340,"width":1080}
    //如果subtype是2语音条，那么extend是{"duration":"6"}
    private String extend; //（service + sqlite）

    //filename = 附件本地沙盒文件名，同时也是七牛文件名
    //如果subtype是1图片，或者2语音条，那么就是文件名
    //如果subtype是11或者10电话消息，那么就是1611:800018:1586864352 （发送人id:接受人id:时间戳）
    private String filename; //（service + sqlite）

    // 图片
    private float thumbnailImageWidth;//客户端自己定义的，从extend解析出来的图片宽 //（temp)
    private float thumbnailImageHeight;//客户端自己定义的，从extend解析出来的图片高 //（temp)
    private int fileUploadProgress;//附件上传进度，范围为0--100 //（temp)

    //语音条
    private int duration;//秒,从extend解析出来的 //（temp)
    private int played;//0==没播放过，1==播放过，从extend解析出来的 //（temp)

    private int state;//客户端自己定义的。发送状态 //（sqlite)
    private int hisTotalUnReadedChatCount;//客户端自己定义的。与这个人的所有的未读消息数量 // (temp)
//    private String timeLag;//client定义的。直接显示的时间
    private String client_messageid;//客户端自己定义的临时消息id。// (sqlite)
    private boolean needShowTimeTips;//客户端自己定义的。是否要显示时间tips //（temp)
}

```

#### h4 标题
##### h5 标题
###### h6 标题


## 水平线

___

---

***


## 文本样式

**This is bold text**

__This is bold text__

*This is italic text*

_This is italic text_

~~Strikethrough~~


## 列表



+ Create a list by starting a line with `+`, `-`, or `*`
+ Sub-lists are made by indenting 2 spaces:
  - Marker character change forces new list start:
    * Ac tristique libero volutpat at
    + Facilisis in pretium nisl aliquet
    - Nulla volutpat aliquam velit
+ Very easy!

有序

1. Lorem ipsum dolor sit amet
2. Consectetur adipiscing elit
3. Integer molestie lorem at massa


1. You can use sequential numbers...
1. ...or keep all the numbers as `1.`

Start numbering with offset:

57. foo
1. bar


## 代码

Inline `code`

Indented code

    // Some comments
    line 1 of code
    line 2 of code
    line 3 of code


Block code "fences"

```
Sample text here...
```

Syntax highlighting

``` js
var foo = function (bar) {
  return bar++;
};

console.log(foo(5));
```