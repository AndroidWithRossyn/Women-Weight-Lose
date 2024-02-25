package com.loseweight.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.loseweight.*
import com.loseweight.adapter.BodyFocusAdapter
import com.loseweight.adapter.ChooseYourPlanAdapter
import com.loseweight.databinding.FragmentAnimationBinding
import com.loseweight.databinding.FragmentPlanBinding
import com.loseweight.utils.Debug
import com.loseweight.utils.Utils

class AnimationFragment : BaseFragment() {

    var binding: FragmentAnimationBinding? =null
    var title: String? = null
    var segment: String? = null
    var imagePath: String? = null
    var rootContext:Context? = null

    companion object {
        fun newInstance(imagePath: String): AnimationFragment {
            val pane = AnimationFragment()
            val args = Bundle()
            args.putString("ImagePath", imagePath)
            pane.arguments = args
            return pane
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rootContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_animation, container, false)
        if (requireArguments() != null && requireArguments().getString("ImagePath").isNullOrEmpty().not()) {
            imagePath = requireArguments().getString("ImagePath")
            Debug.e("imagePath", imagePath)
        }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(imagePath!!)
    }



    public fun init(path:String) {

        if(binding !=null) {
            binding!!.viewFlipper.removeAllViews()
            val listImg: ArrayList<String>? =
                Utils.ReplaceSpacialCharacters(path)?.let { Utils.getAssetItems(rootContext!!, it) }

            if (listImg != null) {
                for (i in 0 until listImg.size) {
                    val imgview = ImageView(rootContext)
                    //            Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                    Glide.with(rootContext!!).load(listImg.get(i)).into(imgview)
                    imgview.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    binding!!.viewFlipper.addView(imgview)
                }
            }

            binding!!.viewFlipper.isAutoStart = true
            binding!!.viewFlipper.setFlipInterval(rootContext!!.resources.getInteger(R.integer.viewfliper_animation))
            binding!!.viewFlipper.startFlipping()
        }

    }

    override fun onResume() {
        super.onResume()

    }
}