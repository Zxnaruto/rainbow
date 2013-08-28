package org.sa.rainbow.management.ports.eseb;

import java.io.IOException;
import java.util.Properties;

import org.sa.rainbow.core.Identifiable;
import org.sa.rainbow.core.RainbowDelegate;
import org.sa.rainbow.core.RainbowMaster;
import org.sa.rainbow.core.error.RainbowConnectionException;
import org.sa.rainbow.gauges.IGauge;
import org.sa.rainbow.gauges.IGaugeConfigurationInterface;
import org.sa.rainbow.gauges.IGaugeIdentifier;
import org.sa.rainbow.gauges.IGaugeQueryInterface;
import org.sa.rainbow.gauges.IRainbowGaugeLifecycleBusPort;
import org.sa.rainbow.gauges.ports.eseb.ESEBGaugeConfigurationInterfaceProvider;
import org.sa.rainbow.gauges.ports.eseb.ESEBGaugeConfigurationInterfaceRequirer;
import org.sa.rainbow.gauges.ports.eseb.ESEBGaugeQueryInterfaceProvider;
import org.sa.rainbow.gauges.ports.eseb.ESEBGaugeQueryInterfaceRequirer;
import org.sa.rainbow.management.ports.DisconnectedRainbowManagementPort;
import org.sa.rainbow.management.ports.DisconnectedRainbowMasterConnectionPort;
import org.sa.rainbow.management.ports.IRainbowConnectionPortFactory;
import org.sa.rainbow.management.ports.IRainbowManagementPort;
import org.sa.rainbow.management.ports.IRainbowMasterConnectionPort;
import org.sa.rainbow.models.IModelsManager;
import org.sa.rainbow.models.ports.IRainbowModelChangeBusPort;
import org.sa.rainbow.models.ports.IRainbowModelUSBusPort;
import org.sa.rainbow.models.ports.eseb.ESEBChangeBusAnnouncePort;
import org.sa.rainbow.models.ports.eseb.ESEBGaugeModelUSBusPort;
import org.sa.rainbow.models.ports.eseb.ESEBModelManagerModelUpdatePort;

import edu.cmu.cs.able.eseb.participant.ParticipantException;

public class ESEBRainbowPortFactory implements IRainbowConnectionPortFactory {

    private static ESEBRainbowPortFactory m_instance;

    private ESEBRainbowPortFactory () {

    }

    @Override
    public IRainbowMasterConnectionPort createDelegateSideConnectionPort (RainbowDelegate delegate) {
        try {
            return new ESEBDelegateConnectionPort (delegate);
        }
        catch (IOException e) {
            return DisconnectedRainbowMasterConnectionPort.instance ();
        }

    }

    @Override
    public IRainbowMasterConnectionPort createMasterSideConnectionPort (RainbowMaster rainbowMaster) {

        try {
            return new ESEBMasterConnectionPort (rainbowMaster);
        }
        catch (IOException e) {
            return DisconnectedRainbowMasterConnectionPort.instance ();
        }
    }

    @Override
    public IRainbowManagementPort createDelegateSideManagementPort (RainbowDelegate delegate, String delegateID) {
        try {
            return new ESEBDelegateManagementPort (delegate);
        }
        catch (IOException e) {
            return DisconnectedRainbowManagementPort.instance ();
        }
    }

    @Override
    public IRainbowManagementPort createMasterSideManagementPort (RainbowMaster rainbowMaster,
            String delegateID,
            Properties connectionProperties) {
        try {
            return new ESEBMasterSideManagementPort (rainbowMaster, delegateID, connectionProperties);
        }
        catch (Throwable t) {
            return DisconnectedRainbowManagementPort.instance ();
        }

    }

    public static IRainbowConnectionPortFactory getFactory () {
        if (m_instance == null) {
            m_instance = new ESEBRainbowPortFactory ();
        }
        return m_instance;
    }

    @Override
    public IRainbowModelUSBusPort createModelsManagerUSPort (IModelsManager m) throws RainbowConnectionException {
        try {
            return new ESEBModelManagerModelUpdatePort (m);
        }
        catch (IOException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IRainbowModelUSBusPort createModelsManagerClientUSPort (Identifiable client)
            throws RainbowConnectionException {
        try {
            return new ESEBGaugeModelUSBusPort (client);
        }
        catch (IOException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IRainbowGaugeLifecycleBusPort createGaugeSideLifecyclePort () throws RainbowConnectionException {
        try {
            return new ESEBGaugeSideLifecyclePort ();
        }
        catch (IOException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IRainbowModelChangeBusPort createChangeBusAnnouncePort () throws RainbowConnectionException {
        try {
            return new ESEBChangeBusAnnouncePort ();
        }
        catch (IOException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IRainbowGaugeLifecycleBusPort createManagerGaugeLifecyclePort (IRainbowGaugeLifecycleBusPort manager)
            throws RainbowConnectionException {
        try {
            return new ESEBReceiverSideGaugeLifecyclePort (manager);
        }
        catch (IOException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IGaugeConfigurationInterface createGaugeConfigurationPortClient (IGaugeIdentifier gauge)
            throws RainbowConnectionException {
        try {
            return new ESEBGaugeConfigurationInterfaceRequirer (gauge);
        }
        catch (IOException | ParticipantException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IGaugeQueryInterface createGaugeQueryPortClient (IGaugeIdentifier gauge)
            throws RainbowConnectionException {
        try {
            return new ESEBGaugeQueryInterfaceRequirer (gauge);
        }
        catch (IOException | ParticipantException e) {
            throw new RainbowConnectionException ("Failed to connect", e);
        }
    }

    @Override
    public IGaugeConfigurationInterface createGaugeConfigurationPort (IGauge gauge) throws RainbowConnectionException {
        try {
            return new ESEBGaugeConfigurationInterfaceProvider (gauge);
        }
        catch (IOException | ParticipantException e) {
            throw new RainbowConnectionException ("Failed to connect", e);

        }
    }

    @Override
    public IGaugeQueryInterface createGaugeQueryPort (IGauge gauge) throws RainbowConnectionException {
        try {
            return new ESEBGaugeQueryInterfaceProvider (gauge);
        }
        catch (IOException | ParticipantException e) {
            throw new RainbowConnectionException ("Failed to connect", e);

        }
    }

}
