# TickView
一个精致的打钩小动画，模仿轻芒杂志标记已读的动画


## 效果图
![](https://github.com/ChengangFeng/TickView/blob/master/art/tick_view_animation.gif)

## 使用

### Step 1
``` gradle
allprojects {
  repositories {
    ...
    maven { url 'https://www.jitpack.io' }
  }
}
```

### Step 2
``` gradle
dependencies {
    compile 'com.github.ChengangFeng:TickView:v1.0.2'
}
```

### Step 3

#### xml配置
``` xml
<com.github.chengang.library.TickView
    android:id="@+id/tick_view_accent"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:check_base_color="@color/colorAccent"
    app:rate="normal" />
```

#### 点击事件回调
``` java
tickView.setOnCheckedChangeListener(new TickView.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(TickView tickView, boolean isCheck) {
        //do something here
    }
});
```

#### 模拟点击效果
``` java
tickView.toggle();
```

#### 手动更改控件状态
``` java
tickView.setChecked(true);
```

## 思路实现
* 简书，[http://www.jianshu.com/p/673e3b3715a2](http://www.jianshu.com/p/1b2cdba03d23)
* 掘金，[https://juejin.im/post/59ebe2b75188250989513b1b](https://juejin.im/post/59ebe2b75188250989513b1b)

## 优化思路
* 简书，[http://www.jianshu.com/p/1ff14c0156b0](http://www.jianshu.com/p/1ff14c0156b0)
* 掘金，[https://juejin.im/post/59f5609851882534af2538c0](https://juejin.im/post/59f5609851882534af2538c0)
