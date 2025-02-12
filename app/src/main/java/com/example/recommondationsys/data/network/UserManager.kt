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
    // 已有的接口
    @POST("/api/user/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterApiResponse>

    @POST("/api/user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginApiResponse>

    @PATCH("/api/user/update-isnew/{userId}")
    suspend fun updateUserIsNew(@Path("userId") userId: String): Response<Unit>

    @POST("/api/user/updatePreferences/{userId}")
    suspend fun savePreferences(
        @Path("userId") userId: String,
        @Body preference: UserPreference
    ): Response<Unit>

    @POST("/api/user/logout/{userId}")
    suspend fun logout(@Path("userId") userId: String): Response<Unit>

    // 新增修改密码接口
    @POST("/api/user/changePassword/{userId}")
    suspend fun changePassword(
        @Path("userId") userId: String,
        @Body request: ChangePasswordRequest
    ): Response<Unit>
}

// 请求数据
data class RegisterRequest(val username: String, val password: String, val confirmPassword: String)
data class LoginRequest(val username: String, val password: String)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)

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

    // 新增方法：修改密码
    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        val currentUser = getUser()
        if (currentUser == null) {
            Log.e("UserManager", "当前用户未登录")
            return false
        }

        return try {
            val response = apiService.changePassword(
                userId = currentUser.id,
                request = ChangePasswordRequest(currentPassword, newPassword)
            )
            if (response.isSuccessful) {
                Log.d("UserManager", "密码修改成功")
                true
            } else {
                Log.e("UserManager", "密码修改失败: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserManager", "修改密码请求异常: ${e.message}")
            false
        }
    }
    suspend fun updateUserProfile(username: String, age: Int, email: String): Boolean {
        val currentUser = getUser()
        if (currentUser == null) {
            Log.e("UserManager", "当前用户未登录")
            return false
        }

        return try {
            val updatedUser = currentUser.copy(username = username, age = age, email = email)
            // 假设后端 API 有一个 updateUser 方法
            val response = apiService.updateUser(updatedUser.id, updatedUser)
            if (response.isSuccessful) {
                saveUser(updatedUser) // 本地保存更新后的用户信息
                true
            } else {
                Log.e("UserManager", "更新用户信息失败: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("UserManager", "更新用户信息请求异常: ${e.message}")
            false
        }
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
}
