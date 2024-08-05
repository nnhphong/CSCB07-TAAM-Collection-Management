package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

import com.example.b07demosummer2024.LoginModel;
import com.example.b07demosummer2024.LoginView;
import com.example.b07demosummer2024.LoginPresenter;
import com.example.b07demosummer2024.User;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginUnitTest {

    @Mock
    private LoginModel loginModelMock;

    @Mock
    private LoginView loginViewMock;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() {
//        loginModelMock = mock(LoginModel.class);
//        loginViewMock = mock(LoginView.class);
        loginPresenter = new LoginPresenter(loginModelMock, loginViewMock);
    }

    @Test
    public void testLoginSuccess() {
        List<User> user_list = new ArrayList<>();
        TaskCompletionSource<List<User>> tcs = new TaskCompletionSource<>();
        user_list.add(new User("thomas", "123"));
        tcs.setResult(user_list);
        when(loginModelMock.login("thomas", "123")).thenReturn(tcs.getTask());

        loginPresenter.onButtonClick("thomas", "123");

        verify(loginViewMock).onSuccess();
    }

    @Test
    public void testLoginFailure() {
        List<User> user_list = new ArrayList<>();
        TaskCompletionSource<List<User>> tcs = new TaskCompletionSource<>();
        tcs.setResult(user_list);
        when(loginModelMock.login("thomas", "123")).thenReturn(tcs.getTask());

        loginPresenter.onButtonClick("thomas", "123");

        verify(loginViewMock).onFailure();
    }
}