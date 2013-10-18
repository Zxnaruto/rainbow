package org.sa.rainbow.core.ports;

import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.sa.rainbow.core.RainbowDelegate;
import org.sa.rainbow.core.error.RainbowConnectionException;

public class DisconnectedRainbowDelegateConnectionPort extends AbstractDelegateConnectionPort {

    public DisconnectedRainbowDelegateConnectionPort () {
        super (null);
    }

    Logger                                   LOGGER     = Logger.getLogger (DisconnectedRainbowDelegateConnectionPort.class);

    static DisconnectedRainbowDelegateConnectionPort m_instance = new DisconnectedRainbowDelegateConnectionPort ();

    public static AbstractDelegateConnectionPort instance () {
        return m_instance;
    }
    
    @Override
    public IRainbowManagementPort connectDelegate (String delegateID, Properties connectionProperties)
            throws RainbowConnectionException {
        LOGGER.error ("Attempt to connect through a disconnected deployment port");
        return DisconnectedRainbowManagementPort.instance ();
    }

    @Override
    public void disconnectDelegate (String delegateId) {
        LOGGER.error ("Attempt to disconnect through a disconnected deployment port");


    }

    @Override
    public void report (String delegateID, ReportType type, String msg) {
        String log = MessageFormat.format ("Delegate: {0}: {1}", delegateID, msg);
        switch (type) {
        case INFO:
            LOGGER.info (log);
            break;
        case WARNING:
            LOGGER.warn (log);
            break;
        case ERROR:
            LOGGER.error (log);
            break;
        case FATAL:
            LOGGER.fatal (log);
            break;
        }
    }

    @Override
    public void dispose () {
    }

}