package team.weathy.ui.nicknamechange

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import team.weathy.api.EditUserReq
import team.weathy.api.UserAPI
import team.weathy.di.Api
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.UniqueIdentifier
import team.weathy.util.debugE
import team.weathy.util.emit
import team.weathy.util.extensions.launchCatch

class NicknameChangeViewModel @ViewModelInject constructor(
        @Api private val userAPI: UserAPI, private val uniqueId: UniqueIdentifier
) : ViewModel() {
    val nickname = MutableLiveData("")
    val isSubmitEnabled = nickname.map {
        it.length in 1..6
    }
    private val _loadingSubmit = MutableLiveData(false)
    val loadingSubmit: LiveData<Boolean> = _loadingSubmit

    val focused = MutableLiveData(true)

    val onHideKeyboard = SimpleEventLiveData()
    val onSuccess = SimpleEventLiveData()

    fun onSubmit() {
        focused.value = false
        onHideKeyboard.emit()

        launchCatch({
            userAPI.editUser(EditUserReq(nickname.value ?: ""))
        }, loading = _loadingSubmit, onSuccess = {
            uniqueId.saveUserNickname(it.user.nickname)
            onSuccess.emit()
        }, onFailure = {
            debugE(it)
        })
    }

}