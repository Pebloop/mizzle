package com.pebloop.mizzle.android.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

data class UserSession(
    val id: String,
    val name: String,
    val email: String,
    val role: String? = null
)

data class ServerResourceItem(
    val id: String,
    val name: String,
    val type: String,
    val r2Key: String,
    val downloadUrl: String,
    val mimeType: String? = null,
    val regionX: Int = 0,
    val regionY: Int = 0,
    val regionWidth: Int = -1,
    val regionHeight: Int = -1
)


class AuthManager private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mizzle_auth_prefs", Context.MODE_PRIVATE)
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    var serverUrl: String
        get() = prefs.getString("server_url", "http://10.0.2.2:5173") ?: "http://10.0.2.2:5173"
        set(value) {
            prefs.edit().putString("server_url", value).apply()
        }

    var sessionCookie: String?
        get() = prefs.getString("session_cookie", null)
        private set(value) {
            prefs.edit().putString("session_cookie", value).apply()
        }

    var currentUser: UserSession? = null
        private set

    val isLoggedIn: Boolean
        get() = sessionCookie != null

    interface AuthCallback<T> {
        fun onSuccess(result: T)
        fun onError(errorMessage: String)
    }

    fun signUp(name: String, email: String, password: String, callback: AuthCallback<UserSession>) {
        executor.execute {
            try {
                val url = URL("$serverUrl/api/auth/sign-up/email")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("name", name)
                    put("email", email)
                    put("password", password)
                }

                OutputStreamWriter(conn.outputStream).use { it.write(jsonBody.toString()) }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) {
                    saveCookie(conn)
                    val responseStr = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                    val jsonResponse = JSONObject(responseStr)
                    val userObj = jsonResponse.getJSONObject("user")

                    val user = UserSession(
                        id = userObj.getString("id"),
                        name = userObj.getString("name"),
                        email = userObj.getString("email"),
                        role = if (userObj.has("role") && !userObj.isNull("role")) userObj.getString("role") else null
                    )
                    currentUser = user
                    mainHandler.post { callback.onSuccess(user) }
                } else {
                    val errorStr = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: "Registration failed ($responseCode)"
                    val errorMsg = try {
                        JSONObject(errorStr).optString("message", "Registration failed")
                    } catch (e: Exception) {
                        "Registration failed: HTTP $responseCode"
                    }
                    mainHandler.post { callback.onError(errorMsg) }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError(e.message ?: "Network error") }
            }
        }
    }

    fun signIn(email: String, password: String, callback: AuthCallback<UserSession>) {
        executor.execute {
            try {
                val url = URL("$serverUrl/api/auth/sign-in/email")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }

                OutputStreamWriter(conn.outputStream).use { it.write(jsonBody.toString()) }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    saveCookie(conn)
                    val responseStr = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                    val jsonResponse = JSONObject(responseStr)
                    val userObj = jsonResponse.getJSONObject("user")

                    val user = UserSession(
                        id = userObj.getString("id"),
                        name = userObj.getString("name"),
                        email = userObj.getString("email"),
                        role = if (userObj.has("role") && !userObj.isNull("role")) userObj.getString("role") else null
                    )
                    currentUser = user
                    mainHandler.post { callback.onSuccess(user) }
                } else {
                    val errorStr = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: "Login failed ($responseCode)"
                    val errorMsg = try {
                        JSONObject(errorStr).optString("message", "Invalid email or password")
                    } catch (e: Exception) {
                        "Login failed: HTTP $responseCode"
                    }
                    mainHandler.post { callback.onError(errorMsg) }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError(e.message ?: "Network error") }
            }
        }
    }

    fun signInOffline(callback: AuthCallback<UserSession>) {
        val dummyUser = UserSession(
            id = "offline_dev_user",
            name = "Offline Developer",
            email = "dev@local.mizzle",
            role = "user"
        )
        sessionCookie = "mizzle_offline_session=true"
        currentUser = dummyUser
        mainHandler.post { callback.onSuccess(dummyUser) }
    }

    fun uploadDroplet(id: String, name: String, jsonPayload: String, callback: AuthCallback<String>) {
        executor.execute {
            try {
                val url = URL("$serverUrl/api/droplets/upload")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                sessionCookie?.let { conn.setRequestProperty("Cookie", it) }
                conn.doOutput = true

                val body = JSONObject().apply {
                    put("id", id)
                    put("name", name)
                    try {
                        put("data", JSONObject(jsonPayload))
                    } catch (e: Exception) {
                        put("data", jsonPayload)
                    }
                }

                OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) {
                    val responseStr = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                    val jsonResponse = JSONObject(responseStr)
                    val msg = jsonResponse.optString("message", "Droplet uploaded successfully")
                    mainHandler.post { callback.onSuccess(msg) }
                } else {
                    val errorStr = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: "Upload failed ($responseCode)"
                    val errorMsg = try {
                        JSONObject(errorStr).optString("error", "Upload failed")
                    } catch (e: Exception) {
                        "Upload failed: HTTP $responseCode"
                    }
                    mainHandler.post { callback.onError(errorMsg) }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError(e.message ?: "Network error during upload") }
            }
        }
    }

    fun signOut(callback: AuthCallback<Unit>) {
        executor.execute {
            try {
                val url = URL("$serverUrl/api/auth/sign-out")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                sessionCookie?.let { conn.setRequestProperty("Cookie", it) }

                val responseCode = conn.responseCode
                sessionCookie = null
                currentUser = null

                mainHandler.post { callback.onSuccess(Unit) }
            } catch (e: Exception) {
                sessionCookie = null
                currentUser = null
                mainHandler.post { callback.onSuccess(Unit) }
            }
        }
    }

    fun fetchServerResources(typeFilter: String? = null, callback: AuthCallback<List<ServerResourceItem>>) {
        executor.execute {
            try {
                val endpoint = if (typeFilter != null) "$serverUrl/api/resources?type=$typeFilter" else "$serverUrl/api/resources"
                val url = URL(endpoint)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                sessionCookie?.let { conn.setRequestProperty("Cookie", it) }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseStr = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                    val jsonResponse = JSONObject(responseStr)
                    val array = jsonResponse.optJSONArray("resources") ?: org.json.JSONArray()
                    val items = mutableListOf<ServerResourceItem>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val rawUrl = obj.optString("downloadUrl", "")
                        val fullDownloadUrl = if (rawUrl.startsWith("http")) rawUrl else "$serverUrl$rawUrl"
                        items.add(
                            ServerResourceItem(
                                id = obj.optString("id", ""),
                                name = obj.optString("name", "Unnamed"),
                                type = obj.optString("type", "texture"),
                                r2Key = obj.optString("r2Key", ""),
                                downloadUrl = fullDownloadUrl,
                                mimeType = obj.optString("mimeType", null),
                                regionX = obj.optInt("regionX", 0),
                                regionY = obj.optInt("regionY", 0),
                                regionWidth = obj.optInt("regionWidth", -1),
                                regionHeight = obj.optInt("regionHeight", -1)
                            )
                        )
                    }
                    mainHandler.post { callback.onSuccess(items) }
                } else {
                    mainHandler.post { callback.onError("Failed to fetch resources ($responseCode)") }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError(e.message ?: "Network error fetching resources") }
            }
        }
    }

    fun downloadServerResourceFile(downloadUrl: String, destFile: java.io.File, callback: AuthCallback<java.io.File>) {
        executor.execute {
            try {
                val fullUrl = if (downloadUrl.startsWith("http")) downloadUrl else "$serverUrl$downloadUrl"
                val url = URL(fullUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                sessionCookie?.let { conn.setRequestProperty("Cookie", it) }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    destFile.parentFile?.mkdirs()
                    conn.inputStream.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    mainHandler.post { callback.onSuccess(destFile) }
                } else {
                    mainHandler.post { callback.onError("Download failed with HTTP $responseCode") }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError(e.message ?: "Download error") }
            }
        }
    }


    private fun saveCookie(conn: HttpURLConnection) {
        val headerFields = conn.headerFields
        val cookies = headerFields["Set-Cookie"]
        if (cookies != null) {
            val sb = StringBuilder()
            for (cookie in cookies) {
                if (sb.isNotEmpty()) sb.append("; ")
                sb.append(cookie.split(";")[0])
            }
            sessionCookie = sb.toString()
        }
    }

    companion object {
        @Volatile
        private var instance: AuthManager? = null

        @JvmStatic
        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
