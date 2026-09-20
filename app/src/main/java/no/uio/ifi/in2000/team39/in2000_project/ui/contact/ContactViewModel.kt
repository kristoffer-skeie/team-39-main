package no.uio.ifi.in2000.team39.in2000_project.ui.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the UI state for the contact form, including the subject and message fields.
 */
data class ContactUiState(
    val subject: String = "",
    val message: String = ""
)

/**
 * Manages the UI state and interactions for a contact form.
 * Provides functions to update subject, message, and send an email.
 */
class ContactViewModel : ViewModel() {
    private val _contactUiState = MutableStateFlow(ContactUiState())
    val contactUiState: StateFlow<ContactUiState> = _contactUiState.asStateFlow()

    /**
     * Updates the subject text in the UI state with [newSubject].
     */
    fun updateSubject(newSubject: String) {
        _contactUiState.update { currentState ->
            currentState.copy(subject = newSubject)
        }
    }

    /**
     * Updates the message text in the UI state with [newMessage].
     */
    fun updateMessage(newMessage: String) {
        _contactUiState.update { currentState ->
            currentState.copy(message = newMessage)
        }
    }

    /**
     * Clears the contact form by resetting the subject and message fields.
     */
    fun clearState() {
        _contactUiState.update {
            ContactUiState()
        }
    }

    /**
     * Attempts to send an email using the provided [context] and the current [subject] and [message].
     * Constructs an email intent and launches it if an email application is available.
     *
     * Logs an error if no suitable application is found.
     */
    fun sendEmail(context: Context, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("momoad910@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.e("ContactViewModel", "Error sending email")
        }
    }
}
