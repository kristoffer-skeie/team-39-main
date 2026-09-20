package no.uio.ifi.in2000.team39.in2000_project.ui.contact

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar

/**
 * Displays a contact form allowing users to send an email. Integrates navigation and state management
 * through the use of [NavController] for navigation actions and [ContactViewModel] for handling the email sending logic.
 *
 * The UI is built within a [Scaffold] structure which includes a top bar and a bottom navigation bar. The main content
 * area allows the user to input the subject and message of the email. If the subject and message have been previously
 * entered, the UI state is preserved through [ContactViewModel].
 */
@Composable
fun ContactScreen(
    navController: NavController,
    contactViewModel: ContactViewModel = viewModel(),
) {
    val context = LocalContext.current
    val contactUiState by contactViewModel.contactUiState.collectAsState()
    val settingsRoute = stringResource(id = R.string.setting_screen_route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            ContactScreenTopBar(navController, settingsRoute)
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        ContactScreenMainContent(
            subject = contactUiState.subject,
            onSubjectChange = contactViewModel::updateSubject,
            message = contactUiState.message,
            onMessageChange = contactViewModel::updateMessage,
            innerPadding = innerPadding,
            onButtonClick = {
                handleButtonClick(
                    context = context,
                    contactViewModel = contactViewModel,
                    subject = contactUiState.subject,
                    message = contactUiState.message
                )
            }
        )
    }
}

/**
 * Displays the top bar for the contact screen, integrating navigation actions.
 *
 * Uses [NavController] for handling navigation actions and takes a [settingsRoute] string for navigating to the settings screen.
 */
@Composable
fun ContactScreenTopBar(navController: NavController, settingsRoute: String) {
    TopBar(
        onBackClick = { navController.popBackStack() },
        onSettingsClick = { navController.navigate(settingsRoute) },
        title = stringResource(id = R.string.contact_screen_title)
    )
}

/**
 * Handles the button click event for sending an email and clearing the state.
 *
 * Utilizes the provided [context] for accessing resources, [contactViewModel] for managing the state and logic,
 * and the provided [subject] and [message] for the email content.
 */
fun handleButtonClick(
    context: Context,
    contactViewModel: ContactViewModel,
    subject: String,
    message: String
) {
    contactViewModel.sendEmail(context, subject, message)
    contactViewModel.clearState()
}
