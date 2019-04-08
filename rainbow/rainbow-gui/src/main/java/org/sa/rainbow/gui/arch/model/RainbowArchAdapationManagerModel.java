package org.sa.rainbow.gui.arch.model;

import org.sa.rainbow.core.adaptation.IAdaptationManager;
import org.sa.rainbow.gui.arch.controller.AbstractRainbowController;
import org.sa.rainbow.gui.arch.controller.IRainbowUIController;

public class RainbowArchAdapationManagerModel extends RainbowArchModelElement {

	private IAdaptationManager<?> m_manager;

	public RainbowArchAdapationManagerModel(IAdaptationManager<?> manager) {
		super();
		m_manager = manager;
	}

	@Override
	public String getId() {
		return m_manager.id();
	}

}
