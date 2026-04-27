package com.valentinesgarage.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.valentinesgarage.app.domain.model.EmployeeRole
import com.valentinesgarage.app.ui.navigation.Routes

data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

object BottomTabs {
    val Dashboard = BottomTab(Routes.DASHBOARD, "Dashboard", Icons.Default.Dashboard)
    val Trucks = BottomTab(Routes.TRUCKS, "Trucks", Icons.Default.LocalShipping)
    val Reports = BottomTab(Routes.REPORTS, "Reports", Icons.Default.Assessment)
    val Profile = BottomTab(Routes.PROFILE, "Me", Icons.Default.Person)

    fun forRole(role: EmployeeRole): List<BottomTab> = when (role) {
        EmployeeRole.OWNER -> listOf(Dashboard, Trucks, Reports, Profile)
        EmployeeRole.MECHANIC -> listOf(Dashboard, Trucks, Profile)
    }
}

@Composable
fun GarageBottomBar(
    role: EmployeeRole,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
    ) {
        BottomTabs.forRole(role).forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { if (currentRoute != tab.route) onNavigate(tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
