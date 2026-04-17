import androidx.compose.foundation.layout.*;
import androidx.compose.material.MaterialTheme;
import androidx.compose.material.Surface;
import androidx.compose.material.Text;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.dp;

@Composable
fun LoginScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to Tgram TV Unlimited", style = MaterialTheme.typography.h4)
            // Add TextField and Button for login here
        }
    }
}