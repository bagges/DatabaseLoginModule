package de.bagges.jaas;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by markus on 10.12.14.
 */
@Stateless
public class DatabaseLoginModule implements LoginModule {

    private static final String KEY_JAAS_LOGIN_NAME = "javax.security.auth.login.name";
    private static final String KEY_JAAS_LOGIN_PASSWORD = "javax.security.auth.login.password";

    private static final Logger LOG = Logger.getLogger(DatabaseLoginModule.class.getName());

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    private String username;
    private String password;

    private Principal myPrincipal;
    private boolean authenticated = false;

    @PersistenceContext(unitName = "em", type = PersistenceContextType.EXTENDED)
    EntityManager em;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        LOG.log(Level.FINE, "Initializing login module...");
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        LOG.log(Level.FINE, "Login called.");
        if(sharedState.containsKey(KEY_JAAS_LOGIN_NAME) && sharedState.containsKey(KEY_JAAS_LOGIN_PASSWORD)) {
            username = ""+sharedState.get(KEY_JAAS_LOGIN_NAME);
            password = ""+sharedState.get(KEY_JAAS_LOGIN_PASSWORD);
        } else {
            Callback[] callbacks = new Callback[2];
            callbacks[0] = new NameCallback("Username: ");
            callbacks[1] = new PasswordCallback("Password: ", false);
            try {
                callbackHandler.handle(callbacks);
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        username = ((NameCallback) cb).getName();
                    } else if (cb instanceof PasswordCallback) {
                        password = new String(((PasswordCallback) cb).getPassword());
                    }
                }
            } catch (IOException | UnsupportedCallbackException e) {
                LOG.log(Level.SEVERE, "Cannot process callbacks", e);
                authenticated = false;
            }
        }

        Query query = em.createNativeQuery("select password from USERS where username = ?");
        query.setParameter(1, username);
        String dbPassword = ""+query.getSingleResult();

        if(password.equalsIgnoreCase(dbPassword)) {
            authenticated = true;
        }
        return authenticated;
    }

    @Override
    public boolean commit() throws LoginException {
        LOG.log(Level.FINE, "Commit called.");
        if(!authenticated) {
            authenticated = false;
            username = null;
            password = null;
            myPrincipal = null;
            return false;
        } else {
            sharedState.put(KEY_JAAS_LOGIN_NAME, username);
            sharedState.put(KEY_JAAS_LOGIN_PASSWORD, password);
            myPrincipal = new UserPrincipal(username);
            subject.getPrincipals().add(myPrincipal);
            return true;
        }
    }

    @Override
    public boolean abort() throws LoginException {
        LOG.log(Level.FINE, "Abort called.");
        authenticated = false;
        password = null;
        username = null;
        myPrincipal = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        LOG.log(Level.FINE, "Logout called.");
        sharedState.remove(KEY_JAAS_LOGIN_NAME);
        sharedState.remove(KEY_JAAS_LOGIN_PASSWORD);
        subject.getPrincipals().remove(myPrincipal);
        authenticated = false;
        return true;
    }
}
