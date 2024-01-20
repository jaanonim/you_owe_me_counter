package com.jaanonim.you_owe_me_counter

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object NotificationSerializer : Serializer<NotificationRecord> {
    override val defaultValue: NotificationRecord = NotificationRecord.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NotificationRecord {
        try {
            return NotificationRecord.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: NotificationRecord,
        output: OutputStream
    ) = t.writeTo(output)
}

const val DATA_STORE_FILE_NAME = "notification_record.pb"


val Context.notificationRecord: DataStore<NotificationRecord> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = NotificationSerializer
)