package org.sa.rainbow.gui.arch;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.DesktopIconUI;
import javax.swing.table.DefaultTableModel;

import org.ho.yaml.Yaml;
import org.sa.rainbow.core.error.RainbowException;
import org.sa.rainbow.core.gauges.GaugeInstanceDescription;
import org.sa.rainbow.core.gauges.OperationRepresentation;
import org.sa.rainbow.core.models.commands.IRainbowOperation;
import org.sa.rainbow.core.ports.eseb.converters.CommandRepresentationConverter;
import org.sa.rainbow.core.util.Pair;
import org.sa.rainbow.gui.GaugePanel;
import org.sa.rainbow.gui.widgets.TableColumnAdjuster;
import org.sa.rainbow.gui.widgets.TimeSeriesPanel;
import org.sa.rainbow.gui.widgets.TimeSeriesPanel.ICommandProcessor;

public class ArchGuagePanel extends GaugePanel {

	private GaugeInfo m_gaugeInfo;
	private HashMap<String, Integer> m_op2row = new HashMap<>();

	public ArchGuagePanel(String gaugeId, GaugeInfo gaugeInfo) {
		super(gaugeId);
		m_gaugeInfo = gaugeInfo;
	}

	@Override
	public void createContent() {
		setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		Object[][] data = {};
		Object[] colNames = { "Operation", "Target", "Parameters" };
		DefaultTableModel tableModel = new DefaultTableModel(data, colNames);
		m_table = new JTable(tableModel);
//		m_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		scrollPane.setViewportView(m_table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		m_table.setAutoscrolls(true);
		TableColumnAdjuster tca = new TableColumnAdjuster(m_table);
		tca.setDynamicAdjustment(true);
		List<Pair<String, OperationRepresentation>> signatures = m_gaugeInfo.getDescription().commandSignatures();
		int row = 0;
		for (Pair<String, OperationRepresentation> pair : signatures) {
			String name = pair.secondValue().getName();
			tableModel.addRow(new String[] { name, "", "" });
			m_op2row.put(name, row++);
		}
		m_table.setPreferredScrollableViewportSize(new Dimension(250, 50 * m_op2row.size()));

	}

	@Override
	public void requestModelUpdate(IRainbowOperation command) throws IllegalStateException, RainbowException {
		if (!m_gaugeId.equals(command.getOrigin()))
			return;
		boolean update = true;
		m_table.clearSelection();
		processOperation(command, update, false);
	}

	@Override
	public void requestModelUpdate(List<IRainbowOperation> commands, boolean transaction)
			throws IllegalStateException, RainbowException {
		m_table.clearSelection();

		for (IRainbowOperation op : commands) {
			processOperation(op, false, true);
		}
		for (Runnable runnable : updaters) {
			runnable.run();
		}
	}

	protected void processOperation(IRainbowOperation command, boolean update, boolean extend) throws RainbowException {

		int row = updateOperation(command);
		if (update) {
			for (Runnable runnable : updaters) {
				runnable.run();
			}
		}
		m_table.changeSelection(row, 0, false, extend);
	}

	private int updateOperation(IRainbowOperation op) throws RainbowException {
		DefaultTableModel tableModel = (DefaultTableModel) m_table.getModel();
		String[] data = getTableData(op);
		Integer row = m_op2row.get(data[0]);
		if (row == null)
			throw new RainbowException(data[0] + " is not a known command");
		tableModel.setValueAt(data[1], row, 1);
		tableModel.setValueAt(data[2], row, 2);
		m_gaugeInfo.getOperations().get(op.getName()).add(op);
//		m_table.firePropertyChange("model", 0, 1);
		return row;
	}

	public DesktopIconUI createIcon(JInternalFrame frame, Map<String, Object> uidb) {

		if (m_gaugeInfo != null && m_gaugeInfo.getDescription() != null) {
			GaugeInstanceDescription desc = m_gaugeInfo.getDescription();
			Map<String, Object> ui = (Map<String, Object>) uidb.get(desc.gaugeName());
			if (ui == null) {
				ui = (Map<String, Object>) uidb.get(desc.gaugeType());
			}
			if (ui != null) {
				Map<String, Object> builtin;
				if ((builtin = (Map<String, Object>) ui.get("builtin")) != null) {
					String category = (String) builtin.get("category");
					if ("timeseries".equals(category)) {
						String xLabel = (String) builtin.get("xlabel");
						String yLabel = (String) builtin.get("ylabel");
						String command = (String) builtin.get("command");
						Double upper = (Double )builtin.get("upper");
						Double lower = (Double )builtin.get("lower");
						final String value = (String) builtin.get("value");
						if (command != null && value != null) {
							final OperationRepresentation rep = OperationRepresentation.parseCommandSignature(command);
							int param = 0;
							String[] parameters = rep.getParameters();
							for (String p : parameters) {
								if (value.equals(p)) break;
								param++;
							}
							final int theParam = param;
							ICommandProcessor processor = (op) -> {
								return Double.parseDouble(op.getParameters()[theParam]);
							};
							TimeSeriesPanel ts = new TimeSeriesPanel(null,
									 null,upper,lower, processor);
							ts.setSampleWindow(10);
							return new RainbowTimeSeriesIconUI(ts);
						}
					}
				}
			}

		}

		return new RainbowDesktopIconUI(frame.getFrameIcon());
	}

}
