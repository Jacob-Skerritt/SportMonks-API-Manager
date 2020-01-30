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

    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {

        final String TOKEN = "IeJEyAVbp2IjoYzCdGpZBk7mWOAzSkRXHeiYYeOK9OWgOI0iNjaTcGAXsHfG";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Config database = new Config("jdbc:mysql://localhost/in_game_ratings", "root", "");
        try (Connection db = database.getDatabaseConnection()) {
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
            String livescoresEndpoint2 = "https://soccer.sportmonks.com/api/v2.0/livescores/now?api_token=" + TOKEN + "&include=events,bench,lineup,corners,stats&page=";
            LocalDate sd = LocalDate.parse("2020-01-01");
            LocalDate ed = LocalDate.parse("2020-06-01");
            String fixturesEndpoint = "https://soccer.sportmonks.com/api/v2.0/fixtures/between/" + sd + "/" + ed + "?api_token=" + TOKEN + "&include=events,bench,lineup,stats,corners&leagues=&page=";
            String fixturesPremierLeagueEndpoint = "https://soccer.sportmonks.com/api/v2.0/fixtures/between/2019-08-08/2020-06-01?api_token=IeJEyAVbp2IjoYzCdGpZBk7mWOAzSkRXHeiYYeOK9OWgOI0iNjaTcGAXsHfG&leagues=8&include=events,corners,bench,lineup,stats&page=";
            
            //continents.manageContinents(continentEndpoint);
            //countries.manageCountires(countriesEndpoint);
            //league.manageLeagues(leaguesEndpoint);
            //seasons.manageSeasons(seasonsEndpoint);
            //venues.manageVenues(venuesEndpoint);
            //stages.manageStages(stagesEndpoint);
            //rounds.manageRounds(roundsEndpoint);
            //teams.manageTeams(teamsEndpoint);
            //players.managePlayers(playersEndpoint);
            //fixtures.manageFixtures(fixturesEndpoint);
           //fixtures.getPastLeagueFixtures(fixturesPremierLeagueEndpoint);
              
              

//
//            LocalDateTime currentTime = LocalDateTime.now();
//            LocalDateTime futureTime = currentTime.plusSeconds(5);
//            while (true) {
//
//                if (currentTime.isAfter(futureTime)) {
//                    fixtures.manageLivescores(livescoresEndpoint2);
//                    futureTime = futureTime.plusSeconds(60);
//
//                    System.out.println("\nCurrent Time :" + currentTime + "\nFutureTime: " + futureTime + "\n");
//                }
//
//                currentTime = LocalDateTime.now();
//            }
        }
    }

}
