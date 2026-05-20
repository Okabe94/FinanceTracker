package com.software.financetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.RealEstateAgent
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

private val iconMap: Map<String, ImageVector> = mapOf(
    "shopping_cart" to Icons.Rounded.ShoppingCart,
    "restaurant" to Icons.Rounded.Restaurant,
    "directions_car" to Icons.Rounded.DirectionsCar,
    "home" to Icons.Rounded.Home,
    "local_hospital" to Icons.Rounded.LocalHospital,
    "school" to Icons.Rounded.School,
    "sports_esports" to Icons.Rounded.SportsEsports,
    "flight" to Icons.Rounded.Flight,
    "local_cafe" to Icons.Rounded.LocalCafe,
    "fitness_center" to Icons.Rounded.FitnessCenter,
    "pets" to Icons.Rounded.Pets,
    "phone" to Icons.Rounded.Phone,
    "checkroom" to Icons.Rounded.Checkroom,
    "celebrate" to Icons.Rounded.Celebration,
    "music_note" to Icons.Rounded.MusicNote,
    "wifi" to Icons.Rounded.Wifi,
    "savings" to Icons.Rounded.Savings,
    "work" to Icons.Rounded.Work,
    "child_care" to Icons.Rounded.ChildCare,
    "more_horiz" to Icons.Rounded.MoreHoriz,
    "trending_up" to Icons.Rounded.TrendingUp,
    "account_balance" to Icons.Rounded.AccountBalance,
    "currency_bitcoin" to Icons.Rounded.CurrencyBitcoin,
    "real_estate_agent" to Icons.Rounded.RealEstateAgent
)

fun iconForKey(key: String): ImageVector = iconMap[key] ?: Icons.Rounded.MoreHoriz
