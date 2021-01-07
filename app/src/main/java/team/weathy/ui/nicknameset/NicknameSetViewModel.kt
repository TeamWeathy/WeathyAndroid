package team.weathy.ui.nicknameset

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import team.weathy.api.CreateUserReq
import team.weathy.api.UserAPI
import team.weathy.di.ApiMock
import team.weathy.util.debugE

class NicknameSetViewModel @ViewModelInject constructor(@ApiMock private val userAPI: UserAPI) : ViewModel() {
    init {
        viewModelScope.launch {
            kotlin.runCatching {
                userAPI.createUser(CreateUserReq("", ""))
            }.onSuccess {
                debugE(it)
            }.onFailure {
                debugE(it)
            }
        }
    }
}