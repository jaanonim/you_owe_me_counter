package com.jaanonim.you_owe_me_counter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp
import java.text.SimpleDateFormat

private const val FILE_PROVIDER_AUTHORITY = "com.jaanonim.you_owe_me_counter.fileprovider"
private const val FILE_PROVIDER_NAME = "export.csv"

@RequiresApi(Build.VERSION_CODES.O)
fun exportAndShare(data: List<Notification>, context: Context) {
    try {
        val filename = FILE_PROVIDER_NAME
        val myFile = File(context.cacheDir, filename)

        // write the file here, e.g.
        FileOutputStream(myFile).use { stream ->
            stream.write(generateCsvString(data).toByteArray())
        }


        // here, com.example.myapp.fileprovider should match the file provider in your manifest
        val contentUri =
            getUriForFile(context, FILE_PROVIDER_AUTHORITY, myFile)

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(
            contentUri,
            "text/csv"
        )
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(context, intent, null)
    } catch (e: Exception) {
        Log.d("FS ERROR", "Writing csv failed: $e")
        Toast.makeText(context, "Writing csv failed: $e", Toast.LENGTH_LONG).show()
        throw e
    }
}

fun generateCsvString(data: List<Notification>): String {
    val sb = StringBuilder()
    sb.append("Timestamp,Title,Text,Value\n")
    data.forEach {
        val timestamp = escapeCsvString(
            SimpleDateFormat.getDateTimeInstance()
                .format(Timestamp(it.timestamp))
        )
        val title = escapeCsvString(it.title)
        val text = escapeCsvString(it.text)
        val value = escapeCsvString(it.value.toString())

        sb.append(
            "${timestamp},${title},${text},${value}\n"
        )
    }
    return sb.toString()
}

fun escapeCsvString(s: String): String {
    return if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
        "\"" + s.replace("\"", "\"\"") + "\""
    } else {
        s
    }
}