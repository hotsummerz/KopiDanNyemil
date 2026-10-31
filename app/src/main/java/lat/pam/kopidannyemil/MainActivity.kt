package lat.pam.kopidannyemil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import lat.pam.kopidannyemil.ui.*
import lat.pam.kopidannyemil.ui.theme.KopiDanNyemilTheme

enum class Screen { Splash, SignUp, Login, Register, Home, Order, Profile, Confirm }

class AppState {
    var screen by mutableStateOf(Screen.Splash)
    val users = mutableStateListOf(User("Keira Arina", "Keira", "123abc"))
    var currentUser by mutableStateOf<User?>(null)
    val cart = mutableStateMapOf<Int, Int>()

    val cartCount get() = cart.values.sum()

    fun add(item: MenuItem) { cart[item.id] = (cart[item.id] ?: 0) + 1 }

    fun remove(item: MenuItem) {
        val qty = (cart[item.id] ?: 0) - 1
        if (qty <= 0) cart.remove(item.id) else cart[item.id] = qty
    }

    fun logout() {
        currentUser = null
        cart.clear()
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

        Screen.Confirm -> ConfirmScreen {
            state.cart.clear()
            state.screen = Screen.Home
        }

        Screen.Home, Screen.Order, Screen.Profile -> {
            val tab = when (state.screen) {
                Screen.Order -> Tab.Order
                Screen.Profile -> Tab.Profile
                else -> Tab.Home
            }
            Scaffold(
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
                        Tab.Home -> HomeScreen(name, state.cart, onAdd = state::add)

                        Tab.Order -> OrderScreen(
                            cart = state.cart,
                            defaultName = name,
                            onRemove = state::remove,
                        ) { state.screen = Screen.Confirm }

                        Tab.Profile -> ProfileScreen(
                            userName = name,
                            onSave = { fullName ->
                                state.currentUser = state.currentUser?.copy(fullName = fullName)
                            },
                            onLogout = state::logout,
                        )
                    }
                }
            }
        }
    }
}