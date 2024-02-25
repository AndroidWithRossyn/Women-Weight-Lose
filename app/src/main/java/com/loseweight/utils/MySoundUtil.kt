package com.loseweight.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.loseweight.R
import java.util.*

class MySoundUtil {
    var SOUND_DING = 1
    var SOUND_WHISTLE = 0
    var SOUND_CHEER = 2

    @SuppressLint("StaticFieldLeak")
    private var utils: MySoundUtil? = null
    private var audioManager: AudioManager? = null
    private var soundMap: MutableMap<Int?, Int?>? = null
    private var soundPool: SoundPool? = null
    private var ttsSoundPool: SoundPool? = null
    private var context: Context? = null

    constructor(context: Context) {
        init(context)
        this.context = context
    }

    @Synchronized
    fun getInstance(context: Context): MySoundUtil? {
        var mySoundUtil: MySoundUtil?
        synchronized(MySoundUtil::class.java) {
            if (utils == null) {
                utils = MySoundUtil(context)
            }
            mySoundUtil = utils
        }
        return mySoundUtil
    }

    fun init(context: Context) {
        try {
            //soundPool = new SoundPool(3, 3, 0);
            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .build()
            soundMap = HashMap<Int?, Int?>()
            soundMap!![SOUND_WHISTLE] = soundPool!!.load(context, R.raw.whistle, 1)
            soundMap!![SOUND_DING] = soundPool!!.load(context, R.raw.ding, 1)
            soundMap!![SOUND_CHEER] = soundPool!!.load(context, R.raw.cheer, 1)
            audioManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            ttsSoundPool = SoundPool(1, 3, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSound(sound_index: Int) {
        try {
            if (!Utils.getPref(context!!,Constant.PREF_IS_SOUND_MUTE,false)) {
                if (soundPool != null && soundMap != null && audioManager != null) {
                    soundPool!!.play(
                        soundMap!![sound_index]!!
                        , 1.0f
                        , 1.0f, 1, 0, 1.0f
                    )

                    //            Log.e("--play sound-", "--played--");
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unInit() {
        if (ttsSoundPool != null) {
            ttsSoundPool!!.release()
        }
    }
}