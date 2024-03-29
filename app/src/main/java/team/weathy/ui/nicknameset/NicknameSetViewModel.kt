package team.weathy.ui.nicknameset

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import team.weathy.api.CreateUserReq
import team.weathy.api.UserAPI
import team.weathy.di.Api
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.UniqueIdentifier
import team.weathy.util.debugE
import team.weathy.util.emit
import team.weathy.util.extensions.launchCatch

class NicknameSetViewModel @ViewModelInject constructor(
        @Api private val userAPI: UserAPI, private val uniqueId: UniqueIdentifier
) : ViewModel() {
    val nickname = MutableLiveData("")
    val isSubmitEnabled = nickname.map {
        it.length in 1..6
    }

    val loadingSubmit = MutableLiveData(false)

    val focused = MutableLiveData(true)

    val onHideKeyboard = SimpleEventLiveData()
    val onSuccess = SimpleEventLiveData()

    fun onSubmit() {
        focused.value = false
        onHideKeyboard.emit()
        val newUniqueId = uniqueId.generate()

        launchCatch({
            userAPI.createUser(CreateUserReq(newUniqueId, nickname.value ?: ""))
        }, loading = loadingSubmit, onSuccess = {
            uniqueId.saveUserId(it.user.id)
            uniqueId.saveId(newUniqueId)
            uniqueId.saveToken(it.token)
            uniqueId.saveUserNickname(nickname.value ?: "")
            onSuccess.emit()
        }, onFailure = {
            debugE(it)
        })
    }
}