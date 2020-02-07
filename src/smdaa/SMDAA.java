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
import java.time.LocalDateTime;

/**
 *
 * @author anyone
 */
public class SMDAA {
    final static String TOKEN = "IeJEyAVbp2IjoYzCdGpZBk7mWOAzSkRXHeiYYeOK9OWgOI0iNjaTcGAXsHfG";
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {

        
        Class.forName("com.mysql.cj.jdbc.Driver");
        Config database = new Config("jdbc:mysql://localhost/in_game_ratings", "root", "");
        try (Connection db = database.getDatabaseConnection()) {
            
            Fixtures fixtures = new Fixtures(db);

            
            String livescoresEndpoint = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&leagues=8&page=";
            //String livescoresEndpoint2 = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&page=";
            String livescoresEndpoint3 = "https://soccer.sportmonks.com/api/v2.0/livescores?api_token=" + TOKEN + "&leagues=8";


            

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime futureTime = currentTime.plusSeconds(5);
            LocalDateTime maintenanceTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth(), 00, 50);
            
            LocalDateTime[] livescoreTime = new LocalDateTime[2];
            LocalDateTime livescoreCheckTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth(), 07, 00);
            livescoreCheckTime = livescoreCheckTime.minusDays(1);
            
            
            
           while (true) {
                
                
               //Weekly maintenance on the database data to ensure they are consistent with sportmonks
                if(maintenanceTime.isBefore(currentTime)){
                    System.out.println("Maintenance Started at : " + LocalDateTime.now());
                    //dataMaitenance(db);
                    System.out.println("Maintenance Finished at : " + LocalDateTime.now() + "\n");
                    maintenanceTime = maintenanceTime.plusDays(7);
                    
                }
                
                //Getting the time frame for livegames during the day
                if(livescoreCheckTime.isBefore(currentTime)){
                    
                    System.out.println("Getting livescoreTimes" + LocalDateTime.now() + "\n");
                    livescoreTime = fixtures.getLivescoreTimes(livescoresEndpoint3);
                    livescoreCheckTime = livescoreCheckTime.plusDays(1);
                    System.out.println("LiveScore Timeframe : ");
                    System.out.println(livescoreTime[0] + " - " + livescoreTime[1] + "\n");
                    System.out.println("Finished livescoreTimes" + LocalDateTime.now() + "\n");
                    
                }
                    
                
                //Getting the livegame data from the livescores endpoint if a game is currently running 
                if(currentTime.isAfter(livescoreTime[0]) && currentTime.isBefore(livescoreTime[1]))
                {    
                    
                    if (currentTime.isAfter(futureTime)) {
                        System.out.println("\nGetting Live Data Now :" + currentTime);
                        fixtures.manageLivescores(livescoresEndpoint);
                        Thread.sleep(1000);
                        futureTime = LocalDateTime.now().plusSeconds(3);
                            
                    }

                    
                }
                
                //Sleeping the thread if there is nothing to be done in the near future
                if(currentTime.isBefore(maintenanceTime.minusHours(1))){
                    
                    if(currentTime.isAfter(livescoreTime[1].plusHours(30))){
                        System.out.println("Sleep Before Livescore: " + LocalDateTime.now()+ "\n");
                        Thread.sleep(3600 * 1000);
                        System.out.println("Waking up Before Livescore: " + LocalDateTime.now()+ "\n");
                    }
                    
                    if(currentTime.isBefore(livescoreTime[0].minusMinutes(90)))
                    {
                        System.out.println("Sleep After Livescore: " + LocalDateTime.now()+ "\n");
                        Thread.sleep(3600 * 1000);
                        System.out.println("Waking up after Livescore: " + LocalDateTime.now()+ "\n");
                    }  
                    
                    
                }else if(currentTime.isBefore(maintenanceTime.minusMinutes(1))){
                    Thread.sleep(60*1000);
                }

              //Updating the localTime variable
              currentTime = LocalDateTime.now();
              
            
            }
        }
    }
    
    //Method to manage get and then update the data in the database,
    //A number of methods calls are commented out as they are not necessary for the curent testing period
    public static void dataMaitenance(Connection db) throws IOException, SQLException{
        
            LocalDate sd = LocalDate.parse("2020-01-01");
            LocalDate ed = LocalDate.parse("2020-06-01");
            String fixturesEndpoint = "https://soccer.sportmonks.com/api/v2.0/fixtures/between/" + sd + "/" + ed + "?api_token=" + TOKEN + "&include=events,bench,lineup,stats,corners&leagues=&page=";
            String fixturesPremierLeagueEndpoint = "https://soccer.sportmonks.com/api/v2.0/fixtures/between/2019-08-08/2020-06-01?api_token=" + TOKEN + "&leagues=8&include=events,corners,bench,lineup,stats&page=";
            String continentEndpoint = "https://soccer.sportmonks.com/api/v2.0/continents?api_token=" + TOKEN + "&page=";
            String venuesEndpoint = "https://soccer.sportmonks.com/api/v2.0/venues/season/?api_token=" + TOKEN + "&page=";
            String countriesEndpoint = "https://soccer.sportmonks.com/api/v2.0/countries?api_token=" + TOKEN + "&page=";
            String leaguesEndpoint = "https://soccer.sportmonks.com/api/v2.0/leagues?api_token=" + TOKEN + "&page=";
            String seasonsEndpoint = "https://soccer.sportmonks.com/api/v2.0/seasons?api_token=" + TOKEN + "&page=";
            String stagesEndpoint = "https://soccer.sportmonks.com/api/v2.0/stages/season/?api_token=" + TOKEN + "&page=";
            String roundsEndpoint = "https://soccer.sportmonks.com/api/v2.0/rounds/season/?api_token=" + TOKEN + "&page=";
            String teamsEndpoint = "https://soccer.sportmonks.com/api/v2.0/teams/season/?api_token=" + TOKEN + "&include=coach&page=";
            String playersEndpoint = "https://soccer.sportmonks.com/api/v2.0/squad/season//team/?api_token=" + TOKEN + "&include=player&page=";
            
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
        
            //continents.manageContinents(continentEndpoint);
            //countries.manageCountires(countriesEndpoint);
            league.manageLeagues(leaguesEndpoint);
            seasons.manageSeasons(seasonsEndpoint);
            //venues.manageVenues(venuesEndpoint);
            //stages.manageStages(stagesEndpoint);
            //rounds.manageRounds(roundsEndpoint);
            teams.manageTeams(teamsEndpoint);
            players.managePlayers(playersEndpoint);
            fixtures.manageFixtures(fixturesEndpoint);
            fixtures.getPastLeagueFixtures(fixturesPremierLeagueEndpoint);
        
    }
}
