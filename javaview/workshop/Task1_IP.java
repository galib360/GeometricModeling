package workshop;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jv.object.PsDialog;
import jv.object.PsUpdateIf;
import jvx.project.PjWorkshop_IP;

public class Task1_IP extends PjWorkshop_IP implements ActionListener {
	
	protected Button genusbtn;
	protected Button volumebtn;
	protected Button componentbtn;
	
	protected Label genuslbl;
	protected Label volumelbl;
	protected Label componentlbl;
	
	Task1 t1;
	
	public Task1_IP(){
		super();
		if(getClass()== Task1_IP.class){
			init();
		}
	}
	
	public void init(){
		super.init();
		setTitle("Assignment1 Task1");
	}
	
	public void setParent(PsUpdateIf parent){
		super.setParent(parent);
		t1 = (Task1)parent;
		
		addSubTitle("task 1");
		
		genusbtn = new Button("Number of Genus");
		genusbtn.addActionListener(this);
		volumebtn = new Button("Compute Volume");
		volumebtn.addActionListener(this);
		componentbtn = new Button("No. of Connected Components");
		componentbtn.addActionListener(this);
		
		genuslbl = new Label();
		volumelbl = new Label();
		componentlbl = new Label();
		
		Panel panel = new Panel(new GridLayout(3, 2));
		panel.add(genusbtn);
		panel.add(genuslbl);
		panel.add(volumebtn);
		panel.add(volumelbl);
		panel.add(componentbtn);
		panel.add(componentlbl);

		add(panel);
		
		validate();
		
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == genusbtn) {
			genuslbl.setText("...");
			genuslbl.setText(t1.computeGenus() + "");
			t1.m_geom.update(t1.m_geom);
			return;
		} else if (source == volumebtn) {
			volumelbl.setText("...");
			volumelbl.setText(t1.computeVolume() + "");
			t1.m_geom.update(t1.m_geom);
			return;
		} else if (source == componentbtn) {
			componentlbl.setText("...");
			componentlbl.setText(t1.computeComponents() + "");
			t1.m_geom.update(t1.m_geom);
			return;
		} 
	}
	
	protected int getDialogButtons(){
		return PsDialog.BUTTON_OK;
	}
}