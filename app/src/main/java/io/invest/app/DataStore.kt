package io.invest.app

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStore @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore

    suspend fun setApiToken(token: String) {
        dataStore.updateData { localStorage ->
            localStorage.toBuilder()
                .setApiToken(token)
                .build()
        }
    }

    fun getApiToken() = dataStore.data.map { localStorage ->
        localStorage.apiToken
    }

    companion object {
        private val Context.dataStore: DataStore<LocalStorage> by dataStore(
            fileName = "localstore.pb",
            serializer = DataStoreSerializer
        )

        private object DataStoreSerializer : Serializer<LocalStorage> {
            override val defaultValue: LocalStorage = LocalStorage.getDefaultInstance()

            override suspend fun readFrom(input: InputStream): LocalStorage {
                try {
                    return LocalStorage.parseFrom(input)
                } catch (exception: InvalidProtocolBufferException) {
                    throw CorruptionException("Cannot read protobuf.", exception)
                }
            }

            override suspend fun writeTo(t: LocalStorage, output: OutputStream) {
                t.writeTo(output)
            }
        }
    }
}
