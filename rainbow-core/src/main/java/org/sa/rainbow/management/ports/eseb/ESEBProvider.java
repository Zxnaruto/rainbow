package org.sa.rainbow.management.ports.eseb;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.sa.rainbow.core.Rainbow;

import edu.cmu.cs.able.eseb.bus.EventBus;
import edu.cmu.cs.able.eseb.conn.BusConnection;
import edu.cmu.cs.able.eseb.conn.BusConnectionState;
import edu.cmu.cs.able.parsec.LocalizedParseException;
import edu.cmu.cs.able.parsec.ParsecFileReader;
import edu.cmu.cs.able.typelib.jconv.DefaultTypelibJavaConverter;
import edu.cmu.cs.able.typelib.parser.DefaultTypelibParser;
import edu.cmu.cs.able.typelib.parser.TypelibParsingContext;
import edu.cmu.cs.able.typelib.prim.PrimitiveScope;

public class ESEBProvider {

    /** The set of BusServers on the local machine, keyed by the port **/
    protected static Map<Short, EventBus>       s_servers            = new HashMap<> ();
    /** The set of BusClients already created, keyed by host:port **/
    protected static Map<String, BusConnection> s_clients            = new HashMap<> ();
    /**
     * Return the cached BusServer for this port, creating a new one if it doesn't yet exist
     * 
     * @param port
     *            The port that the server is connected to
     * @return the cached or newly created BusServer
     * @throws IOException
     */
    static EventBus getBusServer (short port) throws IOException {
        EventBus s = s_servers.get (port);
        if (s == null || s.closed ()) {
            ESEBConnector.LOGGER.debug (MessageFormat.format ("Constructing a new BusServer on port {0}", port));
            try {
                s = new EventBus (port, ESEBProvider.SCOPE);
                s_servers.put (port, s);
                s.start ();
            }
            catch (Exception e) {
                ESEBConnector.LOGGER.warn (MessageFormat.format ("BusServer could not be created on port {0}", port));
            }
        }
        return s;
    }
    /**
     * Return the cached BusClient for this host:port combination, or create a new one if it doesn't yet exist
     * 
     * @param remoteHost
     *            The host for the client
     * @param remotePort
     *            The port for the client
     * @return The cached or newly created BusClient
     */
    static BusConnection getBusClient (String remoteHost, short remotePort) {
        // Make sure that we translate host names to IPs
        remoteHost = Rainbow.canonicalizeHost2IP (remoteHost);
        String key = ESEBProvider.clientKey (remoteHost, remotePort);
        BusConnection c = s_clients.get (key);
        if (c == null || c.state () == BusConnectionState.DISCONNECTED) {
            ESEBConnector.LOGGER.debug (MessageFormat.format ("Constructing a new BusClient on {0}", key));
            c = new BusConnection (remoteHost, remotePort, ESEBProvider.SCOPE);
            s_clients.put (key, c);
            c.start ();
        }
        return c;
    }

    private static String clientKey (String remoteHost, short remotePort) {
        StringBuilder sb = new StringBuilder ();
        sb.append (remoteHost);
        sb.append (":");
        sb.append (remotePort);
        String key = sb.toString ();
        return key;
    }

    static final PrimitiveScope                        SCOPE             = new PrimitiveScope ();
    protected static final DefaultTypelibJavaConverter CONVERTER         = DefaultTypelibJavaConverter.make (SCOPE);

    static {
        DefaultTypelibParser parser = DefaultTypelibParser.make ();
        TypelibParsingContext context = new TypelibParsingContext (SCOPE, SCOPE);
        try {
            parser.parse (new ParsecFileReader ()
            .read_memory ("struct typed_attribute_with_value {string name; string type; any? value;}"), context);
            parser.parse (
                    new ParsecFileReader ()
                    .read_memory ("struct command_representation {string label; string target; string modelName; string modelType; string name; list<string> parameters;}"),
                    context);
            parser.parse (
                    new ParsecFileReader ()
                            .read_memory ("struct gauge_state {list<typed_attribute_with_value> setup; list<typed_attribute_with_value> config; list<command_representation> commands;}"),
                    context);
        }
        catch (LocalizedParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
    }

}
