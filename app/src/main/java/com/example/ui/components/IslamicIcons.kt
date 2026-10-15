package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

object IslamicIcons {
    fun getCategoryIcon(key: String): ImageVector {
        return when (key) {
            "wb_sunny" -> Icons.Default.WbSunny
            "bedtime" -> Icons.Default.Bedtime
            "home" -> Icons.Default.Home
            "restaurant" -> Icons.Default.Restaurant
            "water_drop" -> Icons.Default.WaterDrop
            "mosque" -> Icons.Default.Mosque
            "favorite" -> Icons.Default.Favorite
            "shield" -> Icons.Default.Shield
            "spa" -> Icons.Default.Spa
            "healing" -> Icons.Default.Healing
            "directions_car" -> Icons.Default.DirectionsCar
            "thunderstorm" -> Icons.Default.Thunderstorm
            "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
            "family_restroom" -> Icons.Default.FamilyRestroom
            "menu_book" -> Icons.Default.MenuBook
            "nights_stay" -> Icons.Default.NightsStay
            "auto_awesome" -> Icons.Default.AutoAwesome
            "book" -> Icons.Default.Book
            "psychology" -> Icons.Default.Psychology
            "checklist" -> Icons.Default.Checklist
            else -> Icons.Default.Security
        }
    }
}
