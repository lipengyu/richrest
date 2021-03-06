package com.googlecode.richrest.client.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.googlecode.richrest.client.Client;
import com.googlecode.richrest.client.PropertyInfo;
import com.googlecode.richrest.client.event.PropertyAdapter;
import com.googlecode.richrest.client.event.PropertyEvent;
import com.googlecode.richrest.client.event.PropertyListener;

public class ConfigurationPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private Icon enableIcon = Images.getIcon("enable.gif");

	private Icon disableIcon = Images.getIcon("disable.gif");

	private final DefaultListModel configurationModel;

	private final JList configurationList;

	public JList getConfigurationList() {
		return configurationList;
	}

	private final JToolBar toolBar = new JToolBar();

	public JToolBar getToolBar() {
		return toolBar;
	}

	private final JToolBar statusBar = new JToolBar();

	public JToolBar getStatusBar() {
		return statusBar;
	}

	private final Client client;

	public ConfigurationPane(final Client client) {
		if (client == null)
			throw new NullPointerException("Client == null!");
		this.client = client;
		this.setLayout(new BorderLayout());

		toolBar.setFloatable(false);
		this.add(BorderLayout.NORTH, toolBar);

		statusBar.setFloatable(false);
		statusBar.setLayout(new BorderLayout());
		this.add(BorderLayout.SOUTH, statusBar);
		JPanel descPane = new JPanel();
		descPane.add(new JLabel("已使用", enableIcon, JLabel.LEFT));
		descPane.add(new JLabel("未使用", disableIcon, JLabel.LEFT));
		statusBar.add(BorderLayout.EAST, descPane);

		final JLabel nameLabel = new JLabel();
		statusBar.add(BorderLayout.WEST, nameLabel);

		configurationModel = new DefaultListModel();
		configurationList = new JList(configurationModel);
		configurationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		configurationList.setCellRenderer(new ConfigurationListCellRenderer());
		final JScrollPane actionListPane = new JScrollPane();
		actionListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		actionListPane.getViewport().setView(configurationList);
		this.add(BorderLayout.CENTER, actionListPane);

		final JButton saveButton = new JButton("保存", Images.getIcon("save.gif"));
		saveButton.setToolTipText("保存配置项");
		toolBar.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setSelectedFile(new File("transport.properties"));
				int i = fileChooser.showSaveDialog(ConfigurationPane.this);
				if (i == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file.exists()) {
						int ch = JOptionPane.showConfirmDialog(ConfigurationPane.this, "文件已存在，是否覆盖?", "保存", JOptionPane.YES_NO_OPTION);
						if (ch != JOptionPane.YES_OPTION)
							return;
					}
					try {
						FileWriter writer = null;
						try {
							writer = new FileWriter(file);
							for (Map.Entry<Object, Object> entry : client.getProperties().entrySet()) {
								writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
							}
							writer.flush();
						} finally {
							if (writer != null)
								writer.close();
						}
						JOptionPane.showMessageDialog(ConfigurationPane.this, "保存配置项成功!", "保存", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(ConfigurationPane.this, "保存配置列表失败! 原因: " + ex.getMessage(), "保存", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});

		final JButton editButton = new JButton("修改", Images.getIcon("edit.gif"));
		editButton.setToolTipText("修改配置项");
		editButton.setEnabled(false);
		toolBar.add(editButton);
		/*editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getConfigurationModelSize() == 0) {
					JOptionPane.showMessageDialog(ConfigurationPane.this, "没有任何配置项!", "修改", JOptionPane.WARNING_MESSAGE);
					return;
				}
				final PropertyInfo configuration = (PropertyInfo)configurationList.getSelectedValue();
				if (configuration == null) {
					JOptionPane.showMessageDialog(ConfigurationPane.this, "请选择配置项!", "修改", JOptionPane.WARNING_MESSAGE);
					return;
				}
				String desc = configuration.getDescription();
				if (desc != null && desc.length() > 0) {
					int ch = JOptionPane.showConfirmDialog(ConfigurationPane.this, desc + "\n是否继续修改?", "修改", JOptionPane.YES_NO_OPTION);
					if (ch != JOptionPane.YES_OPTION)
						return;
				}
				Collection<String> options = configuration.getOptions();
				String newValue;
				if (options == null || options.size() == 0)
					newValue = JOptionPane.showInputDialog(ConfigurationPane.this, "请输入配置项\"" + configuration.getNameOrKey() + "\"的新值：", configuration.getValueOrDefault());
				else
					newValue = (String)JOptionPane.showInputDialog(ConfigurationPane.this, "请选择配置项\"" + configuration.getNameOrKey() + "\"的新值：", "选择", JOptionPane.INFORMATION_MESSAGE, Images.getIcon("edit.gif"), options.toArray(), configuration.getValueOrDefault());
				if (newValue != null) {
					client.setProperty(configuration.getKey(), newValue);
					JOptionPane.showMessageDialog(ConfigurationPane.this, "修改配置项成功!", "修改", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});*/
		configurationList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				final PropertyInfo configuration = (PropertyInfo)configurationList.getSelectedValue();
				if (configuration != null) {
					String name = configuration.getName();
					if (name != null)
						nameLabel.setText(name);
					else
						nameLabel.setText("");
				}
				editButton.setEnabled(configuration != null);
			}
		});

		JButton resetButton = new JButton("刷新", Images.getIcon("refresh.gif"));
		resetButton.setToolTipText("刷新配置列表");
		toolBar.add(resetButton);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					refreshConfigurationList();
					JOptionPane.showMessageDialog(ConfigurationPane.this, "刷新配置列表成功!", "刷新配置列表", JOptionPane.INFORMATION_MESSAGE);
				} catch (Throwable t) {
					JOptionPane.showMessageDialog(ConfigurationPane.this, "刷新配置列表失败! 原因: " + t.getMessage(), "刷新配置列表", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		try {
			refreshConfigurationList();
		} catch (Throwable t) {
			// ignore
		}

		configurationListener = new ConfigurationDelegate(new PropertyAdapter() {
			public void onPropertyChanged(final PropertyEvent event) {
				PropertyInfo configuration = event.getPropertyInfo();
				synchronized (configurationModel) {
					configurationModel.removeElement(configuration);
					configurationModel.addElement(configuration);
				}
			}
		});
		client.addListener(configurationListener);
	}

	private void refreshConfigurationList() {
		Map<String, PropertyInfo> configurations = client.getPropertyInfos();
		synchronized (configurationModel) {
			configurationModel.clear();
			for (PropertyInfo configuration : configurations.values()) {
				configurationModel.addElement(configuration);
			}
		}
	}

	private final PropertyListener configurationListener;

	public void dispose() {
		client.removeListener(configurationListener);
	}

	private int getConfigurationModelSize() {
		synchronized (configurationModel) {
			return configurationModel.getSize();
		}
	}

	private class ConfigurationListCellRenderer extends DefaultListCellRenderer  {

		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			PropertyInfo configuration = (PropertyInfo)value;
			if(configuration.getName() != null)
				this.setIcon(enableIcon);
			else
				this.setIcon(disableIcon);
			this.setText(configuration.toString());
			return this;
		}

	}

}
