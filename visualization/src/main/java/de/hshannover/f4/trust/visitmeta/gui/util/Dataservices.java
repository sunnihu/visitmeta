package de.hshannover.f4.trust.visitmeta.gui.util;

import java.util.ArrayList;
import java.util.List;

import de.hshannover.f4.trust.visitmeta.data.DataImpl;
import de.hshannover.f4.trust.visitmeta.interfaces.connections.DataserviceConnection;
import de.hshannover.f4.trust.visitmeta.interfaces.data.Data;

public class Dataservices extends DataImpl {

	List<Data> mList;

	@Override
	public String getName() {
		return Dataservices.class.getSimpleName();
	}

	@Override
	public List<Data> getSubData() {
		return new ArrayList<Data>(mList);
	}

	@Override
	public Data copy() {
		Dataservices tmpCopy = new Dataservices();
		tmpCopy.mList = getSubData();
		return tmpCopy;
	}

	@Override
	public Class<?> getDataTypeClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addDataserviceConnection(DataserviceConnection newDataserviceConnection) {
		mList.add(newDataserviceConnection);
	}

}
