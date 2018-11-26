package com.lazard.nyapp.nyapp.util

import android.animation.Animator

class SimpleAnimatorListener(
    val onAnimationRepeat: ((Animator?) -> Unit)? = null,
    val onAnimationCancel: ((Animator?) -> Unit)? = null,
    val onAnimationStart: ((Animator?) -> Unit)? = null,
    val onAnimationEnd: ((Animator?) -> Unit)? = null
) : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
        onAnimationRepeat?.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animator?) {
        onAnimationEnd?.invoke(animation);
    }

    override fun onAnimationCancel(animation: Animator?) {
        onAnimationCancel?.invoke(animation)
    }

    override fun onAnimationStart(animation: Animator?) {
        onAnimationStart?.invoke(animation)
    }

}