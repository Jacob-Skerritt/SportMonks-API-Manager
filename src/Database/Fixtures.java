package Database;

import SportMonks.Endpoint;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

public class Fixtures {

    private Connection db;
    private Leagues leagues;
    private FixturesPlayers fixturesPlayers;
    private FixturesEvents fixturesEvents;
    private FixturesCorners fixturesCorners;
    private FixturesTeams fixturesTeams;
    private Venues venues;

    public Fixtures() {

    }

    public Fixtures(Connection db) {
        this.db = db;
        this.leagues = new Leagues(db);
        this.fixturesPlayers = new FixturesPlayers(db);
        this.fixturesEvents = new FixturesEvents(db);
        this.fixturesCorners = new FixturesCorners(db);
        this.fixturesTeams = new FixturesTeams(db);
        this.venues = new Venues(db);
    }

    public void manageFixtures(String fixturesEndpoint) throws IOException, SQLException {

        JSONObject leagueIds = leagues.getJSONLeaguesId();
        JSONArray leaguesArray = leagueIds.getJSONArray("data");

        for (Object seasonId : leaguesArray) {
            JSONObject tempSeason = (JSONObject) seasonId;
            int id = tempSeason.getInt("id");
            String newFixturesEndpoint = Endpoint.makeNewEndpoint(fixturesEndpoint, id, "leagues=");

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;

            while (!lastPage) {

                JSONObject fixtures;

                try {
                    fixtures = Endpoint.getDataFromEndpoint(newFixturesEndpoint + i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray fixturesArray = fixtures.getJSONArray("data");
                JSONObject metaData = fixtures.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : fixturesArray) {
                    JSONObject tempObject = (JSONObject) obj;
                    JSONObject tempFixture = sanitiseFixture(tempObject);
                    
                    
                    if(!tempFixture.get("venue_id").toString().equals("null") && !venues.checkVenueExists(tempFixture.getInt("venue_id")))
                        venues.addVenue(tempFixture.getInt("venue_id"));

                    try {
                        // the mysql insert statement
                        String query = " insert into fixtures(id, league_id, season_id, stage_id, round_id, venue_id, weather_code, weather_type, weather_report_image, temperature, fixture_status, starting_time, starting_date, timezone, time_minute, time_second, added_time, extra_time, injury_time)"
                                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                + " league_id=VALUES(league_id), season_id=VALUES(season_id), stage_id=VALUES(stage_id), round_id=VALUES(round_id), venue_id=VALUES(venue_id),"
                                + " weather_code=VALUES(weather_code), weather_type=VALUES(weather_type), weather_report_image=VALUES(weather_report_image), temperature=VALUES(temperature),"
                                + " fixture_status=VALUES(fixture_status), starting_time=VALUES(starting_time), starting_date=VALUES(starting_date), timezone=VALUES(timezone),"
                                + " time_minute=VALUES(time_minute), time_second=VALUES(time_second), added_time=VALUES(added_time), extra_time=VALUES(extra_time), injury_time=VALUES(injury_time)";

                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = db.prepareStatement(query);
                        preparedStmt.setInt(1, tempFixture.getInt("id"));
                        preparedStmt.setInt(2, tempFixture.getInt("league_id"));
                        preparedStmt.setInt(3, tempFixture.getInt("season_id"));

                        if (!tempFixture.get("stage_id").toString().equals("null")) {
                            preparedStmt.setInt(4, tempFixture.getInt("stage_id"));
                        } else {
                            preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                        }

                        if (!tempFixture.get("round_id").toString().equals("null")) {
                            preparedStmt.setInt(5, tempFixture.getInt("round_id"));
                        } else {
                            preparedStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        if (!tempFixture.get("venue_id").toString().equals("null")) {
                            preparedStmt.setInt(6, tempFixture.getInt("venue_id"));
                        } else {
                            preparedStmt.setNull(6, java.sql.Types.VARCHAR);
                        }

                        preparedStmt.setString(7, tempFixture.getString("code"));
                        preparedStmt.setString(8, tempFixture.getString("type"));

                        preparedStmt.setString(9, tempFixture.get("icon").toString());

                        if (!tempFixture.get("temp").toString().equals("null")) {
                            preparedStmt.setInt(10, tempFixture.getInt("temp"));
                        } else {
                            preparedStmt.setNull(10, java.sql.Types.VARCHAR);
                        }

                        preparedStmt.setString(11, tempFixture.getString("status"));
                        preparedStmt.setTime(12, Time.valueOf(tempFixture.getString("time")));
                        preparedStmt.setDate(13, Date.valueOf(tempFixture.getString("date")));
                        preparedStmt.setString(14, tempFixture.getString("timezone"));

                        preparedStmt.setInt(15, tempFixture.getInt("minute"));
                        preparedStmt.setInt(16, tempFixture.getInt("second"));
                        preparedStmt.setInt(17, tempFixture.getInt("added_time"));
                        preparedStmt.setInt(18, tempFixture.getInt("extra_minute"));
                        preparedStmt.setInt(19, tempFixture.getInt("injury_time"));
                        // execute the preparedstatement
                        preparedStmt.execute();

                    } catch (SQLException ex) {

                    }

                    fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("bench"));
                    fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("lineup"));
                    fixturesEvents.addFixturesEvents(tempFixture.getJSONArray("events"));
                    fixturesCorners.addFixturesCorners(tempFixture.getJSONArray("corners"));
                    JSONObject localTeam = createFixtureTeam(tempFixture, true);
                    JSONObject visitorTeam = createFixtureTeam(tempFixture, false);

                    fixturesTeams.addFixturesTeams(localTeam);
                    fixturesTeams.addFixturesTeams(visitorTeam);

                }
                if (maxPage <= i) {
                    lastPage = true;
                } else {
                    i++;
                }

            }

        }

    }

