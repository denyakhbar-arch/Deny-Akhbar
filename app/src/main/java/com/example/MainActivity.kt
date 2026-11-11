package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.auth.AuthManager
import com.example.auth.AuthViewModel
import com.example.data.AppViewMode
import com.example.data.DarcorpRepository
import com.example.model.UserProfile
import com.example.ui.screens.LandingPageScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PortalScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val repository = DarcorpRepository()
    private val authManager by lazy { AuthManager(this) }
    private val authViewModel by lazy { AuthViewModel(authManager, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sync repository with persisted authentication session if available
        val currentAuth = authViewModel.uiState.value
        if (currentAuth.isSignedIn) {
            repository.switchUserRole(
                UserProfile(
                    id = currentAuth.uid ?: "usr_persisted",
                    name = currentAuth.displayName ?: "Deny Akhbar",
                    email = currentAuth.email ?: "denyakhbar@gmail.com",
                    role = currentAuth.selectedRole,
                    companyId = "comp_1"
                )
            )
        }

        setContent {
            MyApplicationTheme {
                val viewMode by repository.viewMode.collectAsState()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    when (viewMode) {
                        AppViewMode.LANDING_PAGE -> {
                            LandingPageScreen(
                                repository = repository,
                                authViewModel = authViewModel,
                                onLaunchPortal = {
                                    repository.setViewMode(AppViewMode.APP_PORTAL)
                                },
                                onNavigateToLogin = {
                                    repository.setViewMode(AppViewMode.LOGIN_SCREEN)
                                }
                            )
                        }
                        AppViewMode.LOGIN_SCREEN -> {
                            BackHandler {
                                repository.setViewMode(AppViewMode.LANDING_PAGE)
                            }
                            LoginScreen(
                                repository = repository,
                                authViewModel = authViewModel,
                                onLoginSuccess = { user ->
                                    repository.setViewMode(AppViewMode.APP_PORTAL)
                                },
                                onNavigateToLanding = {
                                    repository.setViewMode(AppViewMode.LANDING_PAGE)
                                }
                            )
                        }
                        AppViewMode.APP_PORTAL -> {
                            BackHandler {
                                repository.setViewMode(AppViewMode.LANDING_PAGE)
                            }
                            PortalScreen(
                                repository = repository,
                                onBackToLanding = {
                                    repository.setViewMode(AppViewMode.LANDING_PAGE)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
