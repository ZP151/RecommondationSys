package com.example.recommendationsys.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.recommondationsys.data.model.User
import com.example.recommondationsys.data.model.UserPreference
import com.example.recommondationsys.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import java.io.EOFException

// 定义 API 接口
interface UserApiService {
    @POST("/api/user/register")
    suspend fun register(@Body request: RegisterRequest
    ): Response<RegisterApiResponse>

    @POST("/api/user/login")
    suspend fun login(@Body request: LoginRequest
    ): Response<LoginApiResponse>

    @PATCH("/api/user/update-isnew/{userId}")
    suspend fun updateUserIsNew(@Path("userId") userId: String
    ): Response<Unit>

    @POST("/api/user/updatePreferences/{userId}")
    suspend fun savePreferences(
        @Path("userId") userId: String,
        @Body preference: UserPreference
    ): Response<Unit>
    @POST("/api/user/logout/{userId}") // 新增 logout 接口
    suspend fun logout(@Path("userId") userId: String
    ): Response<Unit>
}

// 请求数据
data class RegisterRequest(val username: String, val password: String, val confirmPassword: String)
data class LoginRequest(val username: String, val password: String)

// API 响应
data class RegisterApiResponse(val user: User)
data class LoginApiResponse(val user: User)



// UserManager，负责与后端交互
object UserManager {
    private lateinit var sharedPreferences: SharedPreferences
    private val apiService: UserApiService

    private var loginJob: Job? = null

    private const val USER_KEY = "user_data"
    private val gson = Gson()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UserApiService::class.java)
    }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.contains(USER_KEY)
    }

    suspend fun registerUser(username: String, password: String, confirmPassword: String): Boolean {
        return try {
            val response = apiService.register(RegisterRequest(username, password, confirmPassword))
            if (response.isSuccessful) {
                response.body()?.user?.let { user ->
                    saveUser(user)
                    return true
                }
            }
            Log.e("UserManager", "注册失败: HTTP ${response.code()} ${response.message()}")
            false
        } catch (e: Exception) {
            Log.e("UserManager", "注册请求失败: ${e.message}")
            false
        }
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        loginJob?.cancel()
        return withContext(Dispatchers.IO) {
            try {
                Log.d("UserManager", "正在登录: $username")
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.user?.let { user ->
                        saveUser(user)
                        return@withContext true
                    }
                }
                Log.e("UserManager", "登录失败: HTTP ${response.code()} ${response.message()}")
                return@withContext false
            } catch (e: Exception) {
                Log.e("UserManager", "登录请求异常: ${e.message}")
                return@withContext false
            }
        }
    }

    private fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(USER_KEY, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(USER_KEY, null)
        return userJson?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                Log.e("UserManager", "JSON 解析失败", e)
                null
            }
        }
    }

    suspend fun logout(): Boolean {
        val currentUser = getUser()
        var isLogoutSuccess = false  // 默认认为登出失败

        if (currentUser != null) {
            try {
                val response = apiService.logout(currentUser.id)
                if (response.isSuccessful) {
                    Log.d("UserManager", "用户登出成功")
                    isLogoutSuccess = true  // 只有后端成功时才设置为 true
                } else {
                    Log.e("UserManager", "用户登出失败: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UserManager", "登出请求异常: ${e.message}")
            }
        }

        // 无论后端是否成功登出，都需要清理本地数据
        loginJob?.cancel()
        sharedPreferences.edit().clear().apply()
        Log.d("UserManager", "用户本地登出成功")

        return isLogoutSuccess
    }



    suspend fun updateUserIsNew(isNew: Boolean) {
        val currentUser = getUser()
        if (currentUser != null) {
            val updatedUser = User(
                id = currentUser.id,
                username = currentUser.username,
                isNewUser = isNew
            )
            saveUser(updatedUser)

            try {
                val response = apiService.updateUserIsNew(currentUser.id)
                if (response.isSuccessful) {
                    Log.d("UserManager", "isNewUser 更新成功")
                } else {
                    Log.e("UserManager", "更新 isNewUser 失败: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UserManager", "更新 isNewUser 发生异常: ${e.message}")
            }
        }
    }

    suspend fun saveUserPreference(preference: UserPreference) {
        val currentUser = getUser()
        if (currentUser != null) {
            try {
                val response = apiService.savePreferences(currentUser.id, preference)
                if (response.isSuccessful) {
                    Log.d("UserManager", "用户偏好设置已更新")
                } else {
                    Log.e("UserManager", "更新偏好设置失败: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UserManager", "更新偏好设置异常: ${e.message}")
            }
        }
    }
}

