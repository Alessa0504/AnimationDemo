package com.example.animationdemo.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.animationdemo.R

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/6/10 14:09
 */
class AnimationManager {

    companion object {
        val TAG = AnimationManager::class.java.simpleName
        val instance = AnimationManager()
    }

    var parentView: FrameLayout? = null

    fun startFly(flyEntityData: ArrayList<FlyEntity>) {
        parentView ?: return
        flyEntityData.forEach { flyEntity ->
            val imageView = createImageView(flyEntity)  //创建ImageView蒙层
            imageView?.let {
                //给蒙层设置图片
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        parentView?.context!!,
                        R.drawable.fly_tips
                    )
                )
                //位移动画
                flyAnimation(it, flyEntity.startPos, flyEntity.endPos)
            }
        }
    }

    /**
     * 创建ImageView蒙层
     * @param flyEntity
     * @return
     */
    private fun createImageView(flyEntity: FlyEntity): ImageView? {
        parentView ?: return null
        flyEntity.startPos ?: return null
        //创建imageView
        val imageView = ImageView(parentView?.context)
        val layoutParams = FrameLayout.LayoutParams(80f.dp2Px(), 80f.dp2Px())
        // === 方法1 ===
//        layoutParams.setMargins(flyEntity.startPos!![0], flyEntity.startPos!![1], 0, 0)  //flyEntity.startPos是startView的左上角坐标，相当于margin
        imageView.layoutParams = layoutParams
        parentView?.addView(imageView)
        return imageView
    }

    /**
     * 位移动画
     * @param flyImageView
     * @param startPos
     * @param endPos
     */
    private fun flyAnimation(flyImageView: ImageView, startPos: IntArray?, endPos: IntArray?) {
        if (startPos == null || endPos == null) {
            parentView?.removeView(flyImageView)
            return
        }
        //x轴、y轴位移
        // === 方法1 ===
//        val translationX = ObjectAnimator.ofFloat(
//            flyImageView,
//            "translationX",
//            0f,  //!注：这是相对于flyImageView的起始&终止偏移量，不设margin情况下
//            (endPos[0] - startPos[0]).toFloat()   //[0]都是横坐标，注意translation是算位移所以(endPos[0] - startPos[0])，0f表示一开始没有偏移
//        )
//        val translationY = ObjectAnimator.ofFloat(
//            flyImageView,
//            "translationY",
//            0f,
//            (endPos[1] - startPos[1]).toFloat()
//        )

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

        translationX.duration = 900
        translationY.duration = 900

        //todo 增添其他动画效果

        val animatorSet = AnimatorSet()
        //OvershootInterpolator,效果开始向前甩，冲到目标值，最后又回到了最终值 https://blog.csdn.net/weixin_43942430/article/details/104063201
        animatorSet.interpolator = OvershootInterpolator(1.5f)
        //设置要播放的动画
        animatorSet.playTogether(translationX, translationY)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            //只监听动画结束
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //移除蒙层，最后不需要展示
                flyImageView.setImageDrawable(null)
                parentView?.removeView(flyImageView)
                Log.i(TAG, "位移动画 - 结束 移除view  childCount = ${parentView?.childCount}")
            }
        })
        //开始播放
        animatorSet.start()
    }

    fun onDestroyView() {
        parentView = null
    }
}

class FlyEntity(var startPos: IntArray?, var endPos: IntArray?, var resourceUrl: String?)