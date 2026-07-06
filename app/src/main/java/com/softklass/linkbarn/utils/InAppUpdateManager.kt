package com.softklass.linkbarn.utils

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch

/**
 * Wraps the Play in-app update flow so it can be reused and kept out of the Activity.
 *
 * The [activityResultLauncher] must be registered by the hosting [activity] during its
 * initialization (via `registerForActivityResult`) and passed in here.
 */
class InAppUpdateManager(
    private val activity: ComponentActivity,
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
) {
    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(activity.applicationContext)
    private val updateType = AppUpdateType.FLEXIBLE

    private val listener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Log.i(TAG, "Update has been downloaded.")
                Toast
                    .makeText(
                        activity,
                        "Update Completed. Restarting application.",
                        Toast.LENGTH_SHORT,
                    ).show()
                activity.lifecycleScope.launch {
                    appUpdateManager.completeUpdate()
                }
            }
        }

    fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                Log.i(TAG, "Update is available.")

                appUpdateManager.registerListener(listener)
                Log.i(TAG, "Starting Update.")
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build(),
                )
            } else {
                Log.i(TAG, "No Update Available.")
            }
        }
    }

    fun unregister() {
        appUpdateManager.unregisterListener(listener)
    }

    companion object {
        private const val TAG = "InAppUpdateManager"
    }
}
