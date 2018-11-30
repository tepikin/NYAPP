package com.lazard.nyapp.nyapp.ui.edit

import android.Manifest
import android.app.Activity
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener
import org.jetbrains.anko.contentView


class CheckPermissions(val activity: Activity) {
    fun checkPermissions(onSuccess: () -> Unit) {

        val viewForSnackBar: View? = activity.contentView
        val snackbarPermissionListener = SnackbarOnDeniedPermissionListener.Builder
            .with(viewForSnackBar, "Write permissions need")
            .withOpenSettingsButton("Settings")
            .build()

        val basePermissionListener = object : BasePermissionListener() {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                onSuccess()
            }
        }

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(CompositePermissionListener(snackbarPermissionListener, basePermissionListener)).check()
    }
}