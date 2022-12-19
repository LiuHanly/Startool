package com.glorystar.startool.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.glorystar.startool.config.base.BaseDialogFramene
import com.glorystar.startool.R

class MediaPayFragment : BaseDialogFramene(){


    var mVideoView: VideoView? = null
    var image_name :ImageView ?=null
    companion object {
        /**
         * @param datase 文件类型
         * @param stun  是否为单选
         * @param identifier 文件路径  sp key
         * @param TYPE 文件类型 针对 普通网页和全屏网页sp key
         * */
        fun newInstance(): MediaPayFragment? {
            return  MediaPayFragment()
        }
    }

    override val layoutId: Int
        get() = R.layout.mediapay_fragment

    override fun initView(view: View?, savedInstanceState: Bundle?) {

        mVideoView =view?.findViewById(R.id.ft)
        image_name =view?.findViewById(R.id.image_name)

        if (Build.VERSION.RELEASE.contains("7")){

        }else{
            Log.e("MediaPayFragment","mVideoView?.rotation")
            mVideoView?.rotation = 90f
        }

        if (tag?.contains("mp4") == true){
            playVideo(tag!!)
        }else {
            image_name?.let { Glide.with(this@MediaPayFragment).load(tag).into(it) }
        }

    }
    private fun playVideo( path :String): Boolean {

        val mc = MediaController(activity)
        mVideoView!!.setMediaController(mc)
        //videoView.setVideoURI(Uri.parse(""));
        mVideoView!!.setVideoPath(path)
        mVideoView!!.setOnCompletionListener {

            mVideoView!!.start()
        }
        mVideoView!!.setOnPreparedListener { }
        mVideoView!!.setOnErrorListener { mp, what, extra ->

            mVideoView!!.start()
            false
        }
        mVideoView!!.requestFocus()
        mVideoView!!.start()
        return true
    }

    private fun stopVideo() {
        mVideoView!!.stopPlayback()
    }


}