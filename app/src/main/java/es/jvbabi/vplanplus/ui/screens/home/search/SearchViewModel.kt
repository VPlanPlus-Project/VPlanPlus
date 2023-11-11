package es.jvbabi.vplanplus.ui.screens.home.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(SearchState())
    val state: State<SearchState> = _state

    fun type(value: String) {
        _state.value = _state.value.copy(searchValue = value)
    }

    fun toggleFilter(filterType: FilterType) {
        _state.value = _state.value.copy(
            filter = _state.value.filter.toMutableMap().apply {
                this[filterType] = !this[filterType]!!
            }
        )
    }
}

data class SearchState(
    val searchValue: String = "",
    val filter: Map<FilterType, Boolean> = mapOf(
        FilterType.TEACHER to true,
        FilterType.ROOM to true,
        FilterType.CLASS to true,
        FilterType.PROFILE to true
    )
)

enum class FilterType {
    TEACHER,
    ROOM,
    CLASS,
    PROFILE
}