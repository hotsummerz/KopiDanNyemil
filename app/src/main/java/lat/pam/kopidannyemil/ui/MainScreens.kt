package lat.pam.kopidannyemil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.kopidannyemil.MenuItem
import lat.pam.kopidannyemil.dummyMenu
import lat.pam.kopidannyemil.rupiah
import lat.pam.kopidannyemil.ui.theme.*

enum class Tab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Order("Order", Icons.Filled.ShoppingCart),
    Profile("Profile", Icons.Filled.Person)
}

@Composable
fun BottomBar(selected: Tab, cartCount: Int, onSelect: (Tab) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        Tab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = {
                    if ((tab == Tab.Order) && (cartCount > 0)) {
                        BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) { Icon(tab.icon, tab.label) }
                    } else Icon(tab.icon, tab.label)
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Ink,
                    indicatorColor = Ink,
                ),
            )
        }
    }
}


@Composable
fun HomeScreen(userName: String, cart: Map<Int, Int>, onAdd: (MenuItem) -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
    ) {
        item {
            Text("Halo, $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text("Makanan & Minuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(dummyMenu) { item ->
            MenuCard(item, cart[item.id] ?: 0) { onAdd(item) }
        }
    }
}

@Composable
private fun MenuCard(item: MenuItem, qty: Int, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PeachSoft),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Peach),
                contentAlignment = Alignment.Center,
            ) { Text(item.emoji, fontSize = 30.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text("[${item.category.displayName}] ${item.description}", style = MaterialTheme.typography.bodySmall)
                Text(rupiah(item.price), fontWeight = FontWeight.SemiBold, color = Caramel)
            }
            FilledIconButton(
                onClick = onAdd,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Ink),
            ) {
                if (qty > 0) Text(qty.toString(), color = Color.White) else Icon(Icons.Filled.Add, "Tambah", tint = Color.White)
            }
        }
    }
}

@Composable
fun ProfileScreen(userName: String, onSave: (String) -> Unit, onLogout: () -> Unit) {
    var name by remember(userName) { mutableStateOf(userName) }
    var saved by remember { mutableStateOf(value = false) }
    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Halo, $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        Text("Profile Saya", fontWeight = FontWeight.Bold)
        Text("Profile Setting", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(16.dp))
        AppField(name, { name = it; saved = false }, "Nama")
        if (saved) Text("Profil tersimpan ✓", color = Caramel, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Save", enabled = name.isNotBlank()) { onSave(name.trim()); saved = true }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Logout", onClick = onLogout)
    }
}

@Composable
fun OrderScreen(
    cart: Map<Int, Int>,
    defaultName: String,
    onRemove: (MenuItem) -> Unit,
    onSubmit: () -> Unit,
) {
    var fullName by remember(defaultName) { mutableStateOf(defaultName) }
    var address by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val lines = dummyMenu.mapNotNull { item ->
        val qty = cart[item.id] ?: 0
        if (qty > 0) item to qty else null
    }
    val total = lines.sumOf { (item, qty) -> item.price * qty }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Alamat", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(16.dp))
        AppField(fullName, { fullName = it; error = null }, "Nama Lengkap")
        Spacer(Modifier.height(12.dp))
        AppField(address, { address = it; error = null }, "Alamat", singleLine = false, minLines = 2)
        Spacer(Modifier.height(12.dp))
        AppField(landmark, { landmark = it }, "Patokan atau detail lengkap", singleLine = false, minLines = 2)
        Spacer(Modifier.height(20.dp))
        Text("Ringkasan Pesanan", fontWeight = FontWeight.Bold)
        if (lines.isEmpty()) {
            Text("Keranjang kosong. Pilih menu di Home dulu.", style = MaterialTheme.typography.bodySmall)
        } else {
            lines.forEach { (item, qty) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${item.emoji} ${item.name} x$qty", Modifier.weight(1f))
                    Text(rupiah(item.price * qty))
                    TextButton(onClick = { onRemove(item) }) { Text("−") }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Total", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(rupiah(total), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Order dan Kirim", enabled = lines.isNotEmpty()) {
            if (fullName.isBlank() || address.isBlank()) error = "Nama dan alamat wajib diisi"
            else onSubmit()
        }
    }
}

@Composable
fun ConfirmScreen(onOk: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(110.dp).clip(CircleShape).background(MintOk),
            contentAlignment = Alignment.Center,
        ) { Text("✅", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0B7A3E)) }
        Spacer(Modifier.height(24.dp))
        Text(
            "Terima kasih!\nKurir kami akan segera mengantar pesananmu :)",
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        PrimaryButton("Oke", onClick = onOk)
    }
}
