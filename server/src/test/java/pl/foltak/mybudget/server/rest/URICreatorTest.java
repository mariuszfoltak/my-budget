package pl.foltak.mybudget.server.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mfoltak
 */
public class URICreatorTest {

    public URICreatorTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void createSimpleUri() {
        URICreator uriCreator = new URICreator("account");
        assertThat(uriCreator.create("wallet"), is(URI.create("account/wallet")));
    }

    @Test
    public void createUriFromManyStrings() {
        URICreator uriCreator = new URICreator("account");
        assertThat(uriCreator.create("wallet", "bank", "etc"), is(URI.create("account/wallet/bank/etc")));
    }

    @Test
    public void createUriForStringsWithSpaces() throws UnsupportedEncodingException {
        String first = "space test";
        String second = "test space";
        URICreator uriCreator = new URICreator(first);
        String firstEncoded = URLEncoder.encode(first, "utf-8");
        String secondEncoded = URLEncoder.encode(second, "utf-8");
        assertThat(uriCreator.create(second), is(URI.create(firstEncoded + "/" + secondEncoded)));
    }

}
