package com.isimo.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class CommandLine extends CompoundAction implements ActionListener {
	JTextArea textarea = null;
	Thread r = null;
	public CommandLine(LocationAwareElement definition, Action parent) {
		super(definition, parent);
		// TODO Auto-generated constructor stub
	}
	
	void createAndShowGUI() {
		try {
			JFrame frame = new JFrame("Enter command");
			frame.setSize(new Dimension(600, 250));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JLabel label = new JLabel("Command:");
			JPanel panel = new JPanel(new BorderLayout());
	        textarea = new JTextArea();
			JButton button = new JButton("Submit command");
			button.addActionListener(this);
	        frame.getContentPane().add(panel);
	        panel.add(label, BorderLayout.WEST);
	        panel.add(textarea, BorderLayout.CENTER);
	        panel.add(button, BorderLayout.SOUTH);
	        //Display the window.
	        frame.setVisible(true);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	@Override
	public void execute() {
		super.execute();
		if(!"false".equals(testExecutionManager.getProperties().get("isimo.nocommandline")))
			return;
		try {
			java.awt.EventQueue.invokeLater(r = new Thread() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
			r.join();
			testExecutionManager.sleep(100000000);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		try {
			log("Action "+actionEvent.getActionCommand());
			SAXReader reader = testExecutionManager.getSAXReader();
			Document doc = reader.read(new StringReader("<actions>"+textarea.getText()+"</actions>"));
			for(Object elem: doc.getRootElement().elements()) {
				Element element = (Element) elem;
				Action action = Action.getAction((LocationAwareElement)element, this.getParent());
				action.execute();
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
