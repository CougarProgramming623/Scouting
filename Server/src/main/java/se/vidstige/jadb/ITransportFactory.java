package se.vidstige.jadb;

import java.io.IOException;

/**
 * Created by T on 2016. 03. 01..
 */
public interface ITransportFactory {
    public Transport createTransport() throws IOException;
}
