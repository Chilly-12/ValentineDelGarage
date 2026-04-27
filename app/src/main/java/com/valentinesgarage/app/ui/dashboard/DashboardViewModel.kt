package com.valentinesgarage.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.domain.model.DashboardStats
import com.valentinesgarage.app.domain.usecase.GetDashboardStatsUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
) : ViewModel() {

    private val employeeIdFlow = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val stats: StateFlow<DashboardStats> = employeeIdFlow
        .flatMapLatest { id ->
            getDashboardStatsUseCase.observe(id ?: 0L)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardStats.Empty)

    fun setEmployee(employeeId: Long) {
        employeeIdFlow.value = employeeId
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                DashboardViewModel(container.getDashboardStatsUseCase)
            }
        }
    }
}
