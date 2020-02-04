package Database;

import SportMonks.Endpoint;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Players {

    private Connection db;
    private TeamsSeasons teamsSeasons;
    private TeamsPlayers teamsPlayers;

    public Players() {

    }

    public Players(Connection db) {
        this.db = db;
        this.teamsSeasons = new TeamsSeasons(db);
        this.teamsPlayers = new TeamsPlayers(db);

    }

    public void managePlayers(String playersEndpoint) throws SQLException, IOException {
        // HI HI
        JSONObject teamsSeasonsIds = teamsSeasons.getTeamsSeasonsIds();
        JSONArray teamsSeasonsArray = teamsSeasonsIds.getJSONArray("data");

        for (Object TSObject : teamsSeasonsArray) {
            JSONObject tempTeam = (JSONObject) TSObject;

            int teamId = tempTeam.getInt("team_id");
            int seasonId = tempTeam.getInt("season_id");

            String tempPlayersEndpoint = Endpoint.makeNewEndpoint(playersEndpoint, seasonId, "season/");
            String newPlayersEndpoint = Endpoint.makeNewEndpoint(tempPlayersEndpoint, teamId, "team/");

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;

            while (!lastPage) {

                JSONObject players;

                try {
                    players = Endpoint.getDataFromEndpoint(newPlayersEndpoint + i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray teamsPlayersArray = new JSONArray();
                JSONArray teamsArray = players.getJSONArray("data");
                JSONObject metaData = players.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : teamsArray) {
                    JSONObject tempObject = (JSONObject) obj;

                    if (tempObject.has("player")) {

                        JSONObject player = tempObject.getJSONObject("player").getJSONObject("data");

                        try {
                            // the mysql insert statement
                            String query = " insert into players (id, country_id, firstname, lastname, common_name, display_name, nationality, date_of_birth, image, height, weight)"
                                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?) ON DUPLICATE KEY UPDATE"
                                    + " country_id=VALUES(country_id), firstname=VALUES(firstname), lastname=VALUES(lastname),"
                                    + " common_name=VALUES(common_name), display_name=VALUES(display_name),"
                                    + " nationality=VALUES(nationality), date_of_birth=VALUES(date_of_birth), image=VALUES(image), height=VALUES(height), weight=VALUES(weight)";
                            
                            
                            String[] commonName;
                            String[] displayName;
                            String name;
                            if (!player.get("display_name").toString().equals("null") && !player.get("common_name").toString().equals("null")){
                                commonName = player.getString("common_name").split(" ");
                                displayName = player.getString("display_name").split(" ");
                                displayName[0] = commonName[0];
                                name = String.join(" ", displayName);
                            }else
                                name = "N/A";
                            
                            
                            

                            // create the mysql insert preparedstatement
                            PreparedStatement preparedStmt = db.prepareStatement(query);
                            preparedStmt.setInt(1, player.getInt("player_id"));

                            if (!player.get("country_id").toString().equals("null")) {
                                preparedStmt.setInt(2, player.getInt("country_id"));
                            } else {
                                preparedStmt.setNull(2, java.sql.Types.VARCHAR);
                            }

                            if (!player.get("firstname").toString().equals("null")) {
                                preparedStmt.setString(3, player.getString("firstname"));
                            } else {
                                preparedStmt.setString(3, "N/A");
                            }

                            if (!player.get("lastname").toString().equals("null")) {
                                preparedStmt.setString(4, player.getString("lastname"));
                            } else {
                                preparedStmt.setString(4, "N/A");
                            }

                            if (!player.get("common_name").toString().equals("null")) {
                                preparedStmt.setString(5, player.getString("common_name"));
                            } else {
                                preparedStmt.setString(5, "N/A");
                            }
                            
                            preparedStmt.setString(6, name);

                            if (!player.get("nationality").toString().equals("null")) {
                                preparedStmt.setString(7, player.getString("nationality"));
                            } else {
                                preparedStmt.setNull(7, java.sql.Types.VARCHAR);
                            }

                            if (!player.get("birthdate").toString().equals("null")) {
                                String[] dateArray = player.getString("birthdate").split("/");
                                String date = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];

                                System.out.println(date);
                                preparedStmt.setDate(8, Date.valueOf(date));
                            } else {
                                preparedStmt.setNull(8, java.sql.Types.VARCHAR);
                            }

                            if (!player.get("image_path").toString().equals("null")) {
                                preparedStmt.setString(9, player.get("image_path").toString());
                            } else {
                                preparedStmt.setNull(9, java.sql.Types.VARCHAR);
                            }

                            if (!player.get("height").toString().equals("null")) {
                                preparedStmt.setString(10, player.getString("height"));
                            } else {
                                preparedStmt.setNull(10, java.sql.Types.VARCHAR);
                            }

                            if (!player.get("weight").toString().equals("null")) {
                                preparedStmt.setString(11, player.getString("weight"));
                            } else {
                                preparedStmt.setNull(11, java.sql.Types.VARCHAR);
                            }

                            // execute the preparedstatement
                            preparedStmt.execute();
                        } catch (SQLException ex) {

                        }

                        JSONObject teamPlayer = new JSONObject();
                        teamPlayer.put("player_id", player.getInt("player_id"));
                        teamPlayer.put("team_id", teamId);
                        teamPlayer.put("season_id", seasonId);

                        if (!player.get("position_id").toString().equals("null")) {
                            teamPlayer.put("position_id", player.getInt("position_id"));
                        } else {
                            teamPlayer.put("position_id", "null");
                        }

                        teamsPlayersArray.put(teamPlayer);

                    }

                }
                teamsPlayers.addTeamsPlayers(teamsPlayersArray);

                if (maxPage <= i) {
                    lastPage = true;
                } else {
                    i++;
                }

            }
        }

    }

}
