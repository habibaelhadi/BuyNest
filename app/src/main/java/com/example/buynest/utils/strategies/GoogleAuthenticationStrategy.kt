package com.example.buynest.utils.strategies

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.repos.authenticationrepo.AuthenticationRepo

class GoogleAuthenticationStrategy(
     val context: Context,
     val launcher: ActivityResultLauncher<Intent>
): AuthenticationStrategy {

    override suspend fun authenticate(repo: AuthenticationRepo): Result<Unit> {
        val result = repo.logInWithGoogle(context, launcher)
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }
        return Result.success(Unit)
    }

}
