package de.hshannover.f4.trust.visitmeta.gui.dialog;

import java.awt.Component;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.UniformInterfaceException;

import de.hshannover.f4.trust.visitmeta.Main;
import de.hshannover.f4.trust.visitmeta.gui.dialog.ConnectionDialog.DataServicePanel;
import de.hshannover.f4.trust.visitmeta.gui.dialog.ConnectionDialog.MapServerPanel;
import de.hshannover.f4.trust.visitmeta.gui.dialog.ConnectionDialog.TabPanel;
import de.hshannover.f4.trust.visitmeta.gui.util.DataserviceConnection;
import de.hshannover.f4.trust.visitmeta.gui.util.RestConnection;
import de.hshannover.f4.trust.visitmeta.util.yaml.DataservicePersister;
import de.hshannover.f4.trust.visitmeta.util.yaml.DataservicePersisterException;

public class CheckSavingTabbedPane extends JTabbedPane {

	private static final long serialVersionUID = -3638300978826054978L;

	private static final Logger log = Logger.getLogger(CheckSavingTabbedPane.class);

	private static DataservicePersister mDataservicePersister = Main.getDataservicePersister();

	static {
		log.addAppender(new JTextAreaAppander());
	}

	@Override
	public void setSelectedIndex(int index){
		if(getSelectedComponent() != null && getSelectedComponent() instanceof TabPanel){
			TabPanel panel = (TabPanel)getSelectedComponent();
			if(panel.mChanges){
				int n = JOptionPane.showConfirmDialog(this, "Would you like to save your changes?", "Save changes?", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION){
					try{
						yesOption(panel);
					} catch (FileNotFoundException	| DataservicePersisterException | JSONException e) {
						// logging already finished
					}
				}else if(n == JOptionPane.NO_OPTION){
					noOption(panel);
					return;
				}else if(n == JOptionPane.CLOSED_OPTION){
					return;
				}
			}
		}
		super.setSelectedIndex(index);
	}

	public void yesOption(Component selectedComponent) throws FileNotFoundException, DataservicePersisterException, UniformInterfaceException, JSONException {
		if(selectedComponent instanceof DataServicePanel){
			DataServicePanel panel = (DataServicePanel)selectedComponent;

			DataserviceConnection tmp = panel.mPreviousConnection.copy();	// for rollback
			panel.updateDataserviceConnection(panel.mPreviousConnection);

			try {
				mDataservicePersister.update(tmp.getName(), panel.mPreviousConnection);
				panel.mChanges = false;
			} catch (FileNotFoundException e) {
				log.error("Error while updating the Dataservice-Connection(" + tmp.getName() + ")", e);
				//rollBack
				panel.mPreviousConnection.update(tmp);
				throw e;
			} catch (DataservicePersisterException e) {
				log.warn(e.toString());
				//rollBack
				panel.mPreviousConnection.update(tmp);
				throw e;
			}
		}else if(selectedComponent instanceof MapServerPanel){
			MapServerPanel panel = (MapServerPanel)selectedComponent;

			RestConnection tmp = panel.mPreviousConnection.copy();	// for rollback
			panel.updateRestConnection(panel.mPreviousConnection);	// update the model

			try {
				panel.mPreviousConnection.saveInDataservice();
				panel.mChanges = false;
			}catch (UniformInterfaceException | JSONException e){
				log.error("error while save RestConnection in dataservice", e);
				//rollBack
				panel.mPreviousConnection.update(tmp);
				throw e;
			}
		}
	}
	public void noOption(Component selectedComponent) {
		// nothing to do
	}

	//	public void
}