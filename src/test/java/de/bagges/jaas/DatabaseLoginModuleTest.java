package de.bagges.jaas;

import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Created by markus on 10.12.14.
 */
public class DatabaseLoginModuleTest {

    @Before
    public void setup() {
        System.setProperty("java.security.auth.login.config", "src/test/resources/jaas.config");
        EJBContainer container = EJBContainer.createEJBContainer();
        Context ctx = container.getContext();
    }

    @Test
    public void testPositiveLogin() throws LoginException {
        LoginContext ctx = new LoginContext("Test", new TestCallback("Markus", "Markus"));
        ctx.login();
    }

    @Test(expected = LoginException.class)
    public void testNegativeLogin() throws LoginException {
        LoginContext ctx = new LoginContext("Test", new TestCallback("Markus", "Backes"));
        ctx.login();
    }

    @Test
    public void testSubject() throws LoginException{
        LoginContext ctx = new LoginContext("Test", new TestCallback("Markus", "Markus"));
        ctx.login();
        ctx.logout();
    }
}