    public void manageLivescores(String livescoresEndpoint) throws IOException, SQLException {

        System.out.println("Started now: " + LocalDateTime.now());

        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;

        while (!lastPage) {

            JSONObject fixtures;

            try {
                fixtures = Endpoint.getDataFromEndpoint(livescoresEndpoint + i);
            } catch (RuntimeException | IOException e) {
                System.out.println(e);
                break;
            }

            System.out.println("lenght of JSONObject: " + fixtures.toString().length());
            System.out.println("After API data: " + LocalDateTime.now());

            JSONArray fixturesArray = fixtures.getJSONArray("data");
            JSONObject metaData = fixtures.getJSONObject("meta");

            if (metaData.has("pagination")) {
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
            }

            for (Object obj : fixturesArray) {
                JSONObject tempObject = (JSONObject) obj;
                JSONObject tempFixture = sanitiseFixture(tempObject);

                //if(tempFixture.getInt("league_id") == 8)
                //{
                try {
                    // the mysql insert statement
                    String query = " insert into fixtures(id, league_id, season_id, stage_id, round_id, venue_id, weather_code, weather_type, weather_report_image, temperature, fixture_status, starting_time, starting_date, timezone, time_minute, time_second, added_time, extra_time, injury_time)"
                            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                            + " league_id=VALUES(league_id), season_id=VALUES(season_id), stage_id=VALUES(stage_id), round_id=VALUES(round_id), venue_id=VALUES(venue_id),"
                            + " weather_code=VALUES(weather_code), weather_type=VALUES(weather_type), weather_report_image=VALUES(weather_report_image), temperature=VALUES(temperature),"
                            + " fixture_status=VALUES(fixture_status), starting_time=VALUES(starting_time), starting_date=VALUES(starting_date), timezone=VALUES(timezone),"
                            + " time_minute=VALUES(time_minute), time_second=VALUES(time_second), added_time=VALUES(added_time), extra_time=VALUES(extra_time), injury_time=VALUES(injury_time)";

                    // create the mysql insert preparedstatement
                    PreparedStatement preparedStmt = db.prepareStatement(query);
                    preparedStmt.setInt(1, tempFixture.getInt("id"));
                    preparedStmt.setInt(2, tempFixture.getInt("league_id"));
                    preparedStmt.setInt(3, tempFixture.getInt("season_id"));

                    if (!tempFixture.get("stage_id").toString().equals("null")) {
                        preparedStmt.setInt(4, tempFixture.getInt("stage_id"));
                    } else {
                        preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                    }

                    if (!tempFixture.get("round_id").toString().equals("null")) {
                        preparedStmt.setInt(5, tempFixture.getInt("round_id"));
                    } else {
                        preparedStmt.setNull(5, java.sql.Types.VARCHAR);
                    }

                    if (!tempFixture.get("venue_id").toString().equals("null")) {
                        preparedStmt.setInt(6, tempFixture.getInt("venue_id"));
                    } else {
                        preparedStmt.setNull(6, java.sql.Types.VARCHAR);
                    }

                    preparedStmt.setString(7, tempFixture.getString("code"));
                    preparedStmt.setString(8, tempFixture.getString("type"));

                    preparedStmt.setString(9, tempFixture.get("icon").toString());

                    if (!tempFixture.get("temp").toString().equals("null")) {
                        preparedStmt.setInt(10, tempFixture.getInt("temp"));
                    } else {
                        preparedStmt.setNull(10, java.sql.Types.VARCHAR);
                    }

                    preparedStmt.setString(11, tempFixture.getString("status"));
                    preparedStmt.setTime(12, Time.valueOf(tempFixture.getString("time")));
                    preparedStmt.setDate(13, Date.valueOf(tempFixture.getString("date")));
                    preparedStmt.setString(14, tempFixture.getString("timezone"));

                    preparedStmt.setInt(15, tempFixture.getInt("minute"));
                    preparedStmt.setInt(16, tempFixture.getInt("second"));
                    preparedStmt.setInt(17, tempFixture.getInt("added_time"));
                    preparedStmt.setInt(18, tempFixture.getInt("extra_minute"));
                    preparedStmt.setInt(19, tempFixture.getInt("injury_time"));
                    // execute the preparedstatement
                    preparedStmt.execute();

                } catch (SQLException ex) {

                }

                fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("bench"));
                fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("lineup"));
                fixturesEvents.addFixturesEvents(tempFixture.getJSONArray("events"));
                fixturesCorners.addFixturesCorners(tempFixture.getJSONArray("corners"));
                JSONObject localTeam = createFixtureTeam(tempFixture, true);
                JSONObject visitorTeam = createFixtureTeam(tempFixture, false);
                fixturesTeams.addFixturesTeams(localTeam);
                fixturesTeams.addFixturesTeams(visitorTeam);
                // }
            }

