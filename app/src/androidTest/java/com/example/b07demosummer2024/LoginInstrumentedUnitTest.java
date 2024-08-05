package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class LoginInstrumentedUnitTest {

    @Mock
    LoginModel loginModelMock;

    @Mock
    LoginView loginViewMock;

    LoginPresenter loginPresenter;

    @Before
    public void setUp() {
        loginModelMock = mock(LoginModel.class);
        loginViewMock = mock(LoginView.class);
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