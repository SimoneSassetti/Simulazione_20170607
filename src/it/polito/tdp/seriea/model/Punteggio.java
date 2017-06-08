package it.polito.tdp.seriea.model;

public class Punteggio implements Comparable<Punteggio>{
	
	private Team team;
	private int punt;

	public Punteggio(Team team, int punt) {
		super();
		this.team = team;
		this.punt = punt;
	}

	public int getPunt() {
		return punt;
	}

	public void setPunt(int punt) {
		this.punt = punt;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public int compareTo(Punteggio p) {
		return -(this.punt-p.punt);
	}
}
