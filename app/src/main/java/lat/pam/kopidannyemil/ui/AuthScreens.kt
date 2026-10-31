package lat.pam.kopidannyemil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.kopidannyemil.User
import lat.pam.kopidannyemil.hasSameUsername
import lat.pam.kopidannyemil.matches
import lat.pam.kopidannyemil.ui.theme.Caramel
import lat.pam.kopidannyemil.ui.theme.Peach
import lat.pam.kopidannyemil.ui.theme.PeachSoft

@Composable
fun SplashScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(120.dp).clip(CircleShape).background(Peach),
            contentAlignment = Alignment.Center,
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
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            "Sign up",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Join as a member & experience more fun from various meal at Kopi dan Nyemil app",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
        )

        Spacer(Modifier.height(36.dp))

        // Minimalist benefits row
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BenefitItem("☕", "Koleksi Kopi & Minuman Pilihan", "Espresso, matcha, artisan tea segar")
            BenefitItem("🥐", "Camilan & Pastry Fresh Oven", "Croissant renyah, donat lembut & cookie")
            BenefitItem("🚀", "Pemesanan Praktis & Cepat", "Pesan antar langsung ke lokasimu")
        }

        Spacer(Modifier.weight(1f))

        PrimaryButton("Register", onClick = onRegister)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Sudah punya akun? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
            )
            Text(
                "Login",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Caramel,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onLogin() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun BenefitItem(icon: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PeachSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun LoginScreen(
    users: List<User>,
    onBack: () -> Unit,
    onSuccess: (User) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(36.dp))
        Text("Login User", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text("Masuk ke akunmu untuk mulai memesan", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(Modifier.height(20.dp))

        // Interactive autofill banner for demo account
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PeachSoft),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    username = "Keira"
                    password = "123abc"
                    error = null
                },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("💡", fontSize = 20.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Akun Demo: Keira / 123abc", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = Caramel)
                    Text("Ketuk di sini untuk autofill instan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        AppField(username, { username = it; error = null }, "Username")
        Spacer(Modifier.height(12.dp))
        AppField(
            value = password,
            onChange = { password = it; error = null },
            label = "Password",
            password = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "🙈" else "👁️", fontSize = 16.sp)
                }
            },
        )
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Login") {
            val match = users.firstOrNull { it.matches(username, password) }
            if (match != null) onSuccess(match) else error = "Username atau password salah"
        }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Kembali", onClick = onBack)
    }
}

@Composable
fun RegisterScreen(
    users: List<User>,
    onBack: () -> Unit,
    onSuccess: (User) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(36.dp))
        Text("Register", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text("Buat akun barumu untuk menikmati berbagai menu pilihan", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(Modifier.height(24.dp))
        AppField(name, { name = it; error = null }, "Nama lengkap")
        Spacer(Modifier.height(12.dp))
        AppField(username, { username = it; error = null }, "Username")
        Spacer(Modifier.height(12.dp))
        AppField(
            value = password,
            onChange = { password = it; error = null },
            label = "Password (min. 6 karakter)",
            password = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "🙈" else "👁️", fontSize = 16.sp)
                }
            },
        )
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Register") {
            when {
                (name.isBlank() || username.isBlank() || password.isBlank()) ->
                    error = "Semua field wajib diisi"
                password.length < 6 -> error = "Password minimal 6 karakter"
                users.any { it.hasSameUsername(username) } -> error = "Username sudah dipakai"
                else -> onSuccess(User(name.trim(), username.trim(), password))
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Kembali", onClick = onBack)
    }
}