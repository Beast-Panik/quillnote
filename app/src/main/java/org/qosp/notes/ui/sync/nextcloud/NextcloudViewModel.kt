package org.qosp.notes.ui.sync.nextcloud

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.qosp.notes.data.sync.core.*
import org.qosp.notes.data.sync.nextcloud.NextcloudConfig
import org.qosp.notes.preferences.*
import javax.inject.Inject

@HiltViewModel
class NextcloudViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    val username = preferenceRepository.getEncryptedString(PreferenceRepository.NEXTCLOUD_USERNAME)
    val password = preferenceRepository.getEncryptedString(PreferenceRepository.NEXTCLOUD_PASSWORD)

    fun setURL(url: String) = viewModelScope.launch {
        if (!URLUtil.isHttpsUrl(url)) return@launch

        val url = if (url.endsWith("/")) url else "$url/"
        preferenceRepository.putEncryptedStrings(
            PreferenceRepository.NEXTCLOUD_INSTANCE_URL to url,
        )
    }

    suspend fun authenticate(username: String, password: String): Response<*> {
        val config = NextcloudConfig(
            username = username,
            password = password,
            remoteAddress = preferenceRepository.getEncryptedString(PreferenceRepository.NEXTCLOUD_INSTANCE_URL).first()
        )

        val response = withContext(Dispatchers.IO) {
            val loginResult = syncManager.authenticate(config)
            if (loginResult is Success) syncManager.isServerCompatible(config) else loginResult
        }

        return response.also {
            if (it is Success) {
                preferenceRepository.putEncryptedStrings(
                    PreferenceRepository.NEXTCLOUD_USERNAME to username,
                    PreferenceRepository.NEXTCLOUD_PASSWORD to password,
                )
            }
        }
    }
}
