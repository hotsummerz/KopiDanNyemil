package lat.pam.kopidannyemil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.kopidannyemil.User
import lat.pam.kopidannyemil.ui.theme.Peach

@Composable
fun SplashScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(120.dp).clip(CircleShape).background(Peach),
            contentAlignment = Alignment.Center
        ) { Text("☕", fontSize = 56.sp) }
        Spacer(Modifier.height(24.dp))
        Text("Kopi dan Nyemil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Get a taste of your wish", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.weight(1f))
        PrimaryButton("Start now", onClick = onStart)
    }
}

@Composable
fun SignUpScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text("Sign up", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text("Join as a member & experience more fun from various meal at Kopi dan Nyemil app")
        Spacer(Modifier.weight(1f))
        PrimaryButton("Register", onClick = onRegister)
        Spacer(Modifier.height(12.dp))
        OutlineButton("Login", onClick = onLogin)
    }
}

@Composable
fun LoginScreen(
    users: List<User>,
    onSuccess: (User) -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(48.dp))
        Text("Login User", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Akun demo: Keira / 123abc", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))
        AppField(username, { username = it; error = null }, "Username")
        Spacer(Modifier.height(12.dp))
        AppField(password, { password = it; error = null }, "Password", password = true)
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Login") {
            val user = users.firstOrNull { u -> u.username == username.trim() && u.password == password }
            if (user != null) onSuccess(user) else error = "Username atau password salah"
        }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Kembali", onClick = onBack)
    }
}

@Composable
fun RegisterScreen(
    users: List<User>,
    onSuccess: (User) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(48.dp))
        Text("Register", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(24.dp))
        AppField(name, { name = it; error = null }, "Nama lengkap")
        Spacer(Modifier.height(12.dp))
        AppField(username, { username = it; error = null }, "Username")
        Spacer(Modifier.height(12.dp))
        AppField(password, { password = it; error = null }, "Password", password = true)
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Register") {
            when {
                name.isBlank() || username.isBlank() || password.isBlank() ->
                    error = "Semua field wajib diisi"
                password.length < 6 -> error = "Password minimal 6 karakter"
                users.any { it.username == username.trim() } -> error = "Username sudah dipakai"
                else -> onSuccess(User(name.trim(), username.trim(), password))
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Kembali", onClick = onBack)
    }
}
