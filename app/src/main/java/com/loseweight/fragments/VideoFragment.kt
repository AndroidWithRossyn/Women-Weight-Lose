package com.loseweight.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.loseweight.R
import com.loseweight.databinding.FragmentVideoBinding
import com.loseweight.utils.Debug


class VideoFragment : BaseFragment() {

    lateinit var binding: FragmentVideoBinding
    var videoId: String? = null
    var rootContext: Context? = null
    var player: YouTubePlayer? = null
    var youTubePlayerFragment: Fragment? = null

    companion object {
        fun newInstance(videoId: String): VideoFragment {
            val pane = VideoFragment()
            val args = Bundle()
            args.putString("videoId", videoId)
            pane.arguments = args
            return pane
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rootContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false)
        if (requireArguments() != null && requireArguments().getString("videoId").isNullOrEmpty()
                .not()
        ) {
            videoId = requireArguments().getString("videoId")
            Debug.e("videoId", videoId)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    private fun init() {

        val youTubePlayerFragment = childFragmentManager.findFragmentById(R.id.youtubesupportfragment)
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.youtubesupportfragment, youTubePlayerFragment!!).commit()

        (youTubePlayerFragment as YouTubePlayerSupportFragment).initialize(
            getString(R.string.youtube_api_key),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    arg0: YouTubePlayer.Provider?,
                    youTubePlayer: YouTubePlayer,
                    b: Boolean
                ) {
                    if (!b) {
                            player = youTubePlayer
                        player?.cueVideo(videoId!!.substringAfter("v="))
                    }
                }

                override fun onInitializationFailure(
                    arg0: YouTubePlayer.Provider?,
                    arg1: YouTubeInitializationResult?
                ) {
                    if (arg1 != null) {
                        Debug.e("", arg1.name)
                    }
                }
            })


    }

    fun setVideo(videoId: String) {
        this.videoId = videoId
        if (player != null) {
            player!!.release()
            player = null
        }
            init()

    }

    override fun onResume() {
        super.onResume()
    }

    inner class ClickHandler {

    }
}