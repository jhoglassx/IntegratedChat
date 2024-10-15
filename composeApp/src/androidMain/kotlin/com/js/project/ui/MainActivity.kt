package com.js.project.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.js.project.provider.DispatcherProvider
import com.js.project.service.ServerService
import com.js.project.ui.app.App
import com.js.project.ui.auth.AuthViewModel
import com.js.project.ui.auth.model.AuthAction
import com.js.project.ui.auth.model.AuthState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val authManager: ServerService by inject()
    private val dispatcher: DispatcherProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authViewModel: AuthViewModel by inject()

            val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
            if (uiState.authGoogleIntent == null) {
                authViewModel.onAction(AuthAction.GetGoogleSignInIntent)
            }
            if (uiState.authTwitchIntent == null) {
                authViewModel.onAction(AuthAction.GetTwitchSignInIntent)
            }

            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        val intent = result.data
                        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                        val account = task.getResult(ApiException::class.java)
                        val authorizationCode = account?.serverAuthCode
                        authViewModel.onAction(
                            AuthAction.GetGoogleUser(authorizationCode)
                        )
                    }
                }
            )

            App(
                onSignInGoogle = {
                    startGoogleSignInIntent(googleSignInLauncher, uiState)
                },
                onSignInTwitch = {
                    startTwitchSignInIntent(uiState, authViewModel)
                },
                authState = uiState
            )
        }
    }

    private fun startGoogleSignInIntent(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        uiState: AuthState
    ) {
        lifecycleScope.launch {
            val signInIntent = uiState.authGoogleIntent as Intent
            launcher.launch(signInIntent)
        }
    }

    private fun startTwitchSignInIntent(
        uiState: AuthState,
        authViewModel: AuthViewModel
    ) {
        lifecycleScope.launch {
            withContext(dispatcher.IO) {
                val uri = uiState.authTwitchIntent as String
                handleServerTwitch(authViewModel)

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                application.startActivity(intent)
            }
        }
    }

    private fun handleServerTwitch(authViewModel: AuthViewModel) {
        authManager.server(8080) { code ->
            if (code != null) {
                authViewModel.onAction(AuthAction.GetTwitchUser(code))

                Logger.i(
                    tag = "MainActivity", Throwable(code.toString())
                ) {
                    "handleServerTwitch -> Authorization code received: $code"
                }

            } else {
                Logger.e(
                    tag = "MainActivity", Throwable()
                ) {
                    "handleServerTwitch -> handleServerTwitch: Authorization code not found"
                }
            }
            authManager.server(8080){}.stop(1000, 10000)
        }
    }
}


@Preview(device = "id:Nexus 10")
@Composable
fun AppAndroidPreview() {
    //App(userInfo, { signIn() }) { signOut() }
}