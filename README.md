# AnimationDemo
2个view之间的投掷动画。
## xml中设置了layout_margin需要另外计算起/终点偏移
原起始view设置了layout_margin后，蒙层view也要设置同样的margin才能保证起始位置是在原起始view上。原终止view同理。
### 解决方式1
如果是用无偏移量，
```
// === 方法1 ===
val translationX = ObjectAnimator.ofFloat(
    flyImageView,
    "translationX",
    0f,  //!注：这是相对于flyImageView的起始&终止偏移量，不设margin情况下
    (endPos[0] - startPos[0]).toFloat()   //[0]都是横坐标，注意translation是算位移所以(endPos[0] - startPos[0])，0f表示一开始没有偏移
)
val translationY = ObjectAnimator.ofFloat(
    flyImageView,
    "translationY",
    0f,
    (endPos[1] - startPos[1]).toFloat()
)
```
就需要在创建蒙层view的时候也设置margin做偏移，
```
val imageView = ImageView(parentView?.context)
val layoutParams = FrameLayout.LayoutParams(80f.dp2Px(), 80f.dp2Px())
// === 方法1 ===
layoutParams.setMargins(flyEntity.startPos!![0], flyEntity.startPos!![1], 0, 0)  //flyEntity.startPos是startView的左上角坐标，相当于margin
imageView.layoutParams = layoutParams
parentView?.addView(imageView)
```
并且在终止位置endPos调整偏移量，
```
endPosArray = IntArray(2).apply {
            endView.getLocationOnScreen(this)   //头像处于屏幕坐标-获取的是左上角点的坐标
        }
// === 方法1 ===        
endPosArray!![0] = endPosArray!![0] + endView.width / 2 - 50f.dp2Px()   //50f.dp2Px():再通过数值微调
endPosArray!![1] = endPosArray!![1] + endView.height / 2 - 120f.dp2Px()
```
### 解决方式2
起始和终止的偏移量都带入margin，
```
// === 方法2 ===
val translationX = ObjectAnimator.ofFloat(
    flyImageView,
    "translationX",
    //!注：这是相对于flyImageView的起始&终止偏移量，但如果view设置了margin，就只能这样获取
    (startPos[0] + flyImageView.marginStart).toFloat(),
    (endPos[0] - startPos[0] - 40f.dp2Px()).toFloat()   //40f.dp2Px()是endView的marginEnd
)
val translationY = ObjectAnimator.ofFloat(
    flyImageView,
    "translationY",
    (startPos[1] + flyImageView.marginTop).toFloat(),
    (endPos[1] - startPos[1]).toFloat()
)
```
且
```
// === 方法2 ===
//由于起始偏移加了startView.marginStart，对应终止偏移也要加startView.marginStart
endPosArray!![0] = endPosArray!![0] + endView.width / 2 + startView.marginStart  //endView.width / 2 是为了偏移至头像中心点
endPosArray!![1] = endPosArray!![1] + endView.height / 2 + startView.marginTop
```



