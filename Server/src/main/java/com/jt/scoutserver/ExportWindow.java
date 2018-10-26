package com.jt.scoutserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jt.scoutserver.utils.ExcelUtils;
import com.jt.scoutserver.utils.ExcelWriter;
import com.jt.scoutserver.utils.Utils;
import com.jt.scoutserver.utils.Utils.Operator;

public class ExportWindow extends JFrame {

	private JComboBox<ExportOption> options = new JComboBox<ExportOption>();
	private JPanel operators = new JPanel();
	private JComboBox<Utils.Operator> operatorsBox = new JComboBox<Utils.Operator>();
	private JComboBox<String> fieldNames = new JComboBox<String>();
	private JButton export = new JButton("Export");
	private JTextField fieldValue = new JTextField(20);

	public ExportWindow() {
		super("Export Scouting Data");
		JPanel panel = new JPanel(new BorderLayout());
		setSize(400, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		for (ExportOption option : ExportOption.values())
			options.addItem(option);

		for (Field field : Utils.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Utils.Operator.class.isAssignableFrom(field.getType())) {
				try {
					operatorsBox.addItem((Operator) field.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					throw new RuntimeException(e1);
				}
			}
		}

		Map<String, Integer> map = ExcelWriter.getHeader(0, ExcelUtils.ALL_FILES, 0, ExcelWriter.allFiles());
		for (String key : map.keySet()) {
			fieldNames.addItem(key);
		}

		operators.setLayout(new BoxLayout(operators, BoxLayout.Y_AXIS));
		operators.add(new JLabel("All matches whose"));
		operators.add(fieldNames);
		operators.add(new JLabel("is"));
		operators.add(operatorsBox);
		operators.add(fieldValue);

		panel.add(options, BorderLayout.NORTH);
		panel.add(export, BorderLayout.SOUTH);
		panel.add(operators, BorderLayout.CENTER);

		setContentPane(panel);

		setVisible(true);

		export.addActionListener((e) -> {
			options.getItemAt(options.getSelectedIndex()).onClick(operatorsBox, fieldNames.getItemAt(fieldNames.getSelectedIndex()), fieldValue.getText());
		});
		options.addItemListener((e) -> updateCombo());
		updateCombo();
	}

	private void updateCombo() {
		for (Component c : operators.getComponents()) {
			c.setEnabled(options.getItemAt(options.getSelectedIndex()) == ExportOption.CUSTOM);
		}

	}

	private static int rowStart, colStart;

	private enum ExportOption {
		//format:off
		ALL("All Matches", (a, b, c) -> {
			File file = ExcelUtils.saveExcelFile();
			if (file == null)
				return;
			ExcelWriter.write(file, rowStart, colStart, ExcelUtils.ALL_FILES, 0);
		}), 
		MATCH("Single Match", (a, b, c) -> {
			int matchNumber = Utils.getIntInput(0, 100000, "Enter Match Number", "Enter the match number to export");
			if(matchNumber == Integer.MIN_VALUE) 
				return;
			File file = ExcelUtils.saveExcelFile();
			if (file == null)
				return;
			ExcelWriter.write(file, rowStart, colStart, ExcelUtils.SINGLE_MATCH, matchNumber);
		}), 
		TEAM("Single Team", (a, b, c) -> {
			int teamNumber = Utils.getIntInput(0, 100000, "Enter Team Number", "Enter the team number to export");
			if(teamNumber == Integer.MIN_VALUE) 
				return;
			File file = ExcelUtils.saveExcelFile();
			if (file == null)
				return;
			ExcelWriter.write(file, rowStart, colStart, ExcelUtils.SINGLE_TEAM, teamNumber);
		}),
		CUSTOM("Custom", (operatorsBox, fieldName, fieldValue) -> {
			File file = ExcelUtils.saveExcelFile();
			if (file == null)
				return;
			List<File> files = Utils.getFilesWith(fieldName, operatorsBox.getItemAt(operatorsBox.getSelectedIndex()), fieldValue);
			ExcelWriter.write(file, rowStart, colStart, ExcelUtils.ALL_FILES, 0, files);
		});
		//format:on
		String name;
		MyRunnable onClick;

		ExportOption(String name, MyRunnable onClick) {
			this.name = name;
			this.onClick = onClick;
		}

		@Override
		public String toString() {
			return name;
		}

		public void onClick(JComboBox<Utils.Operator> a, String b, String c) {
			onClick.run(a, b, c);
		}

	}

	private interface MyRunnable {
		public void run(JComboBox<Utils.Operator> operatorsBox, String fieldName, String fieldValue);
	}

}
