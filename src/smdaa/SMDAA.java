/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smdaa;

import Database.Config;
import Database.Continents;
import Database.Countries;
import Database.Fixtures;
import Database.Leagues;
import Database.Players;
import Database.Rounds;
import Database.Seasons;
import Database.Stages;
import Database.Teams;
import Database.Venues;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 * @author anyone
 */
public class SMDAA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        
        
    final String TOKEN ="IeJEyAVbp2IjoYzCdGpZBk7mWOAzSkRXHeiYYeOK9OWgOI0iNjaTcGAXsHfG";
    //Class.forName not needed?? Must check when tested on Tomcat server at college, if issue arise it may be caused my missing .forName decleration
    Class.forName("com.mysql.cj.jdbc.Driver");
    Config database = new Config("jdbc:mysql://localhost/in_game_ratings","root", "" );
    Connection db = database.getDatabaseConnection();
    Continents continents = new Continents(db);
    Venues venues = new Venues(db);
    Countries countries = new Countries(db);
    Leagues league = new Leagues(db);
    Seasons seasons = new Seasons(db);
    Stages stages = new Stages(db);
    Rounds rounds = new Rounds(db);
    Teams teams = new Teams(db);
    Players players = new Players(db);
    Fixtures fixtures = new Fixtures(db);
   

    String continentEndpoint = "https://soccer.sportmonks.com/api/v2.0/continents?api_token=" + TOKEN + "&page=";
    String venuesEndpoint = "https://soccer.sportmonks.com/api/v2.0/venues/season/?api_token=" + TOKEN + "&page=";
    String countriesEndpoint = "https://soccer.sportmonks.com/api/v2.0/countries?api_token=" + TOKEN + "&page=";
    String leaguesEndpoint = "https://soccer.sportmonks.com/api/v2.0/leagues?api_token=" + TOKEN + "&page=";
    String seasonsEndpoint = "https://soccer.sportmonks.com/api/v2.0/seasons?api_token=" + TOKEN + "&page=";
    String stagesEndpoint = "https://soccer.sportmonks.com/api/v2.0/stages/season/?api_token=" + TOKEN + "&page=";
    String roundsEndpoint = "https://soccer.sportmonks.com/api/v2.0/rounds/season/?api_token=" + TOKEN + "&page=";
    String teamsEndpoint = "https://soccer.sportmonks.com/api/v2.0/teams/season/?api_token=" + TOKEN + "&page=";
    String playersEndpoint = "https://soccer.sportmonks.com/api/v2.0/squad/season//team/?api_token=" + TOKEN + "&include=player&page=";
    String livescoresEndpoint = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&leagues=8&page=";
    
    LocalDate sd = LocalDate.parse("2020-01-01");
    LocalDate ed = LocalDate.parse("2020-06-01");
    String fixturesEndpoint = "https://soccer.sportmonks.com/api/v2.0/fixtures/between/"+ sd +"/"+ ed +"?api_token=" + TOKEN + "&include=events,bench,lineup,corners&leagues=&page=";
    
   
   

    
    continents.manageContinents(continentEndpoint);
    countries.manageCountires(countriesEndpoint);
    league.manageLeagues(leaguesEndpoint);
    seasons.manageSeasons(seasonsEndpoint);
    venues.manageVenues(venuesEndpoint);
    stages.manageStages(stagesEndpoint);
    rounds.manageRounds(roundsEndpoint);
    teams.manageTeams(teamsEndpoint);
    players.managePlayers(playersEndpoint);
    fixtures.manageFixtures(fixturesEndpoint);
    fixtures.manageLivescores(livescoresEndpoint);
        
    
    db.close();
    
    
    
    

   

       
    
    /*
    LocalDateTime currentTime = LocalDateTime.now();
    LocalDateTime futureTime = currentTime.plusMinutes(1);
    futureTime = futureTime.plusSeconds(1);
    LocalDateTime requestTime = (LocalDateTime.now()).plusMinutes(1);
    int i =2000;
        
    while(currentTime.isBefore(futureTime)){
        System.out.println(futureTime);
        System.out.println(currentTime);
        if(currentTime.equals(requestTime)){
            try {
                // the mysql insert statement
                String query = " insert into leagues (id, active, country_id,  name,is_cup, current_season_id, available)"
                        + " values (?, ?, ?, ?,  ? , ?,?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = DB_Conn.prepareStatement(query);
                preparedStmt.setInt(1, i);
                preparedStmt.setBoolean(2, true);
                preparedStmt.setInt(3,i);
                preparedStmt.setString(4, "bob");
                preparedStmt.setBoolean(5, true);
                preparedStmt.setInt(6, i);
                preparedStmt.setBoolean(7,true);
                // execute the preparedstatement
                preparedStmt.execute();
                
                i++;
            }catch (SQLException ex) {

            }
        
            futureTime = futureTime.plusMinutes(60);
            requestTime = requestTime.plusMinutes(60);
        }
        currentTime = LocalDateTime.now();
        
        
    }*/
    }
    
}
