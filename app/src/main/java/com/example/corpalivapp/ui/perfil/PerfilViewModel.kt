package com.example.corpalivapp.ui.perfil

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpalivapp.data.local.ImageStore
import com.example.corpalivapp.data.local.UserPreferences
import com.example.corpalivapp.data.model.Usuario
import com.example.corpalivapp.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel del perfil. Gestiona los datos del usuario, la imagen de portada
 * y el avatar, persistiendo todo en DataStore y almacenamiento interno.
 */
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = UserPreferences(application)
    private val repository = AuthRepository(preferences)

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    val portadaPath: StateFlow<String?> = repository.portadaPath()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val avatarPath: StateFlow<String?> = repository.avatarPath()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sesionEmail().collectLatest { email ->
                _usuario.value = if (email.isNullOrBlank()) {
                    null
                } else {
                    repository.obtenerUsuario(email)
                }
            }
        }
    }

    fun actualizarPortadaDesdeGaleria(uri: Uri?) {
        actualizarImagen(uri, esPortada = true)
    }

    fun actualizarAvatarDesdeGaleria(uri: Uri?) {
        actualizarImagen(uri, esPortada = false)
    }

    fun actualizarPortadaDesdeCamara(uri: Uri?, capturaCorrecta: Boolean) {
        if (!capturaCorrecta || uri == null) {
            _mensaje.value = "No se tomó la fotografía."
            return
        }
        actualizarImagen(uri, esPortada = true)
    }

    fun actualizarAvatarDesdeCamara(uri: Uri?, capturaCorrecta: Boolean) {
        if (!capturaCorrecta || uri == null) {
            _mensaje.value = "No se tomó la fotografía."
            return
        }
        actualizarImagen(uri, esPortada = false)
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repository.cerrarSesion()
        }
    }

    fun consumirMensaje() {
        _mensaje.value = null
    }

    private fun actualizarImagen(uri: Uri?, esPortada: Boolean) {
        if (uri == null) return

        viewModelScope.launch {
            val anterior = if (esPortada) portadaPath.value else avatarPath.value
            val ruta = withContext(Dispatchers.IO) {
                ImageStore.copiarImagen(
                    context = getApplication<Application>(),
                    uri = uri,
                    prefijo = if (esPortada) "portada" else "avatar"
                )
            }

            if (ruta == null) {
                _mensaje.value = "No se pudo cargar la imagen seleccionada."
                return@launch
            }

            if (esPortada) {
                repository.guardarPortada(ruta)
            } else {
                repository.guardarAvatar(ruta)
            }
            ImageStore.eliminar(anterior)
            _mensaje.value = if (esPortada) {
                "Imagen de portada actualizada."
            } else {
                "Foto de perfil actualizada."
            }
        }
    }
}
