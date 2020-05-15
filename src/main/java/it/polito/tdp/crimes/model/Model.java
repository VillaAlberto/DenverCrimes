package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	private EventsDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;

	public Model() {
		dao = new EventsDao();

	}

	public List<String> getCategories() {
		List<String> ls = dao.listAllCategories();
		ls.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return ls;
	}

	public List<Month> getMonths() {
		return dao.listAllMonths();
	}

	public void creaGrafo(int mese, String category) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		// creo i vertici
		List<String> vertici = dao.listAllVertex(mese, category);
		Graphs.addAllVertices(grafo, vertici);

		// creo archi
		if (vertici.size() > 1) {
			for (int i = 0; i < vertici.size(); i++) {
				for (int j = 1; j < vertici.size(); j++) {
					if (i < j) {
						int peso = dao.calcolaPeso(vertici.get(i), vertici.get(j), mese);
						if (peso > 0)
							Graphs.addEdge(grafo, vertici.get(i), vertici.get(j), peso);
					}
				}
			}
		}

	}

	public int numVertex() {
		return grafo.vertexSet().size();
	}

	public int numEdges() {
		return grafo.edgeSet().size();
	}

	public String output() {
		String s = "";
		int tot = 0;
		for (DefaultWeightedEdge d : grafo.edgeSet()) {
			tot += grafo.getEdgeWeight(d);
		}

		if (numEdges() == 0) {
			s = "Non ci sono archi";
			return s;
		}

		double media = tot / numEdges();
		for (DefaultWeightedEdge d : grafo.edgeSet()) {
			if (grafo.getEdgeWeight(d) >= media)
				s += d.toString() + " " + grafo.getEdgeWeight(d) + " \n";
		}
		return s;
	}

}
