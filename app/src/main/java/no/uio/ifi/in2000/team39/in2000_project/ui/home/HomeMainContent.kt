package no.uio.ifi.in2000.team39.in2000_project.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team39.in2000_project.R

/**
 * Constructs the main content of the home screen, which includes a background image, the application logo, and a start button.
 * This function sets up a visually appealing introductory screen for users with a large background image and a centrally aligned button
 * that triggers navigation or other interactions defined in [onButtonClick]. The [modifier] is applied to the column layout to handle
 * padding and alignment.
 */
@Composable
fun HomeMainContent(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackgroundImage(Modifier.matchParentSize())

        ContentColumn(
            onButtonClick = onButtonClick,
            modifier = modifier.align(Alignment.Center)
        )
    }
}

/**
 * Displays the background image. The [modifier] is applied to the image to handle sizing.
 */
@Composable
private fun BackgroundImage(modifier: Modifier) {
    AsyncImage(
        model = "https://i.pinimg.com/736x/87/d8/dc/87d8dc0495f32e8dd3bda359c7b00f0d.jpg",
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

/**
 * Displays the content of the home screen including the logo and the start button.
 *
 * The [onButtonClick] defines the action performed when the button is clicked.
 * The [modifier] is applied to the column layout to handle padding and alignment.
 */
@Composable
private fun ContentColumn(
    onButtonClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 25.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoImage()

        StartButton(onButtonClick = onButtonClick)
    }
}

/**
 * Displays the application logo.
 */
@Composable
private fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.portal_space_logo),
        contentDescription = null,
    )
}

/**
 * Displays the start button that triggers the action defined in [onButtonClick].
 */
@Composable
private fun StartButton(
    onButtonClick: () -> Unit
) {
    Button(
        onClick = onButtonClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(horizontal = 15.dp)
            .padding(bottom = 50.dp)
            .defaultMinSize(48.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = stringResource(id = R.string.start_button),
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
