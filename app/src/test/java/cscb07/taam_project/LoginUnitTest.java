package cscb07.taam_project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

import com.example.b07demosummer2024.LoginModel;
import com.example.b07demosummer2024.LoginView;
import com.example.b07demosummer2024.LoginPresenter;
import com.example.b07demosummer2024.User;
import com.google.android.gms.tasks.Tasks;

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
        loginModelMock = mock(LoginModel.class);
        loginViewMock = mock(LoginView.class);
        loginPresenter = new LoginPresenter(loginModelMock, loginViewMock);
    }

    @Test
    public void testLoginSuccess() {
        User testUser = new User("thomas", "123");

        Task<User> mockedTask = mock(Task.class);
        when(loginModelMock.login("thomas", "123")).thenReturn(mockedTask);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                OnCompleteListener<User> listener = invocation.getArgument(0);
                listener.onComplete(mockedTask);
                return null;
            }
        }).when(mockedTask).addOnCompleteListener(any());

        when(mockedTask.getResult()).thenReturn(testUser);

        loginPresenter.onButtonClick("thomas", "123");

        verify(loginViewMock).onSuccess();
    }

    @Test
    public void testLoginFailure() {
        Task<User> mockedTask = mock(Task.class);
        when(loginModelMock.login("thomas", "123")).thenReturn(mockedTask);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                OnCompleteListener<User> listener = invocation.getArgument(0, OnCompleteListener.class);
                listener.onComplete(mockedTask);
                return null;
            }
        }).when(mockedTask).addOnCompleteListener(any());

        when(mockedTask.getResult()).thenReturn(null);

        loginPresenter.onButtonClick("thomas", "123");

        verify(loginViewMock).onFailure();
    }
}