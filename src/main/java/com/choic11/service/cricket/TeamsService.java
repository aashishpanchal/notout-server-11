package com.choic11.service.cricket;

import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.repository.cricket.TeamsRepository;
import com.choic11.service.CustomerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamsService {

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    MatchesService matchesService;

    public TeamsRepository getTeamsRepository() {
        return teamsRepository;
    }

    public BaseResponse getAlreadyCreatedTeamCount(Integer UserId, int matchUniqueId) {

        int alreadyCreatedTeamCount = teamsRepository.getCustomerAlreadyCreatedTeamCount(UserId, matchUniqueId);
        return new BaseResponse(0, false, "Match Team Count.", alreadyCreatedTeamCount);
    }

    public BaseResponse createCustomerTeam(BaseRequest baseRequest) {
        List<JSONObject> playersDataList = new ArrayList<JSONObject>();
        try {
            JSONArray jsonArrayPlayers = new JSONArray(baseRequest.getParam("player_json").toString());
            if (jsonArrayPlayers.isEmpty()) {
                return new BaseResponse(0, true, "Team can't create without players.", null);
            } else if (jsonArrayPlayers.length() != 11) {
                return new BaseResponse(0, true, "Invalid players data.", null);
            } else {
                HashSet<String> playerIds = new HashSet<String>();
                HashSet<String> playerPositions = new HashSet<String>();
                int captionCount = 0;
                int vccaptionCount = 0;
                int playerCount = 0;

                for (Object jsonObj : jsonArrayPlayers) {
                    JSONObject jsonObject = (JSONObject) jsonObj;
                    if (playerIds.contains(jsonObject.get("player_id"))) {
                        return new BaseResponse(0, true, "Invalid players data.", null);
                    }

                    if (playerPositions.contains(jsonObject.get("player_pos"))) {
                        return new BaseResponse(0, true, "Invalid players data.", null);
                    }

                    float multiplier = Float.parseFloat(jsonObject.get("player_multiplier").toString());

                    if (multiplier == 2f) {
                        captionCount++;
                    } else if (multiplier == 1.5f) {
                        vccaptionCount++;
                    } else if (multiplier == 1f) {
                        playerCount++;
                    }


                    playerIds.add(jsonObject.get("player_id").toString());
                    playerPositions.add(jsonObject.get("player_pos").toString());

                    playersDataList.add(jsonObject);

                }

                if (captionCount != 1 && vccaptionCount != 1 && playerCount != 9) {
                    return new BaseResponse(0, true, "Invalid players data.", null);
                }
            }

        } catch (JSONException jsonException) {
            return new BaseResponse(0, true, "Player json parsing exception", null);
        }

        Collections.sort(playersDataList, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "player_id";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                int valA = 0;
                int valB = 0;

                try {
                    valA = Integer.parseInt(a.get(KEY_NAME).toString());
                    valB = Integer.parseInt(b.get(KEY_NAME).toString());
                } catch (JSONException e) {
                }
                if (valB < valA) {
                    return -1;
                } else if (valB > valA) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        String customerTeamName = baseRequest.getParam("customer_team_name");
        int teamName = Integer.parseInt(baseRequest.getParam("team_name"));
        int fromAdmin = Integer.parseInt(baseRequest.getParam("fromadmin"));
        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));

        Object createCustomerTeam = teamsRepository.createCustomerTeam(baseRequest.authUserId, match_unique_id,
                playersDataList, customerTeamName, teamName, fromAdmin);

        teamsRepository.updateCustomerMatchTeamInfo(match_unique_id,baseRequest.authUserId);

        if (createCustomerTeam instanceof String) {
            if (createCustomerTeam.toString().equals("UNABLE_TO_PROCEED")) {
                return new BaseResponse(0, true, "Invalid match.", null);
            } else if (createCustomerTeam.toString().equals("NO_MATCH_FOUND")) {
                return new BaseResponse(0, true, "Invalid match.", null);
            } else if (createCustomerTeam.toString().equals("INVALID_MATCH")) {
                return new BaseResponse(0, true,
                        "The deadline has passed! Check out the contests you've joined for this match.", null);
            } else if (createCustomerTeam.toString().equals("TEAM_CREATION_LIMIT_EXEED")) {
                return new BaseResponse(0, true, "Team creation limit exeed.", null);
            } else if (createCustomerTeam.toString().equals("TEAM_ALREADY_EXIST")) {
                return new BaseResponse(0, true, "Same Team Already Exist.", null);
            } else if (createCustomerTeam.toString().equals("CUSTOMER_TEAM_NAME_ALREADY_EXIST")) {
                return new BaseResponse(0, true, "Customer Team name Already Exist.", null);
            } else if (createCustomerTeam.toString().equals("TEAM_NAME_ALREADY_EXIST")) {
                return new BaseResponse(0, true, "Team name Already Exist.", null);
            }
        }

        return new BaseResponse(0, false, "Team created successfully.", createCustomerTeam);
    }

    public BaseResponse updateCustomerTeam(BaseRequest baseRequest) {
        List<JSONObject> playersDataList = new ArrayList<JSONObject>();
        try {
            JSONArray jsonArrayPlayers = new JSONArray(baseRequest.getParam("player_json").toString());
            if (jsonArrayPlayers.isEmpty()) {
                return new BaseResponse(0, true, "Team can't create without players.", null);
            } else if (jsonArrayPlayers.length() != 11) {
                return new BaseResponse(0, true, "Invalid players data.", null);
            } else {
                HashSet<String> playerIds = new HashSet<String>();
                HashSet<String> playerPositions = new HashSet<String>();
                int captionCount = 0;
                int vccaptionCount = 0;
                int playerCount = 0;

                for (Object jsonObj : jsonArrayPlayers) {
                    JSONObject jsonObject = (JSONObject) jsonObj;
                    if (playerIds.contains(jsonObject.get("player_id"))) {
                        return new BaseResponse(0, true, "Invalid players data.", null);
                    }

                    if (playerPositions.contains(jsonObject.get("player_pos"))) {
                        return new BaseResponse(0, true, "Invalid players data.", null);
                    }

                    float multiplier = Float.parseFloat(jsonObject.get("player_multiplier").toString());

                    if (multiplier == 2f) {
                        captionCount++;
                    } else if (multiplier == 1.5f) {
                        vccaptionCount++;
                    } else if (multiplier == 1f) {
                        playerCount++;
                    }

                    playerIds.add(jsonObject.get("player_id").toString());
                    playerPositions.add(jsonObject.get("player_pos").toString());

                    playersDataList.add(jsonObject);

                }

                if (captionCount != 1 && vccaptionCount != 1 && playerCount != 9) {
                    return new BaseResponse(0, true, "Invalid players data.", null);
                }

            }

        } catch (JSONException jsonException) {
            return new BaseResponse(0, true, "Player json parsing exception", null);
        }

        Collections.sort(playersDataList, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "player_id";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                int valA = 0;
                int valB = 0;

                try {
                    valA = Integer.parseInt(a.get(KEY_NAME).toString());
                    valB = Integer.parseInt(b.get(KEY_NAME).toString());
                } catch (JSONException e) {
                }
                if (valB < valA) {
                    return -1;
                } else if (valB > valA) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));
        String isUpdateAdmin="0";
        if(baseRequest.hasParam("isUpdateAdmin")){
            isUpdateAdmin=baseRequest.getParam("isUpdateAdmin");
        }

        Object createCustomerTeam = teamsRepository.updateCustomerTeam(baseRequest.authUserId, match_unique_id,
                playersDataList, Integer.parseInt(baseRequest.getParam("customer_team_id").toString()), isUpdateAdmin);

        if (createCustomerTeam instanceof String) {
            if (createCustomerTeam.toString().equals("UNABLE_TO_PROCEED")) {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            } else if (createCustomerTeam.toString().equals("NO_MATCH_FOUND")) {
                return new BaseResponse(0, true, "Invalid match.", null);
            } else if (createCustomerTeam.toString().equals("INVALID_MATCH")) {
                return new BaseResponse(0, true,
                        "The deadline has passed! Check out the contests you've joined for this match.", null);
            } else if (createCustomerTeam.toString().equals("TEAM_CREATION_LIMIT_EXEED")) {
                return new BaseResponse(0, true, "Team creation limit exeed.", null);
            } else if (createCustomerTeam.toString().equals("TEAM_ALREADY_EXIST")) {
                return new BaseResponse(0, true, "Same Team Already Exist.", null);
            } else if (createCustomerTeam.toString().equals("NO_RECORD")) {
                return new BaseResponse(0, true, "No Team Found.", null);
            }
        }

        return new BaseResponse(0, false, "Team updated successfully.", createCustomerTeam);
    }

    public BaseResponse getCustomerMatchTeam(int UserId, int matchUniqueId) {

        Object data = teamsRepository.getCustomerMatchTeam(UserId, matchUniqueId, matchesService);
        if (data instanceof String) {
            String err = (String) data;
            if (err.equals("UNABLE_TO_PROCEED")) {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            } else {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            }

        } else {
            return new BaseResponse(0, false, "Team List", data);
        }

    }

    public BaseResponse getCustomerMatchTeamDetail(int UserId, int customerTeamId) {

        Object data = teamsRepository.getCustomerMatchTeamDetail(UserId, customerTeamId, matchesService);
        if (data instanceof String) {
            String err = (String) data;
            if (err.equals("UNABLE_TO_PROCEED")) {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            } else {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            }

        } else {
            return new BaseResponse(0, false, "Team Detail", data);
        }

    }

    public BaseResponse getMatchDreamTeamDetail(BaseRequest baseRequest, int matchUniqueid) {

        Object data = teamsRepository.getMatchDreamTeamDetail(matchUniqueid, matchesService);
        if (data instanceof String) {
            String err = (String) data;
            if (err.equals("UNABLE_TO_PROCEED")) {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            } else if (err.equals("NO_RECORD")) {
                return new BaseResponse(0, true, "No Teams Found.", null);
            } else {
                return new BaseResponse(0, true, "Unable to proceed.", null);
            }

        } else {
            return new BaseResponse(0, false, "Team Detail", data);
        }

    }

    public BaseResponse getCustomerMatchTeamStats(BaseRequest baseRequest, int customerTeamId) {
        HashMap<String, Object> customerMatchTeamStats = teamsRepository
                .getCustomerMatchTeamStats(baseRequest.authUserId, customerTeamId);
        if (customerMatchTeamStats.isEmpty()) {
            return new BaseResponse(0, true, "No Teams Found.", null);
        } else {
            return new BaseResponse(0, false, "Team Stats Detail.", customerMatchTeamStats);
        }

    }

    public BaseResponse getMatchDreamTeamStats(BaseRequest baseRequest, String matchUniqueid) {
        HashMap<String, Object> customerMatchTeamStats = teamsRepository.getMatchDreamTeamStats(baseRequest.authUserId,
                Integer.parseInt(matchUniqueid));
        if (customerMatchTeamStats.isEmpty()) {
            return new BaseResponse(0, true, "No Teams Found.", null);
        } else {
            return new BaseResponse(0, false, "Team Stats Detail.", customerMatchTeamStats);
        }
    }
}
