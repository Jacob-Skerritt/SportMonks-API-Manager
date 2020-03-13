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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;
import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;

/**
 *
 * @author Jacob skerrit
 * 
 * Main method for the program, controls used to manage the infinite while loop
 * 
 */
public class SMDAA {
    //Initialising the token used to validate the programs access to the SportMonks api endpoints
    static String TOKEN;
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, PropertyVetoException {

        getToken("C:\\Users\\anyone\\Desktop\\token.txt");
        System.out.println(TOKEN);
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver"); 
        dataSource.setJdbcUrl("jdbc:mysql://localhost/in_game_ratings");
        dataSource.setUser("root");
        dataSource.setPassword("");
        dataSource.setMinPoolSize(50);
        dataSource.setMaxIdleTime(3600);
        dataSource.setMaxConnectionAge(3600);
        
        
        

            //Initialising the livescore endpoints vairables
            String livescoresEndpoint = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&page=";
            //String livescoresEndpoint2 = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&page=";
            String livescoresEndpoint3 = "https://soccer.sportmonks.com/api/v2.0/livescores?api_token=" + TOKEN;
            String livescoresEndpoint4 = "https://soccer.sportmonks.com/api/v2.0/livescores?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&page=";
            //+ "&leagues=8"

            
            //Initialising the variables used for managing time in the loop
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime updateTime = LocalDateTime.now();
            LocalDateTime futureTime = currentTime.plusSeconds(5);
            LocalDateTime maintenanceTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth(), 00, 50);
            LocalDateTime[] livescoreTime = new LocalDateTime[2];
            LocalDateTime livescoreCheckTime = LocalDateTime.of(currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth(), 07, 00);
            livescoreCheckTime = livescoreCheckTime.minusDays(1);
            
            
            
            
        while (true) {

            try (Connection db = dataSource.getConnection()) {
                Fixtures fixtures = new Fixtures(db);

                //Weekly maintenance on the database data to ensure they are consistent with sportmonks
                if(maintenanceTime.isBefore(currentTime)){
                    System.out.println("Maintenance Started at : " + LocalDateTime.now());
                    dataMaitenance(db);
                    System.out.println("Maintenance Finished at : " + LocalDateTime.now() + "\n");
                    maintenanceTime = maintenanceTime.plusDays(7);
                    System.out.println("Next Maintenance Scheduled for : " + maintenanceTime + "\n");
                }
                
                if (currentTime.isAfter(updateTime)) {
                    System.out.println("\nUpdating Daily Fixtures Data Now :" + currentTime);
                    updateTime = LocalDateTime.now().plusMinutes(30);
                    fixtures.manageLivescores(livescoresEndpoint4);  
                        
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
                        futureTime = LocalDateTime.now().plusSeconds(100);
                        fixtures.manageLivescores(livescoresEndpoint);
                        
                        Thread.sleep(1000);
                        
                        
                    }
                    
                    
                    
                }
                
                //Sleeping the thread if there is nothing to be done in the near future
                if(currentTime.isBefore(maintenanceTime.minusHours(1))){
                    
                    if(currentTime.isAfter(livescoreTime[1].plusHours(1))){
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
                
            }catch(Exception e){
                System.out.println(e);
                dataSource = new ComboPooledDataSource();
                dataSource.setDriverClass("com.mysql.jdbc.Driver"); 
                dataSource.setJdbcUrl("jdbc:mysql://localhost/in_game_ratings");
                dataSource.setUser("root");
                dataSource.setPassword("");
                dataSource.setMinPoolSize(50);
                dataSource.setMaxIdleTime(3600);
                dataSource.setMaxConnectionAge(3600);
            }
            







        }
    }
    
    //Method to manage get and then update the data in the database,
    //A number of methods calls are commented out as they are not necessary for the curent testing period
    //Any method calls that are commented out are simply to save API calls or for testing purposes
    public static void dataMaitenance(Connection db) throws IOException, SQLException{
            
        //initialising all of the endpoint variables that will be use to access the SportMonks API endpionts
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
            
            //Initialising the objects that represent database tables
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
            fixtures.getPastLeagueFixtures(fixturesPremierLeagueEndpoint);
        
    }
    
    //Method used to get the API token  from a text file, required to make API connections
    public static void getToken(String txtFile) throws FileNotFoundException{
        File file = new File(txtFile); 
        Scanner sc = new Scanner(file); 

     
        TOKEN  = sc.nextLine();
        
    }
}