            if (maxPage <= i) {
                lastPage = true;
            } else {
                i++;
            }

        }

        System.out.println("Finished now: " + LocalDateTime.now());

    }
    
    //The following method is a requirement for the group project and would not be used in a general context.
    //Proper implementation to acquire past fixture data would require timed requests for the different leagues during periods of down time
    //This method is only concerned with the gathering of a single leagues past games since the project focuses on the premier league
    public void getPastLeagueFixtures(String fixturesEndpoint) throws IOException, SQLException {


            

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;

            while (!lastPage) {

                JSONObject fixtures;

                try {
                    fixtures = Endpoint.getDataFromEndpoint(fixturesEndpoint + i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray fixturesArray = fixtures.getJSONArray("data");
                JSONObject metaData = fixtures.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : fixturesArray) {
                    JSONObject tempObject = (JSONObject) obj;
                    JSONObject tempFixture = sanitiseFixture(tempObject);
                   
                    if(!tempFixture.get("venue_id").toString().equals("null") && !venues.checkVenueExists(tempFixture.getInt("venue_id")))
                        venues.addVenue(tempFixture.getInt("venue_id"));
                    
                    

                    try {
                        // the mysql insert statement
                        String query = " insert into fixtures(id, league_id, season_id, stage_id, round_id, venue_id, weather_code, weather_type, weather_report_image, temperature, fixture_status, starting_time, starting_date, timezone, time_minute, time_second, added_time, extra_time, injury_time)"
                                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                + " id=VALUES(id), league_id=VALUES(league_id), season_id=VALUES(season_id), stage_id=VALUES(stage_id), round_id=VALUES(round_id), venue_id=VALUES(venue_id),"
                                + " weather_code=VALUES(weather_code), weather_type=VALUES(weather_type), weather_report_image=VALUES(weather_report_image), temperature=VALUES(temperature),"
                                + " fixture_status=VALUES(fixture_status), starting_time=VALUES(starting_time), starting_date=VALUES(starting_date), timezone=VALUES(timezone),"
                                + " time_minute=VALUES(time_minute), time_second=VALUES(time_second), added_time=VALUES(added_time), extra_time=VALUES(extra_time), injury_time=VALUES(injury_time)";

                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = db.prepareStatement(query);
                        preparedStmt.setInt(1, tempFixture.getInt("id"));
                        preparedStmt.setInt(2, tempFixture.getInt("league_id"));
                        preparedStmt.setInt(3, tempFixture.getInt("season_id"));

                        if (!tempFixture.get("stage_id").toString().equals("null")) {
                            preparedStmt.setInt(4, tempFixture.getInt("stage_id"));
                        } else {
                            preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                        }

                        if (!tempFixture.get("round_id").toString().equals("null")) {
                            preparedStmt.setInt(5, tempFixture.getInt("round_id"));
                        } else {
                            preparedStmt.setNull(5, java.sql.Types.VARCHAR);
                        }

                        if (!tempFixture.get("venue_id").toString().equals("null")) {
                            preparedStmt.setInt(6, tempFixture.getInt("venue_id"));
                        } else {
                            preparedStmt.setNull(6, java.sql.Types.VARCHAR);
                        }

                        preparedStmt.setString(7, tempFixture.getString("code"));
                        preparedStmt.setString(8, tempFixture.getString("type"));

                        preparedStmt.setString(9, tempFixture.get("icon").toString());

                        if (!tempFixture.get("temp").toString().equals("null")) {
                            preparedStmt.setInt(10, tempFixture.getInt("temp"));
                        } else {
                            preparedStmt.setNull(10, java.sql.Types.VARCHAR);
                        }

                        preparedStmt.setString(11, tempFixture.getString("status"));
                        preparedStmt.setTime(12, Time.valueOf(tempFixture.getString("time")));
                        preparedStmt.setDate(13, Date.valueOf(tempFixture.getString("date")));
                        preparedStmt.setString(14, tempFixture.getString("timezone"));

                        preparedStmt.setInt(15, tempFixture.getInt("minute"));
                        preparedStmt.setInt(16, tempFixture.getInt("second"));
                        preparedStmt.setInt(17, tempFixture.getInt("added_time"));
                        preparedStmt.setInt(18, tempFixture.getInt("extra_minute"));
                        preparedStmt.setInt(19, tempFixture.getInt("injury_time"));
                        System.out.println(preparedStmt + "\n");
                        // execute the preparedstatement
                        preparedStmt.execute();

                    } catch (SQLException ex) {

                    }

                    fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("bench"));
                    fixturesPlayers.addFixturesPlayers(tempFixture.getJSONArray("lineup"));
                    fixturesEvents.addFixturesEvents(tempFixture.getJSONArray("events"));
                    fixturesCorners.addFixturesCorners(tempFixture.getJSONArray("corners"));
                    JSONObject localTeam = createFixtureTeam(tempFixture, true);
                    JSONObject visitorTeam = createFixtureTeam(tempFixture, false);

                    fixturesTeams.addFixturesTeams(localTeam);
                    fixturesTeams.addFixturesTeams(visitorTeam);

                }
                if (maxPage <= i) {
                    lastPage = true;
                } else {
                    i++;
                }
                
                

            }

        

    }

    private JSONObject createFixtureTeam(JSONObject fixture, boolean localTeam) {

        JSONObject returnFixture = new JSONObject();
        JSONObject scores = fixture.getJSONObject("scores");
        JSONObject formations = fixture.getJSONObject("formations");
        JSONArray statsArray = fixture.getJSONArray("stats");

        String team;

        if (localTeam) {
            team = "localteam";
        } else {
            team = "visitorteam";
        }

        returnFixture.put("fixture_id", fixture.getInt("id"));
        returnFixture.put("team_id", fixture.getInt(team + "_id"));
        returnFixture.put("score", scores.getInt(team + "_score"));
        returnFixture.put("color", fixture.getString(team + "_color"));

        if (!fixture.get("winner_team_id").toString().equals("null")) {

            if (fixture.getInt("winner_team_id") == fixture.getInt(team + "_id")) {
                returnFixture.put("winning_team", true);
            } else {
                returnFixture.put("winning_team", false);
            }
        } else {
            returnFixture.put("winning_team", false);
        }

        if (!formations.get(team + "_formation").toString().equals("null")) {
            returnFixture.put("formation", formations.getString(team + "_formation"));
        } else {
            returnFixture.put("formation", "null");
        }

        if (!scores.get(team + "_pen_score").toString().equals("null")) {
            returnFixture.put("pen_score", scores.getInt(team + "_pen_score"));
        } else {
            returnFixture.put("pen_score", "null");
        }

        if (!statsArray.isEmpty()) {
            if (localTeam) {
                returnFixture.put("stats", statsArray.get(0));
            } else {
                returnFixture.put("stats", statsArray.get(1));
            }
        }

        return returnFixture;

    }

    //remove this?
    private JSONObject sanitiseFixture(JSONObject fixture) {

        //Objects for data associated with livescores and fixtures api endpoints
        JSONObject sanitisedFixture = new JSONObject();
        JSONObject weatherObject = new JSONObject();
        JSONObject temperatureObject = new JSONObject();
        JSONObject timeObject = fixture.getJSONObject("time");
        JSONObject fixtureStartingObject = timeObject.getJSONObject("starting_at");
        JSONObject fixtureFormations = fixture.getJSONObject("formations");

        //Data for id values related to the fixture
        sanitisedFixture.put("id", fixture.getInt("id"));
        sanitisedFixture.put("league_id", fixture.getInt("league_id"));
        sanitisedFixture.put("season_id", fixture.getInt("season_id"));

        if (!fixture.get("stage_id").toString().equals("null")) {
            sanitisedFixture.put("stage_id", fixture.getInt("stage_id"));
        } else {
            sanitisedFixture.put("stage_id", "null");
        }

        if (!fixture.get("round_id").toString().equals("null")) {
            sanitisedFixture.put("round_id", fixture.getInt("round_id"));
        } else {
            sanitisedFixture.put("round_id", "null");
        }

        if (!fixture.get("venue_id").toString().equals("null")) {
            sanitisedFixture.put("venue_id", fixture.getInt("venue_id"));
        } else {
            sanitisedFixture.put("venue_id", "null");
        }

        //Data for temperature and weather
        if (!fixture.get("weather_report").toString().equals("null")) {
            weatherObject = fixture.getJSONObject("weather_report");
            temperatureObject = weatherObject.getJSONObject("temperature_celcius");
        }

        if (!fixture.get("weather_report").toString().equals("null")) {
            sanitisedFixture.put("code", weatherObject.getString("code"));
            sanitisedFixture.put("type", weatherObject.getString("type"));
            sanitisedFixture.put("icon", weatherObject.get("icon").toString());
            sanitisedFixture.put("temp", temperatureObject.getDouble("temp"));
        } else {
            sanitisedFixture.put("code", "null");
            sanitisedFixture.put("type", "null");
            sanitisedFixture.put("icon", "null");
            sanitisedFixture.put("temp", "null");
        }

        //Data related to time for the game, during, before, after
        sanitisedFixture.put("status", timeObject.getString("status"));
        sanitisedFixture.put("time", fixtureStartingObject.getString("time"));
        sanitisedFixture.put("date", fixtureStartingObject.getString("date"));
        sanitisedFixture.put("timezone", fixtureStartingObject.getString("timezone"));

        if (!timeObject.get("minute").toString().equals("null")) {
            sanitisedFixture.put("minute", timeObject.getInt("minute"));
        } else {
            sanitisedFixture.put("minute", 0);
        }

        if (!timeObject.get("second").toString().equals("null")) {
            sanitisedFixture.put("second", timeObject.getInt("second"));
        } else {
            sanitisedFixture.put("second", 0);
        }

        if (!timeObject.get("added_time").toString().equals("null")) {
            sanitisedFixture.put("added_time", timeObject.getInt("added_time"));
        } else {
            sanitisedFixture.put("added_time", 0);
        }

        if (!timeObject.get("extra_minute").toString().equals("null")) {
            sanitisedFixture.put("extra_minute", timeObject.getInt("extra_minute"));
        } else {
            sanitisedFixture.put("extra_minute", 0);
        }

        if (!timeObject.get("injury_time").toString().equals("null")) {
            sanitisedFixture.put("injury_time", timeObject.getInt("injury_time"));
        } else {
            sanitisedFixture.put("injury_time", 0);
        }

        //Data to indentify the teams
        sanitisedFixture.put("localteam_id", fixture.getInt("localteam_id"));
        sanitisedFixture.put("visitorteam_id", fixture.getInt("visitorteam_id"));
        sanitisedFixture.put("formations", fixtureFormations);

        if (!fixture.get("colors").toString().equals("null")) {
            sanitisedFixture.put("localteam_color", fixture.getJSONObject("colors").getJSONObject("localteam").get("color").toString());
            sanitisedFixture.put("visitorteam_color", fixture.getJSONObject("colors").getJSONObject("visitorteam").get("color").toString());
        } else {
            sanitisedFixture.put("localteam_color", "null");
            sanitisedFixture.put("visitorteam_color", "null");
        }

        if (!fixture.get("winner_team_id").toString().equals("null")) {
            sanitisedFixture.put("winner_team_id", fixture.getInt("winner_team_id"));
        } else {
            sanitisedFixture.put("winner_team_id", "null");
        }
        sanitisedFixture.put("events", fixture.getJSONObject("events").getJSONArray("data"));
        sanitisedFixture.put("bench", fixture.getJSONObject("bench").getJSONArray("data"));
        sanitisedFixture.put("lineup", fixture.getJSONObject("lineup").getJSONArray("data"));
        sanitisedFixture.put("corners", fixture.getJSONObject("corners").getJSONArray("data"));
        sanitisedFixture.put("stats", fixture.getJSONObject("stats").getJSONArray("data"));
        sanitisedFixture.put("scores", fixture.getJSONObject("scores"));

        return sanitisedFixture;
    }
}
