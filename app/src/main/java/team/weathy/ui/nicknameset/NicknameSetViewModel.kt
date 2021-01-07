package team.weathy.ui.nicknameset

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.delay
import team.weathy.api.CreateUserReq
import team.weathy.api.UserAPI
import team.weathy.di.ApiMock
import team.weathy.util.SimpleEventLiveData
import team.weathy.util.UniqueIdentifier
import team.weathy.util.emit
import team.weathy.util.extensions.launchCatch

class NicknameSetViewModel @ViewModelInject constructor(
    @ApiMock private val userAPI: UserAPI, private val uniqueId: UniqueIdentifier
) : ViewModel() {
    val nickname = MutableLiveData("")
    val isSubmitEnabled = nickname.map {
        it.length in 1..6
    }

    private val _loadingSubmit = MutableLiveData(false)
    val loadingSubmit: LiveData<Boolean> = _loadingSubmit

    val onHideKeyboard = SimpleEventLiveData()
    val onSuccess = SimpleEventLiveData()

    fun onSubmit() {
        onHideKeyboard.emit()
        val newUniqueId = uniqueId.generate()

        launchCatch({
            delay(2000)
            userAPI.createUser(CreateUserReq(uniqueId.generate(), nickname.value ?: ""))
        }, loading = _loadingSubmit, onSuccess = {
            uniqueId.save(newUniqueId)
            onSuccess.emit()
        }, onFailure = {

        })
    }
}