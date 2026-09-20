package no.uio.ifi.in2000.team39.in2000_project.ui.contact

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.in2000_project.R

/**
 * Main content for the Contact Screen. This function includes input fields for the subject and message,
 * a send button, and a link to restricted airspace information.
 *
 * The [subject] parameter represents the current value of the subject input field, which is updated by the [onSubjectChange] function.
 * The [message] parameter represents the current value of the message input field, which is updated by the [onMessageChange] function.
 * When the send button is clicked, the [onButtonClick] function is invoked.
 * The [innerPadding] parameter specifies the padding within the scaffold, and the [modifier] parameter allows for additional customization.
 */
@Composable
fun ContactScreenMainContent(
    subject: String,
    onSubjectChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SendEmailButton(onButtonClick = onButtonClick)
        CheckAirspaceLink()
        SubjectTextField(subject = subject, onValueChange = onSubjectChange)
        MessageTextField(
            message = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(500.dp)
        )
    }
}

/**
 * Button for sending emails. Triggers [onButtonClick] when pressed.
 */
@Composable
private fun SendEmailButton(onButtonClick: () -> Unit) {
    ElevatedButton(
        onClick = onButtonClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .defaultMinSize(48.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = ButtonDefaults.elevatedButtonElevation(25.dp)
    ) {
        Text(
            text = stringResource(id = R.string.send_email_button),
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Clickable link to check restricted airspace information.
 */
@Composable
fun CheckAirspaceLink() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Check restricted airspace first ")

        val context = LocalContext.current
        val clickableText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textDecoration = TextDecoration.Underline,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            ) {
                append("here")
            }
        }

        ClickableText(
            text = clickableText,
            onClick = {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ippc.no/ippc/index.jsp"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .semantics {
                    contentDescription = "Open restricted airspace information"
                    this.onClick {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.ippc.no/ippc/index.jsp")
                            )
                        context.startActivity(intent)
                        true
                    }
                    role = Role.Button
                }
                .defaultMinSize(48.dp),
        )
    }
}

/**
 * A TextField for the email subject. The [subject] parameter represents the current value of the input field,
 * and the [onValueChange] function updates this value.
 */
@Composable
private fun SubjectTextField(subject: String, onValueChange: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        value = subject,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.subject_text_field)) },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )
}

/**
 * A TextField for the email message. The [message] parameter represents the current value of the input field,
 * and the [onValueChange] function updates this value. The [modifier] parameter allows for additional customization.
 */
@Composable
private fun MessageTextField(message: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    TextField(
        modifier = modifier,
        value = message,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.message_text_field)) },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )
}
