package lat.pam.kopidannyemil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.ShoppingCart
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
import lat.pam.kopidannyemil.MenuCategory
import lat.pam.kopidannyemil.MenuItem
import lat.pam.kopidannyemil.OrderReceipt
import lat.pam.kopidannyemil.User
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
    var selectedCategory by remember { mutableStateOf<MenuCategory?>(null) }
    val filteredMenu = remember(selectedCategory) {
        val cat = selectedCategory
        if (cat == null) dummyMenu
        else dummyMenu.filter { (_, _, _, _, _, category) -> category == cat }
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
    ) {
        item {
            Text("Halo, $userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text("Makanan & Minuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Ink,
                        selectedLabelColor = Color.White,
                    ),
                    shape = RoundedCornerShape(20.dp),
                )
                MenuCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Ink,
                            selectedLabelColor = Color.White,
                        ),
                        shape = RoundedCornerShape(20.dp),
                    )
                }
            }
        }
        items(filteredMenu) { item ->
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
fun ProfileScreen(
    user: User?,
    existingUsers: List<User>,
    onSave: (fullName: String, username: String) -> Unit,
    onLogout: () -> Unit,
) {
    val initialName = user?.fullName ?: ""
    val initialUsername = user?.username ?: ""
    var name by remember(initialName) { mutableStateOf(initialName) }
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var error by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(value = false) }

    val initials = remember(name) {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
            (parts.size == 1) && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "U"
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Profile Saya", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(20.dp))

        // Avatar Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PeachSoft),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Caramel),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(name.ifBlank { "User" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("@${username.ifBlank { "username" }}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Peach,
                    ) {
                        Text(
                            "☕ Member Kopi & Nyemil",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Ink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Pengaturan Profil", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(12.dp))
        AppField(name, { name = it; saved = false; error = null }, "Nama Lengkap")
        Spacer(Modifier.height(12.dp))
        AppField(username, { username = it; saved = false; error = null }, "Username")
        Spacer(Modifier.height(8.dp))
        ErrorText(error)
        if (saved) Text("Profil tersimpan ✓", color = Caramel, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Save Perubahan", enabled = name.isNotBlank() && username.isNotBlank()) {
            val trimmedUser = username.trim()
            val isDuplicate = existingUsers.any { u ->
                (u != user) && u.username.equals(trimmedUser, ignoreCase = true)
            }
            if (isDuplicate) {
                error = "Username sudah dipakai oleh pengguna lain"
            } else {
                onSave(name.trim(), trimmedUser)
                saved = true
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlineButton("Logout", onClick = onLogout)
    }
}

@Composable
fun OrderScreen(
    cart: Map<Int, Int>,
    defaultName: String,
    onAdd: (MenuItem) -> Unit,
    onRemove: (MenuItem) -> Unit,
    onSubmit: (OrderReceipt) -> Unit,
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
    val totalItems = lines.sumOf { (_, qty) -> qty }

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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PeachSoft),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Peach),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Keranjang Kosong",
                            tint = Caramel,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Keranjang Belanja Masih Kosong",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Pilih kopi atau camilan favoritmu di menu Home yuk!",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                    )
                }
            }
        } else {
            lines.forEach { (item, qty) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${item.emoji} ${item.name}", Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Text(rupiah(item.price * qty), modifier = Modifier.padding(end = 8.dp))
                    FilledIconButton(
                        onClick = { onRemove(item) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PeachSoft,
                            contentColor = Caramel,
                        ),
                    ) { Text("−", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
                    Text(
                        qty.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    FilledIconButton(
                        onClick = { onAdd(item) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Ink,
                            contentColor = Color.White,
                        ),
                    ) { Text("+", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
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
            if (fullName.isBlank() || address.isBlank()) {
                error = "Nama dan alamat wajib diisi"
            } else {
                onSubmit(
                    OrderReceipt(
                        recipientName = fullName.trim(),
                        address = address.trim(),
                        landmark = landmark.trim(),
                        totalAmount = total,
                        itemCount = totalItems,
                    ),
                )
            }
        }
    }
}

@Composable
fun ConfirmScreen(receipt: OrderReceipt?, onOk: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(100.dp).clip(CircleShape).background(MintOk),
            contentAlignment = Alignment.Center,
        ) { Text("✅", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0B7A3E)) }
        Spacer(Modifier.height(20.dp))
        Text(
            "Pesanan Berhasil!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Kurir kami akan segera mengantar pesananmu :)",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
        )

        if (receipt != null) {
            Spacer(Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PeachSoft),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Detail Pengiriman", fontWeight = FontWeight.Bold, color = Caramel)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Peach)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Penerima", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text(receipt.recipientName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Alamat", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text(receipt.address, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp), textAlign = TextAlign.End)
                    }
                    if (receipt.landmark.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Patokan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(receipt.landmark, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp), textAlign = TextAlign.End)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Item", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${receipt.itemCount} item", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Peach)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Bayar", fontWeight = FontWeight.Bold)
                        Text(rupiah(receipt.totalAmount), fontWeight = FontWeight.ExtraBold, color = Caramel)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Kembali ke Home", onClick = onOk)
    }
}
