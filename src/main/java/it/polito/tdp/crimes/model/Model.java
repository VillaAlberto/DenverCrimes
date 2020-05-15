package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	private EventsDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	private List<String> best;

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
	
	public Set<DefaultWeightedEdge> getEdges(){
		return grafo.edgeSet();
	}

	
	public List<String> calcolaPercorso(DefaultWeightedEdge e){
		best= new LinkedList<String>();
		String partenza= grafo.getEdgeSource(e);
		String arrivo=grafo.getEdgeTarget(e);
		
		
		List<String> parziale= new LinkedList<String>();
		parziale.add(partenza);
		ricorsiva(arrivo, parziale);
		return best;
	}

	private void ricorsiva(String arrivo, List<String> parziale) {
		if (parziale.get(parziale.size()-1).equals(arrivo)) {
			if(parziale.size()>best.size())
			{
				best=new LinkedList<String>(parziale);
			}
		}
		
		for (String s: Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))){
			if (!parziale.contains(s))
			{
				parziale.add(s);
				ricorsiva(arrivo, parziale);
				parziale.remove(s);
			}
		}
	}
	
	public String formatta() {
		String s="";
		int i=1;
		s+="#vertici: "+ numVertex()+" #archi: "+ numEdges()+"\n";
		for (String stringa: best)
		{
			s+=i+"- "+stringa+"\n";
			i++;
		}
		return s;
	}
}
