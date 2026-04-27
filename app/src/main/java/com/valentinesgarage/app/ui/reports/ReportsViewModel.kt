package com.valentinesgarage.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.valentinesgarage.app.domain.usecase.GetReportsUseCase
import com.valentinesgarage.app.ui.session.requireContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ReportsViewModel(
    getReportsUseCase: GetReportsUseCase,
) : ViewModel() {

    val report: StateFlow<GetReportsUseCase.Report?> = getReportsUseCase.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this as CreationExtras).requireContainer()
                ReportsViewModel(container.getReportsUseCase)
            }
        }
    }
}
