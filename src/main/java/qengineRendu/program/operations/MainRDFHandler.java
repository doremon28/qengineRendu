package qengineRendu.program.operations;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.StatisticData;
import qengineRendu.program.utils.TypeIndex;

public class MainRDFHandler extends AbstractRDFHandler{
	IDictionaryIndexesService dictionaryIndexesService = new DictionaryIndexesServiceImpl();
	@Override
	public void startRDF() throws RDFHandlerException {
		// TODO Auto-generated method stub
		super.startRDF();
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		// TODO Auto-generated method stub
		super.endRDF();
	}

	@Override
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
		// TODO Auto-generated method stub
		super.handleNamespace(prefix, uri);
	}

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		StatisticData.nbTriplet++;
		dictionaryBiMap(TypeIndex.SOP, st);
		dictionaryBiMap(TypeIndex.SPO, st);
		dictionaryBiMap(TypeIndex.POS, st);
		super.handleStatement(st);
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {
		// TODO Auto-generated method stub
		super.handleComment(comment);
	}

	private void dictionaryBiMap(TypeIndex typeIndex, Statement st) {
		dictionaryIndexesService.addEntryFromStatement(typeIndex, st);
	}

}
