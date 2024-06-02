package com.example.biznus.data;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;

import com.example.biznus.MainActivity;
import com.example.biznus.data.model.LoggedInUser;
import com.example.biznus.ui.login.LoginActivity;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            getUsername(username));
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public String getUsername(String username) {
        String tempUser = "";
        String user = username;
        while (user.contains("@")) {
            if (user.startsWith("@")) {
                break;
            }
            tempUser += user.charAt(0);
            user = user.substring(1);
        }
        return tempUser;
    }

    public void logout() {
        // TODO: revoke authentication

    }
}