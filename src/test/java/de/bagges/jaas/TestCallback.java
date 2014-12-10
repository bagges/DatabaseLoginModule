package de.bagges.jaas;

import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * Created by markus on 10.12.14.
 */
public class TestCallback implements CallbackHandler{

    private String username;
    private String password;

    public TestCallback(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for(Callback cb : callbacks) {
            if(cb instanceof NameCallback) {
                ((NameCallback)cb).setName(username);
            } else if(cb instanceof PasswordCallback) {
                ((PasswordCallback)cb).setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(cb, "Not supported");
            }
        }
    }
}
