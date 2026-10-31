package lat.pam.kopidannyemil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import lat.pam.kopidannyemil.ui.*
import lat.pam.kopidannyemil.ui.theme.KopiDanNyemilTheme

enum class Screen { Splash, SignUp, Login, Register, Home, Order, Profile, Confirm }

class AppState {
    var screen by mutableStateOf(Screen.Splash)
    val users = mutableStateListOf(User("Keira Arina", "Keira", "123abc"))
    var currentUser by mutableStateOf<User?>(null)
    val cart = mutableStateMapOf<Int, Int>()
    var lastReceipt by mutableStateOf<OrderReceipt?>(null)

    val cartCount get() = cart.values.sum()

    fun add(item: MenuItem) { cart[item.id] = (cart[item.id] ?: 0) + 1 }

    fun remove(item: MenuItem) {
        val qty = (cart[item.id] ?: 0) - 1
        if (qty <= 0) cart.remove(item.id) else cart[item.id] = qty
    }

    fun logout() {
        currentUser = null
        cart.clear()
        lastReceipt = null
        screen = Screen.SignUp
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { KopiDanNyemilTheme { Surface(Modifier.fillMaxSize()) { KopiApp() } } }
    }
}

@Composable
fun KopiApp() {
    val state = remember { AppState() }
    val name = state.currentUser?.fullName ?: ""

    BackHandler(enabled = state.screen != Screen.Splash) {
        state.screen = when (state.screen) {
            Screen.SignUp -> Screen.Splash
            Screen.Login, Screen.Register -> Screen.SignUp
            Screen.Order, Screen.Profile -> Screen.Home
            Screen.Confirm -> Screen.Home
            else -> state.screen
        }
    }

    when (state.screen) {
        Screen.Splash -> SplashScreen { state.screen = Screen.SignUp }

        Screen.SignUp -> SignUpScreen(
            onRegister = { state.screen = Screen.Register },
        ) { state.screen = Screen.Login }

        Screen.Login -> LoginScreen(
            users = state.users,
            onBack = { state.screen = Screen.SignUp },
        ) { user ->
            state.currentUser = user
            state.screen = Screen.Home
        }

        Screen.Register -> RegisterScreen(
            users = state.users,
            onBack = { state.screen = Screen.SignUp },
        ) { user ->
            state.users.add(user)
            state.currentUser = user
            state.screen = Screen.Home
        }

        Screen.Confirm -> ConfirmScreen(receipt = state.lastReceipt) {
            state.cart.clear()
            state.lastReceipt = null
            state.screen = Screen.Home
        }

        Screen.Home, Screen.Order, Screen.Profile -> {
            val tab = when (state.screen) {
                Screen.Order -> Tab.Order
                Screen.Profile -> Tab.Profile
                else -> Tab.Home
            }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    BottomBar(selected = tab, cartCount = state.cartCount) { picked ->
                        state.screen = when (picked) {
                            Tab.Home -> Screen.Home
                            Tab.Order -> Screen.Order
                            Tab.Profile -> Screen.Profile
                        }
                    }
                },
            ) { paddingValues ->
                Surface(Modifier.padding(paddingValues)) {
                    when (tab) {
                        Tab.Home -> HomeScreen(name, state.cart) { item ->
                            state.add(item)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = "+1 ${item.name} ditambahkan",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }

                        Tab.Order -> OrderScreen(
                            cart = state.cart,
                            defaultName = name,
                            onAdd = state::add,
                            onRemove = state::remove,
                        ) { receipt ->
                            state.lastReceipt = receipt
                            state.screen = Screen.Confirm
                        }

                        Tab.Profile -> ProfileScreen(
                            user = state.currentUser,
                            existingUsers = state.users,
                            onSave = { fullName, newUsername ->
                                val oldUser = state.currentUser
                                val updatedUser = oldUser?.copy(fullName = fullName, username = newUsername)
                                if ((oldUser != null) && (updatedUser != null)) {
                                    val index = state.users.indexOf(oldUser)
                                    if (index != -1) {
                                        state.users[index] = updatedUser
                                    }
                                    state.currentUser = updatedUser
                                }
                            },
                            onLogout = state::logout,
                        )
                    }
                }
            }
        }
    }
}