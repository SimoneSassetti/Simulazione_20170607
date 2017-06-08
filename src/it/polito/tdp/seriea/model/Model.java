package it.polito.tdp.seriea.model;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private List<Season> stagioni;
	private List<Match> partite;
	private Map<String, Team> team;
	private List<Team> listaTeam;
	private SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge> grafo;
	private List<DefaultWeightedEdge> finale;//dominio finale dato dal confrontoi di tutti i domini
	
	
	SerieADAO dao;
	
	public Model(){
		dao=new SerieADAO();
		finale=new ArrayList<DefaultWeightedEdge>();
	}
	
	public List<Season> getStagioni() {
		if(stagioni==null){
			stagioni=dao.listSeasons();
		}
		return stagioni;
	}
	
	public void getTeam(Season s){
		if(listaTeam==null){
			listaTeam=dao.getTeam(s);
		}
		for(Team t: listaTeam){
			team.put(t.getTeam(), t);
		}
		this.getPartite(s, team);
	}
	
	public List<Match> getPartite(Season s, Map<String,Team> t){
		partite=new ArrayList<Match>();
		partite=dao.getPartite(s,t);
		return partite;
	}
	
	public void creaGrafo(Season s){
		grafo=new SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		team=new HashMap<String,Team>();
		listaTeam=null;
		
		this.getTeam(s);
		
		Graphs.addAllVertices(grafo, listaTeam);
		
		for(Match m: partite){
			DefaultWeightedEdge a=grafo.addEdge(m.getHomeTeam(), m.getAwayTeam());
			if(a!=null){
				if(m.getFtr().compareTo("H")==0){
					grafo.setEdgeWeight(a, 1);
				}else if(m.getFtr().compareTo("D")==0){
					grafo.setEdgeWeight(a, 0);
				}else{
					grafo.setEdgeWeight(a, -1);
				}
			}
		}
	}
		
	public List<Punteggio> getClassifica(){
		List<Punteggio> classifica=new ArrayList<Punteggio>();
		int res=0;
		
		for(Team t: grafo.vertexSet()){
			res=0;
			for(DefaultWeightedEdge a: grafo.outgoingEdgesOf(t)){
				if(grafo.getEdgeWeight(a)==1){
					res+=3;
				}else if(grafo.getEdgeWeight(a)==0){
					res+=1;
				}
			}
			for(DefaultWeightedEdge a: grafo.incomingEdgesOf(t)){
				if(grafo.getEdgeWeight(a)==-1){
					res+=3;
				}else if(grafo.getEdgeWeight(a)==0){
					res+=1;
				}
			}
			classifica.add(new Punteggio(t,res));
		}
		Collections.sort(classifica);
		
		return classifica;
	}

	public List<DefaultWeightedEdge> trovaCammino() {
		
		for(Team t: grafo.vertexSet()){
			List<DefaultWeightedEdge> parziale=new ArrayList<DefaultWeightedEdge>();
			List<DefaultWeightedEdge> finale=new ArrayList<DefaultWeightedEdge>();
			int step=0;
			recursive(parziale,finale,step,t);
			if(finale.size()>this.finale.size()){
				this.finale.clear();
				this.finale.addAll(finale);
			}
		}
		return this.finale;
	}

	private void recursive(List<DefaultWeightedEdge> parziale,List<DefaultWeightedEdge> finale,int step,Team t) {
		if(finale.size()<parziale.size()){
			finale.clear();
			finale.addAll(parziale);
		}
		
		for(DefaultWeightedEdge a: grafo.outgoingEdgesOf(t)){
			if(grafo.getEdgeWeight(a)==1){
				if(!parziale.contains(a)){
					parziale.add(a);
					Team team=grafo.getEdgeTarget(a);
					recursive(parziale,finale,step+1,team);
					parziale.remove(a);
				}
			}
		}
	}
}
