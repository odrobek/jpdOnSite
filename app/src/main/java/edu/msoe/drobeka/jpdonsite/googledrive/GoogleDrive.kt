/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * GoogleDrive.kt
 */
package edu.msoe.drobeka.jpdonsite.googledrive

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import edu.msoe.drobeka.jpdonsite.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

private const val TAG = "GoogleDrive"

class GoogleDrive private constructor(
    private val context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    var drive: Drive

    init {
        drive = getDriveService(context)
    }

    private fun getDriveService(context: Context): Drive {
        GoogleSignIn.getLastSignedInAccount(context).let { googleAccount ->
            val credential = GoogleAccountCredential.usingOAuth2(
                context, listOf(DriveScopes.DRIVE)
            )
            credential.selectedAccount = googleAccount!!.account!!
            return Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        }
    }


    companion object {
        private var INSTANCE: GoogleDrive? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = GoogleDrive(context)
            }
        }

        fun get(): GoogleDrive {
            return INSTANCE ?:
            throw IllegalStateException("GoogleDrive must be initialized")
        }
    }
}