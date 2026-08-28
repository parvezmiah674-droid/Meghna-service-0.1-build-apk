package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChatAndPaymentScreen
import com.example.ui.HomeScreen
import com.example.ui.ServiceViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MeghnaServiceApp()
                }
            }
        }
    }
}

@Composable
fun MeghnaServiceApp(
    viewModel: ServiceViewModel = viewModel()
) {
    val technicians by viewModel.filteredTechnicians.collectAsStateWithLifecycle()
    val payments by viewModel.allPayments.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTechnician by viewModel.selectedTechnician.collectAsStateWithLifecycle()
    val currentMessages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val paymentSuccessMsg by viewModel.paymentSuccessMessage.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = selectedTechnician,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { targetTech ->
        if (targetTech == null) {
            HomeScreen(
                technicians = technicians,
                payments = payments,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                onCategorySelect = viewModel::onCategorySelect,
                onSearchChange = viewModel::onSearchQueryChange,
                onTechnicianClick = viewModel::selectTechnician,
                onAddTechnician = viewModel::addTechnician,
                onDeleteTechnician = viewModel::deleteTechnician
            )
        } else {
            BackHandler {
                viewModel.selectTechnician(null)
            }
            ChatAndPaymentScreen(
                technician = targetTech,
                messages = currentMessages,
                onBack = { viewModel.selectTechnician(null) },
                onSendMessage = viewModel::sendMessage,
                onMakePayment = viewModel::makePayment,
                paymentSuccessMsg = paymentSuccessMsg,
                onClearPaymentMsg = viewModel::clearPaymentSuccessMessage
            )
        }
    }
}
