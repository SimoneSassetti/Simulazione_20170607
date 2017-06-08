package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Match> getPartite(Season s, Map<String,Team> mappaTeam) {
		String sql="SELECT match_id,Season,`Div`,Date,t1.team as h,t2.team as a,FTHG,FTAG,FTR "+
					"FROM matches, teams as t1, teams as t2 "+
					"WHERE `Div`='I1' AND Season=? AND matches.HomeTeam=t1.team AND matches.AwayTeam=t2.team";
		
		List<Match> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet rs = st.executeQuery() ;
			
			while(rs.next()) {
				String h=rs.getString("h");
				String a=rs.getString("a");
				if(mappaTeam.get(h)==null || mappaTeam.get(a)==null){
					 System.out.println("ERRORE! Il team non e' presente!");
				}else{
					result.add(new Match(rs.getInt("match_id"),s,rs.getString("Div"),
							rs.getDate("Date").toLocalDate(),mappaTeam.get(h),mappaTeam.get(a),rs.getInt("FTHG"),
							rs.getInt("FTAG"),rs.getString("FTR")));
				}
			}
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Team> getTeam(Season s) {
		String sql="SELECT distinct(t1.team) FROM matches,teams as t1 WHERE matches.Season=? AND t1.team=matches.HomeTeam";
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet rs = st.executeQuery() ;
			
			while(rs.next()) {
				result.add(new Team(rs.getString("team")));
			}
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}


}
