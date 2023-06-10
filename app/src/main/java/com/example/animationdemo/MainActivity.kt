package com.example.animationdemo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.animationdemo.databinding.ActivityMainBinding
import com.example.animationdemo.utils.AnimationManager
import com.example.animationdemo.utils.FlyEntity
import com.example.animationdemo.utils.dp2Px

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        binding!!.avatarOne.setOnClickListener {
            flyAvatarView(binding!!.avatarOne, binding!!.avatarTwo)
        }
    }

    /**
     * 位移起始位置
     * @return
     */
    private fun getStartPoint(startView: ImageView): IntArray {
        val startPosArray = intArrayOf(0, 0)
        startView.getLocationOnScreen(startPosArray)
        //起始坐标移至起始头像中心点
//        startPosArray[0] = startPosArray[0] + startView.width / 2
//        startPosArray[1] = startPosArray[1] + startView.height / 2
        return startPosArray
    }

    /**
     * 飞向终止位置位移
     */
    private fun flyAvatarView(startView: ImageView, endView: ImageView) {
        val flyEntities = ArrayList<FlyEntity>()
        val viewCoordinate = endView.getTag(R.id.personal_avatar)  //坐标复用
        var endPosArray: IntArray? = null
        viewCoordinate?.let { coordinate ->
            endPosArray = coordinate as IntArray
        } ?: kotlin.run {
            endPosArray = IntArray(2).apply {
                endView.getLocationOnScreen(this)   //头像处于屏幕坐标-获取的是左上角点的坐标
            }
            // === 方法1 ===
//            endPosArray!![0] = endPosArray!![0] + endView.width / 2 - 50f.dp2Px()   //50f.dp2Px():再通过数值微调
//            endPosArray!![1] = endPosArray!![1] + endView.height / 2 - 120f.dp2Px()
            // === 方法2 ===
            //由于起始偏移加了startView.marginStart，对应终止偏移也要加startView.marginStart
            endPosArray!![0] = endPosArray!![0] + endView.width / 2 + startView.marginStart  //endView.width / 2 是为了偏移至头像中心点
            endPosArray!![1] = endPosArray!![1] + endView.height / 2 + startView.marginTop
            endView.setTag(R.id.personal_avatar, endPosArray)
        }
        flyEntities.add(FlyEntity(getStartPoint(startView), endPosArray, null))
        AnimationManager.instance.parentView = binding?.flOver
        AnimationManager.instance.startFly(flyEntities)
    }

    override fun onDestroy() {
        AnimationManager.instance.onDestroyView()
        super.onDestroy()
    }
}