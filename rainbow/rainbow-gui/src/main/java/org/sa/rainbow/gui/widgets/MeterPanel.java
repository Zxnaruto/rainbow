package org.sa.rainbow.gui.widgets;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.sa.rainbow.core.models.commands.IRainbowOperation;
import org.sa.rainbow.gui.widgets.TimeSeriesPanel.ICommandProcessor;

public class MeterPanel extends JPanel implements ICommandUpdate {

	private Double m_lower;
	private Double m_upper;
	private Double m_redZone;
	private ICommandProcessor m_processor;
	private DefaultValueDataset m_dataset;
	private JFreeChart m_chart;

	public MeterPanel(Double lower, Double upper, Double redZone, ICommandProcessor processor) {
		m_lower = lower;
		m_upper = upper;
		m_redZone = redZone;
		m_processor = processor;

		setLayout(new BorderLayout(0, 0));

		m_dataset = createDataSet();
		m_chart = createChart();
	}

	private JFreeChart createChart() {
		MeterPlot meter = new MeterPlot(m_dataset);
		meter.setRange(new Range(m_lower, m_upper));
		if (m_redZone != null) {
			meter.addInterval(new MeterInterval("High", new Range(m_redZone, m_upper), Color.red, new BasicStroke(4.0f),
					new Color(255, 0, 0, 128)));
		}
		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, meter, false);
		return chart;
	}

	private DefaultValueDataset createDataSet() {
		DefaultValueDataset value = new DefaultValueDataset();
		return value;
	}

	@Override
	public void newCommand(IRainbowOperation cmd) {
		m_dataset.setValue(m_processor.process(cmd));
	}

}