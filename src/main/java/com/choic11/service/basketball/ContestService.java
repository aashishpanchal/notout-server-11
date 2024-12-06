package com.choic11.service.basketball;

import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.model.BaseRequest;
import com.choic11.model.TblTemplate;
import com.choic11.model.basketball.TblBasketballContestMatch;
import com.choic11.model.basketball.TblBasketballCustomerContest;
import com.choic11.model.basketball.TblBasketballMatch;
import com.choic11.model.response.*;
import com.choic11.repository.basketball.ContestRepository;
import com.choic11.service.CustomerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("BasketballContestService")
public class ContestService {

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    MatchesService matchesService;

    @Autowired
    TeamsService teamsService;

    public ContestRepository getContestRepository() {
        return contestRepository;
    }

    public BaseResponse getMatchContest(BaseRequest baseRequest, int matchId, int matchUniqueId) {

        List<HashMap<String, Object>> matchContest = contestRepository.getMatchContest(matchId, matchUniqueId,
                baseRequest.authUserId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> outputPractice = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            JSONObject jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;
            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            perContests.put("total_team_left", (int) hashMap.get("total_team") - (int) hashMap.get("total_joined_team"));
            perContests.put("slug", hashMap.get("slug"));
            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));
            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {
                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }

            perContests.put("is_beat_the_expert", "N");

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));

            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;
            if (output.containsKey((Integer) hashMap.get("cat_id"))) {

                LinkedHashMap<String, Object> linkedHashMap = output.get((Integer) hashMap.get("cat_id"));
                List<LinkedHashMap<String, Object>> contests = (List<LinkedHashMap<String, Object>>) linkedHashMap
                        .get("contests");
                contests.add(perContests);
            } else {
                List<LinkedHashMap<String, Object>> contests = new ArrayList<LinkedHashMap<String, Object>>();
                contests.add(perContests);

                LinkedHashMap<String, Object> contestCategory = new LinkedHashMap<String, Object>();
                contestCategory.put("id", hashMap.get("cat_id"));
                contestCategory.put("name", hashMap.get("name"));
                contestCategory.put("cash_bonus_used_type", hashMap.get("cat_cash_bonus_used_type"));
                contestCategory.put("cash_bonus_used_value", hashMap.get("cat_cash_bonus_used_value"));
                contestCategory.put("description", hashMap.get("description"));
                contestCategory.put("is_discounted", hashMap.get("is_discounted"));
                contestCategory.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CONTEXTCATEGORY_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

                if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                    contestCategory.put("discount_image", "");
                    contestCategory.put("discount_image_width", "0");
                    contestCategory.put("discount_image_height", "0");
                } else {
                    contestCategory.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                    contestCategory.put("discount_image_width", hashMap.get("discount_image_width"));
                    contestCategory.put("discount_image_height", hashMap.get("discount_image_height"));
                }
                contestCategory.put("contests", contests);
                output.put((Integer) hashMap.get("cat_id"), contestCategory);

            }


        }

        long matchCustomerTeamCountByMatchUniqueId = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        long matchCustomerContestCountByMatchUniqueId = contestRepository
                .getMatchCustomerContestCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        TblBasketballMatch matchMiniDataOnly = matchesService.getMatchesRepository().getMatchMiniDataOnly(matchUniqueId);
        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(matchMiniDataOnly);

        HashMap<String, Object> detail = new HashMap<String, Object>();
        detail.put("total_teams", matchCustomerTeamCountByMatchUniqueId);
        detail.put("total_joined_contest", matchCustomerContestCountByMatchUniqueId);

        return new ContestBaseResponse(0, false, "Contest Listing.", output.values(), outputPractice.values(), detail, null, modifiedMatchData, totalFavorite);

    }

    public BaseResponse getCustomerMatchContest(BaseRequest baseRequest, int matchId, int matchUniqueId) {

        List<HashMap<String, Object>> matchContest = contestRepository.getCustomerMatchContest(matchId, matchUniqueId,
                baseRequest.authUserId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            JSONObject jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;
            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            if (hashMap.containsKey("contestTeams")) {
                LinkedHashMap<String, Object> contestTeams = (LinkedHashMap<String, Object>) hashMap
                        .get("contestTeams");

                perContests.put("myteams", contestTeams.values());
            }

            perContests.put("total_team_left",
                    (int) hashMap.get("total_team") - (int) hashMap.get("total_joined_team"));
            perContests.put("slug", hashMap.get("slug"));
            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));
            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {

                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }

            perContests.put("is_beat_the_expert", "N");

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));

            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;

            if (output.containsKey((Integer) hashMap.get("cat_id"))) {

                LinkedHashMap<String, Object> linkedHashMap = output.get((Integer) hashMap.get("cat_id"));
                List<LinkedHashMap<String, Object>> contests = (List<LinkedHashMap<String, Object>>) linkedHashMap
                        .get("contests");
                contests.add(perContests);

            } else {
                List<LinkedHashMap<String, Object>> contests = new ArrayList<LinkedHashMap<String, Object>>();
                contests.add(perContests);

                LinkedHashMap<String, Object> contestCategory = new LinkedHashMap<String, Object>();
                contestCategory.put("id", hashMap.get("cat_id"));
                contestCategory.put("name", hashMap.get("name"));
                contestCategory.put("cash_bonus_used_type", hashMap.get("cat_cash_bonus_used_type"));
                contestCategory.put("cash_bonus_used_value", hashMap.get("cat_cash_bonus_used_value"));
                contestCategory.put("description", hashMap.get("description"));
                contestCategory.put("is_discounted", hashMap.get("is_discounted"));
                contestCategory.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CONTEXTCATEGORY_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

                if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                    contestCategory.put("discount_image", "");
                    contestCategory.put("discount_image_width", "0");
                    contestCategory.put("discount_image_height", "0");
                } else {

                    contestCategory.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                    contestCategory.put("discount_image_width", hashMap.get("discount_image_width"));
                    contestCategory.put("discount_image_height", hashMap.get("discount_image_height"));
                }
                contestCategory.put("contests", contests);
                output.put((Integer) hashMap.get("cat_id"), contestCategory);

            }
        }

        long matchCustomerTeamCountByMatchUniqueId = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        long matchCustomerContestCountByMatchUniqueId = contestRepository
                .getMatchCustomerContestCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        TblBasketballMatch matchData = matchesService.getCustomerMatchData(matchUniqueId, baseRequest.authUserId);
        TblBasketballMatch matchMiniDataOnly = matchesService.getMatchesRepository().getMatchMiniDataOnly(matchUniqueId);
        matchMiniDataOnly.setContestCount(matchData.getContestCount());
        matchMiniDataOnly.setTeamCount(matchData.getTeamCount());
        matchMiniDataOnly.setTotalInvestment(matchData.getTotalInvestment());
        matchMiniDataOnly.setTotalWinning(matchData.getTotalWinning());


        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(matchMiniDataOnly);


        HashMap<String, Object> detail = new HashMap<String, Object>();
        detail.put("total_teams", matchCustomerTeamCountByMatchUniqueId);
        detail.put("total_joined_contest", matchCustomerContestCountByMatchUniqueId);

        return new ContestBaseResponse(0, false, "Contest Listing.", output.values(), null, detail, null, modifiedMatchData, totalFavorite);

    }

    public BaseResponse getMatchCategoryContest(BaseRequest baseRequest, int matchId, int matchUniqueId, int catId) {
        List<HashMap<String, Object>> matchContest = contestRepository.getMatchCategoryContest(matchId, matchUniqueId,
                baseRequest.authUserId, catId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> outputPractice = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        int i = 0;
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            if (((int) hashMap.get("total_team") - (int) hashMap.get("total_team_current_join")) <= 0) {

                continue;
            }

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            JSONObject jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;

            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            perContests.put("total_team_left",
                    (int) hashMap.get("total_team") - (int) hashMap.get("total_team_current_join"));
            perContests.put("slug", hashMap.get("slug"));
            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));
            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {
                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }
            perContests.put("is_beat_the_expert", "N");

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));
            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;
            output.put(i, perContests);
            i++;

        }

        LinkedHashMap<String, Object> returnOoutput = new LinkedHashMap<String, Object>();
        returnOoutput.put("cash", output.values());
        returnOoutput.put("practice", outputPractice.values());

        long matchCustomerTeamCountByMatchUniqueId = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        long matchCustomerContestCountByMatchUniqueId = contestRepository
                .getMatchCustomerContestCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        HashMap<String, Object> detail = new HashMap<String, Object>();
        detail.put("total_teams", matchCustomerTeamCountByMatchUniqueId);
        detail.put("total_joined_contest", matchCustomerContestCountByMatchUniqueId);

        return new ContestBaseResponse(0, false, "Contest Listing.", output.values(), outputPractice.values(), detail, null, null, totalFavorite);
    }

    public BaseResponse getContestWinnerBreakup(int contestId) {
        HashMap<String, Object> contestWinnerBreakup = contestRepository.getContestWinnerBreakup(contestId);
        if (contestWinnerBreakup == null) {
            return new BaseResponse(1, true, "No Contest Found." + contestId, null);
        }
        HashMap<String, Object> totalTaxPercent = customerService.getTotalTaxPercent();

        String WINNING_BREAKUP_MESSAGE = "Note: The actual prize money may be different than the prize money mentioned above if there is a tie for any of the winning positions. Check FAQs for further details. As per government regulations, a tax of " + totalTaxPercent.get("total_tax") + "% will be deducted if an individual wins more than Rs. 10,000";

        JSONObject jsonObject1 = new JSONObject(contestWinnerBreakup.get("contest_json").toString());
        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("winner_breakup", jsonObject1.toMap());
        output.put("winner_breakup_message", WINNING_BREAKUP_MESSAGE);
        output.put("gadget_disclaimer", contestWinnerBreakup.get("gadget_disclaimer"));
        return new BaseResponse(0, false, "Winner Breakup Listing.", output);
    }

    public BaseResponse getMatchContestSDetail(int matchContestId, BaseRequest baseRequest, int matchUniqueId) {

        List<HashMap<String, Object>> matchContest = contestRepository.getMatchContestSDetail(matchContestId,
                baseRequest.authUserId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> outputPractice = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            JSONObject jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;

            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            perContests.put("total_team_left",
                    (int) hashMap.get("total_team") - (int) hashMap.get("total_joined_team"));
            perContests.put("slug", hashMap.get("slug"));
            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));

            if (hashMap.get("is_beat_the_expert").equals("Y")) {
                perContests.put("entry_fee_multiplier", hashMap.get("entry_fee_multiplier"));
                perContests.put("max_entry_fees", hashMap.get("max_entry_fees"));

                HashSet<Object> entry_fees_suggest = new HashSet<Object>();
                entry_fees_suggest.add(perContests.get("entry_fees"));
                entry_fees_suggest.add((int) perContests.get("max_entry_fees") * 0.75);
                entry_fees_suggest.add(perContests.get("max_entry_fees"));
                perContests.put("entry_fees_suggest", entry_fees_suggest);
            }

            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {
                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }

            perContests.put("is_beat_the_expert", hashMap.get("is_beat_the_expert"));

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));
            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;
            if (output.containsKey((Integer) hashMap.get("cat_id"))) {

                LinkedHashMap<String, Object> linkedHashMap = output.get((Integer) hashMap.get("cat_id"));
                List<LinkedHashMap<String, Object>> contests = (List<LinkedHashMap<String, Object>>) linkedHashMap
                        .get("contests");
                contests.add(perContests);

            } else {
                List<LinkedHashMap<String, Object>> contests = new ArrayList<LinkedHashMap<String, Object>>();
                contests.add(perContests);

                LinkedHashMap<String, Object> contestCategory = new LinkedHashMap<String, Object>();
                contestCategory.put("id", hashMap.get("cat_id"));
                contestCategory.put("name", hashMap.get("name"));
                contestCategory.put("cash_bonus_used_type", hashMap.get("cat_cash_bonus_used_type"));
                contestCategory.put("cash_bonus_used_value", hashMap.get("cat_cash_bonus_used_value"));
                contestCategory.put("description", hashMap.get("description"));
                contestCategory.put("is_discounted", hashMap.get("is_discounted"));
                contestCategory.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CONTEXTCATEGORY_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
                if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                    contestCategory.put("discount_image", "");
                    contestCategory.put("discount_image_width", "0");
                    contestCategory.put("discount_image_height", "0");
                } else {
                    contestCategory.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                    contestCategory.put("discount_image_width", hashMap.get("discount_image_width"));
                    contestCategory.put("discount_image_height", hashMap.get("discount_image_height"));
                }
                contestCategory.put("contests", contests);
                output.put((Integer) hashMap.get("cat_id"), contestCategory);
            }
        }

        long matchCustomerTeamCountByMatchUniqueId = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        long matchCustomerContestCountByMatchUniqueId = contestRepository
                .getMatchCustomerContestCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        HashMap<String, Object> detail = new HashMap<String, Object>();
        detail.put("total_teams", matchCustomerTeamCountByMatchUniqueId);
        detail.put("total_joined_contest", matchCustomerContestCountByMatchUniqueId);

        return new ContestBaseResponse(0, false, "Contest Listing.", output.values(), null, detail, null, null, totalFavorite);
    }

    public BaseResponse getMatchPrivateContestDetail(String slug, BaseRequest baseRequest, int matchUniqueId) {
        List<HashMap<String, Object>> matchContest = contestRepository.getMatchPrivateContestDetail(slug,
                baseRequest.authUserId, matchUniqueId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> outputPractice = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> modifiedMatchData = null;
        JSONObject jsonObject1 = null;
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            int matchUniqueIdd = (int) hashMap.get("match_unique_id");
            TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchData(matchUniqueIdd);
            modifiedMatchData = matchesService.getModifiedMatchData(matchDataOnly);
            if (!modifiedMatchData.get("match_progress").equals("F")) {
                return new BaseResponse(1, true,
                        "The deadline has passed! Check out the contests you've joined for this match.", null);
            }

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;

            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            perContests.put("total_team_left",
                    (int) hashMap.get("total_team") - (int) hashMap.get("total_joined_team"));
            perContests.put("slug", hashMap.get("slug"));

            if ((int) perContests.get("total_team_left") <= 0) {
                return new BaseResponse(1, true, "Oops! Contest already full.", null);
            }

            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));

            if (hashMap.get("is_beat_the_expert").equals("Y")) {
                perContests.put("entry_fee_multiplier", hashMap.get("entry_fee_multiplier"));
                perContests.put("max_entry_fees", hashMap.get("max_entry_fees"));

                HashSet<Object> entry_fees_suggest = new HashSet<Object>();
                entry_fees_suggest.add(perContests.get("entry_fees"));
                entry_fees_suggest.add((int) perContests.get("max_entry_fees") * 0.75);
                entry_fees_suggest.add(perContests.get("max_entry_fees"));
                perContests.put("entry_fees_suggest", entry_fees_suggest);
            }

            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {
                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }

            perContests.put("is_beat_the_expert", hashMap.get("is_beat_the_expert"));

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));

            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;
            if (output.containsKey((Integer) hashMap.get("cat_id"))) {

                LinkedHashMap<String, Object> linkedHashMap = output.get((Integer) hashMap.get("cat_id"));
                List<LinkedHashMap<String, Object>> contests = (List<LinkedHashMap<String, Object>>) linkedHashMap
                        .get("contests");
                contests.add(perContests);

            } else {
                List<LinkedHashMap<String, Object>> contests = new ArrayList<LinkedHashMap<String, Object>>();
                contests.add(perContests);

                LinkedHashMap<String, Object> contestCategory = new LinkedHashMap<String, Object>();
                contestCategory.put("id", hashMap.get("cat_id"));
                contestCategory.put("name", hashMap.get("name"));
                contestCategory.put("cash_bonus_used_type", hashMap.get("cat_cash_bonus_used_type"));
                contestCategory.put("cash_bonus_used_value", hashMap.get("cat_cash_bonus_used_value"));
                contestCategory.put("description", hashMap.get("description"));
                contestCategory.put("is_discounted", hashMap.get("is_discounted"));
                contestCategory.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CONTEXTCATEGORY_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));
                if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                    contestCategory.put("discount_image", "");
                    contestCategory.put("discount_image_width", "0");
                    contestCategory.put("discount_image_height", "0");
                } else {
                    contestCategory.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                    contestCategory.put("discount_image_width", hashMap.get("discount_image_width"));
                    contestCategory.put("discount_image_height", hashMap.get("discount_image_height"));
                }
                contestCategory.put("contests", contests);
                output.put((Integer) hashMap.get("cat_id"), contestCategory);
            }
        }

        return new PrivateBaseResponse(0, false, "Contest Detail.", output.values(), modifiedMatchData,
                jsonObject1 != null ? jsonObject1.toMap() : jsonObject1, totalFavorite);
    }

    public BaseResponse getMatchContestShareDetail(BaseRequest baseRequest, int matchContestId) {
        HashMap<String, Object> matchContestShareDetail = contestRepository.getMatchContestShareDetail(matchContestId);
        if (matchContestShareDetail == null) {
            return new ShareBaseResponse(1, true, "No Contest Found.", null, null);
        }

        HashMap<String, Object> customerDynamicLink = customerService.getCustomerDynamicLink(baseRequest.authUserId);
        if (customerDynamicLink == null) {
            return new ShareBaseResponse(1, true, "No Contest Found.", null, null);
        }

        String dynamicLink = (String) customerDynamicLink.get("dynamic_link");

        String name = (String) matchContestShareDetail.get("name");
        String slug1 = (String) matchContestShareDetail.get("slug");
        String is_beat_the_expert = (String) matchContestShareDetail.get("is_beat_the_expert");
        String image = Util.generateImageUrl((String) matchContestShareDetail.get("image"), FileUploadConstant.MATCH_IMAGE_LARGE_URL, FileUploadConstant.NO_IMG_URL);
        String message = GlobalConstant.CONTEST_SHARE_MESSAGE;
        message = message.replace("{MATCH_NAME}", name);
        message = message.replace("{CONTES_SLUG}", slug1);
        message = message.replace("{APP_DYNAMIC_LINK}", dynamicLink);
        return new ShareBaseResponse(0, false, message, slug1, image);
    }

    public BaseResponse getMatchContestPdf(int matchContestId, int matchUniqueId, BaseRequest baseRequest) {
        HashMap<String, Object> matchContestPdf = contestRepository.getMatchContestPdf(matchContestId);

        if (matchContestPdf == null) {
            return new BaseResponse(1, true, "Hang on! File is in processing. Try after some time.", null);
        }

        if (Util.isEmpty((String) matchContestPdf.get("contest_pdf"))) {
            return new BaseResponse(1, true, "Hang on! File is in processing. Try after some time.", null);
        }

        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("match_contest_id", matchContestId);
        output.put("contest_pdf", Util.generateImageUrl((String) matchContestPdf.get("contest_pdf"), FileUploadConstant.PDF_URL, ""));
        return new BaseResponse(0, false, "Contest Pdf.", output);
    }

    public BaseResponse getContestTeams(int UserId, int matchUniqueId, int matchContestId, int pageNo) {
        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchMiniDataOnly(matchUniqueId);
        int beatExpertTeamId = 0;
        HashMap<String, Object> beatTheExpertTeamId = contestRepository.getBeatTheExpertTeamId(matchContestId);
        if (beatTheExpertTeamId != null) {
            beatExpertTeamId = (int) beatTheExpertTeamId.get("team_id");
        }

        List<HashMap<String, Object>> contestTeams = contestRepository.getContestTeams(UserId, matchUniqueId,
                matchContestId, pageNo, matchDataOnly, beatExpertTeamId);
        if (contestTeams.size() == 0) {
            return new BaseResponse(0, false, "No Teams Found.", contestTeams);
        }
        int contestTeamsCount = contestRepository.getContestTeamsCount(matchUniqueId, matchContestId);
//        HashMap<String, Object> beatExpertAdminTeam = contestRepository.getBeatExpertAdminTeam(matchUniqueId,
//                matchContestId);

        return new ContestTeamBaseResponse(0, false, "Team list.", contestTeams, contestTeamsCount,
                null);

    }

    public void callCreateDuplicateContest(int matchContestId) {
        contestRepository.createDuplicateMatchContest(matchContestId);
    }

    public BaseResponse customerPreJoinContest(BaseRequest baseRequest) {
        String customerTeamIds = baseRequest.getParam("customer_team_ids");

        String[] customerTeamIdsExplodeData = customerTeamIds.split(",");
        int customerTeamIdsCount = customerTeamIdsExplodeData.length;
        Integer[] customerTeamIdsExplode = null;
        if (customerTeamIdsCount > 0) {
            customerTeamIdsExplode = new Integer[customerTeamIdsCount];
            for (int i = 0; i < customerTeamIdsExplodeData.length; i++) {
                String teamId = customerTeamIdsExplodeData[i];
                customerTeamIdsExplode[i] = Integer.parseInt(teamId);
            }
        }

        float entry_fees = (float) 0.00;
        if (baseRequest.hasParam("entry_fees")) {
            entry_fees = Float.parseFloat(baseRequest.getParam("entry_fees"));
        }
        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));
        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(match_unique_id);
        if (matchDataOnly == null) {
            return new BaseResponse(1, true, "Invalid match.", null);
        }

        if (!matchDataOnly.isFixtureMatch()) {
            return new BaseResponse(1, true,
                    "The deadline has passed! Check out the contests you've joined for this match.", null);
        }
        List<HashMap<String, Object>> matchCustomerTeamCount = teamsService.getTeamsRepository().getMatchCustomerTeamCount(customerTeamIdsExplode, baseRequest.authUserId);

        if (matchCustomerTeamCount.size() != customerTeamIdsCount) {
            return new BaseResponse(1, true, "Invalid teams.", null);
        }
        int ifTeamAlreadyJoinInContest = contestRepository.ifTeamAlreadyJoinInContest(baseRequest.authUserId,
                customerTeamIdsExplode, Integer.parseInt(baseRequest.getParam("match_contest_id")));
        if (ifTeamAlreadyJoinInContest > 0) {

            return new BaseResponse(1, true, "Invalid teams already joined.", null);
        }

        LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService.getCustomerRepository()
                .getUpdatedWalletData(baseRequest.authUserId);

        Float depositWallet = (Float) updatedWalletData.get("wallet").get("deposit_wallet");
        Float winningWallet = (Float) updatedWalletData.get("wallet").get("winning_wallet");
        Float bonusWallet = (Float) updatedWalletData.get("wallet").get("bonus_wallet");

        HashMap<String, Object> contetDetail = contestRepository.contestDetailForJoin(baseRequest.authUserId,
                Integer.parseInt(baseRequest.getParam("match_contest_id")));

        if (contetDetail == null) {
            return new BaseResponse(1, true, "No contest found.", null);
        }

        if (contetDetail.get("multi_team_allowed").equals("N") && customerTeamIdsCount > 1) {
            return new BaseResponse(1, true, "Multi team not allowed.", null);
        }

        Float usedBonus = (float) 0.00;
        Float usedDeposit = (float) 0.00;
        Float usedWinning = (float) 0.00;

        Float BONUS_WALLET_PER = (float) 0.00;
        String BONUS_WALLET_PER_TYPE = "P";
        int matchContestId = Integer.parseInt(baseRequest.getParam("match_contest_id"));
        int totalTeam = (int) contetDetail.get("total_team");
        int perUserTeamAllowed = (int) contetDetail.get("per_user_team_allowed");
        int totalJoinedTeamsCount = (int) contetDetail.get("total_joined_teams_count");
        int customerJoinedTeamsCount = 0;
        if (!Util.isEmpty((String) contetDetail.get("customer_team_ids"))) {
            String customerTeamIdss = (String) contetDetail.get("customer_team_ids");
            String[] joinedTeams = customerTeamIdss.split(",");
            customerJoinedTeamsCount = joinedTeams.length;
        }

        if ((totalJoinedTeamsCount + customerTeamIdsCount) > totalTeam) {

            if ((totalTeam - totalJoinedTeamsCount) <= 0) {
                callCreateDuplicateContest(matchContestId);
            }
            return new BaseResponse(1, true, "Oops! Only " + (totalTeam - totalJoinedTeamsCount) + " spot Left.", null);
        }

        if ((customerJoinedTeamsCount + customerTeamIdsCount) > perUserTeamAllowed) {
            return new BaseResponse(1, true,
                    "Oops! You can select Only " + (perUserTeamAllowed - customerJoinedTeamsCount) + " teams.", null);
        }

        if (contetDetail.get("is_beat_the_expert").equals("Y")) {

            if (Float.parseFloat((String) contetDetail.get("entry_fees")) <= entry_fees
                    && Float.parseFloat((String) contetDetail.get("max_entry_fees")) >= entry_fees) {
                entry_fees = entry_fees;
            } else {
                return new BaseResponse(1, true, "Invalid Entry Fees.", null);
            }
        } else {
            entry_fees = (float) contetDetail.get("entry_fees");
        }

        Float cashBonusUsedValue = (Float) contetDetail.get("cash_bonus_used_value");
        String cashBonusUsedType = (String) contetDetail.get("cash_bonus_used_type");

        BONUS_WALLET_PER = cashBonusUsedValue;
        BONUS_WALLET_PER_TYPE = cashBonusUsedType;

        entry_fees = entry_fees * customerTeamIdsCount;
        Float need_pay = entry_fees;
        if (BONUS_WALLET_PER_TYPE.equals("F") && BONUS_WALLET_PER > need_pay) {
            BONUS_WALLET_PER = need_pay;
        }

        if (need_pay > 0) {

            if (bonusWallet > 0) {
                usedBonus = entry_fees * (BONUS_WALLET_PER / 100);
                if (BONUS_WALLET_PER_TYPE.equals("F")) {
                    usedBonus = BONUS_WALLET_PER;
                }
                usedBonus = Util.numberFormate(usedBonus, 2);

                if (usedBonus > bonusWallet) {
                    usedBonus = bonusWallet;
                }

                need_pay -= usedBonus;
            }

            if (need_pay > 0) {

                if (depositWallet > 0) {
                    usedDeposit = need_pay;
                    if (usedDeposit > depositWallet) {
                        usedDeposit = depositWallet;
                    }
                    need_pay -= usedDeposit;
                }
            }

            if (need_pay > 0) {
                if (winningWallet > 0) {
                    usedWinning = need_pay;
                    if (usedWinning > winningWallet) {
                        usedWinning = winningWallet;
                    }
                    need_pay -= usedWinning;
                }

            }

        }
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("wallet", updatedWalletData.get("wallet"));
        output.put("used_bonus", usedBonus);
        output.put("used_deposit", usedDeposit);
        output.put("used_winning", usedWinning);
        output.put("need_pay", need_pay);
        output.put("entry_fees", entry_fees);
        output.put("to_pay", entry_fees - usedBonus);

        HashSet<Float> amount_suggest = new HashSet<Float>();
        if (need_pay > 0) {
            float a = ((int) (need_pay / 10)) * 10;
            if (a < need_pay) {
                a = a + 10;
            }
            float aa = a * 2;
            float aaa = (aa * 2) + a;
            amount_suggest.add(a);
            amount_suggest.add(aa);
            amount_suggest.add(aaa);

        }
        output.put("amount_suggest", amount_suggest);

        return new BaseResponse(0, false, "Contest joined detail.", output);

    }

    public BaseResponse customerJoinContestMulti(BaseRequest baseRequest) {

        String customerTeamIds = baseRequest.getParam("customer_team_id");
        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));

        String[] customerTeamIdsExplodeData = customerTeamIds.split(",");
        int customerTeamIdsCount = customerTeamIdsExplodeData.length;
        Integer[] customerTeamIdsExplode = null;
        if (customerTeamIdsCount > 0) {
            customerTeamIdsExplode = new Integer[customerTeamIdsCount];
            for (int i = 0; i < customerTeamIdsExplodeData.length; i++) {
                String teamId = customerTeamIdsExplodeData[i];
                customerTeamIdsExplode[i] = Integer.parseInt(teamId);
            }
        }

        float entry_fees = (float) 0.00;
        if (baseRequest.hasParam("entry_fees")) {
            entry_fees = Float.parseFloat(baseRequest.getParam("entry_fees"));
        }

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(match_unique_id);
        if (matchDataOnly == null) {
            return new BaseResponse(1, true, "Invalid match.", null);
        }

        if (!matchDataOnly.isFixtureMatch()) {
            return new BaseResponse(1, true,
                    "The deadline has passed! Check out the contests you've joined for this match.", null);
        }
        List<HashMap<String, Object>> matchCustomerTeamCount = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCount(customerTeamIdsExplode, baseRequest.authUserId);

        if (matchCustomerTeamCount.size() != customerTeamIdsCount) {
            return new BaseResponse(1, true, "Invalid teams.", null);
        }

        int ifTeamAlreadyJoinInContest = contestRepository.ifTeamAlreadyJoinInContest(baseRequest.authUserId,
                customerTeamIdsExplode, Integer.parseInt(baseRequest.getParam("match_contest_id")));
        if (ifTeamAlreadyJoinInContest > 0) {

            return new BaseResponse(1, true, "Invalid teams.", null);
        }

        HashMap<String, Object> contetDetail = contestRepository.contestDetailForJoin(baseRequest.authUserId,
                Integer.parseInt(baseRequest.getParam("match_contest_id")));

        if (contetDetail == null) {
            return new BaseResponse(1, true, "No contest found.", null);
        }

        int matchContestId = (int) contetDetail.get("id");
        int matchUniqueId = (int) contetDetail.get("unique_id");
        if (matchUniqueId != match_unique_id) {
            return new BaseResponse(1, true, "No contest found.", null);
        }

        if (contetDetail.get("is_beat_the_expert").equals("Y")) {

            if (Float.parseFloat((String) contetDetail.get("entry_fees")) <= entry_fees
                    && Float.parseFloat((String) contetDetail.get("max_entry_fees")) >= entry_fees) {

                entry_fees = entry_fees;
            } else {
                return new BaseResponse(1, true, "Invalid Entry Fees.", null);
            }

        } else {
            entry_fees = (float) contetDetail.get("entry_fees");
        }

        float realEntryFees = entry_fees;
        {
            LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService
                    .getCustomerRepository().getUpdatedWalletData(baseRequest.authUserId);

            Float depositWallet = (Float) updatedWalletData.get("wallet").get("deposit_wallet");
            Float winningWallet = (Float) updatedWalletData.get("wallet").get("winning_wallet");
            Float winningWalletActual = (Float) updatedWalletData.get("wallet").get("winning_wallet_actual");
            Float bonusWallet = (Float) updatedWalletData.get("wallet").get("bonus_wallet");

            entry_fees = entry_fees * customerTeamIdsCount;

            float usedBonus = 0.00f;
            float usedDeposit = 0.00f;
            float usedWinning = 0.00f;
            float need_pay = entry_fees;

            float BONUS_WALLET_PER = 0.00f;
            String BONUS_WALLET_PER_TYPE = "P";

            float cashBonusUsedValue = (float) contetDetail.get("cash_bonus_used_value");
            String cashBonusUsedType = (String) contetDetail.get("cash_bonus_used_type");

            BONUS_WALLET_PER = cashBonusUsedValue;
            BONUS_WALLET_PER_TYPE = cashBonusUsedType;

            if (BONUS_WALLET_PER_TYPE.equals("F") && BONUS_WALLET_PER > need_pay) {
                BONUS_WALLET_PER = need_pay;
            }

            if (need_pay > 0) {

                if (bonusWallet > 0) {
                    usedBonus = entry_fees * (BONUS_WALLET_PER / 100);
                    if (BONUS_WALLET_PER_TYPE.equals("F")) {
                        usedBonus = BONUS_WALLET_PER;
                    }
                    usedBonus = Util.numberFormate(usedBonus, 2);

                    if (usedBonus > bonusWallet) {
                        usedBonus = bonusWallet;
                    }

                    need_pay -= usedBonus;
                }

                if (need_pay > 0) {

                    if (depositWallet > 0) {
                        usedDeposit = need_pay;
                        if (usedDeposit > depositWallet) {
                            usedDeposit = depositWallet;
                        }
                        need_pay -= usedDeposit;
                    }
                }

                if (need_pay > 0) {
                    if (winningWallet > 0) {
                        usedWinning = need_pay;
                        if (usedWinning > winningWallet) {
                            usedWinning = winningWallet;
                        }
                        need_pay -= usedWinning;
                    }

                }

            }

            if (need_pay > 0) {
                return new BaseResponse(1, true, "low balance.", null);
            }
        }

        List<String> non_joined_teams = new ArrayList<String>();
        entry_fees = realEntryFees;
        int totalTeam = (int) contetDetail.get("total_team");
        int mainTotalJoinedTeamsCount = (int) contetDetail.get("total_joined_teams_count");

        int customerJoinedTeamsCount = 0;
        if (!Util.isEmpty((String) contetDetail.get("customer_team_ids"))) {
            String customerTeamIdss = (String) contetDetail.get("customer_team_ids");
            String[] joinedTeams = customerTeamIdss.split(",");
            customerJoinedTeamsCount = joinedTeams.length;
        }

        LinkedHashMap<String, Object> updatedWalletData = customerService.getCustomerRepository()
                .getUpdatedWalletData(baseRequest.authUserId).get("wallet");

        List<HashMap<String, Object>> mailData = new ArrayList<HashMap<String, Object>>();

        for (HashMap<String, Object> teamData : matchCustomerTeamCount) {
            int teamId = (int) teamData.get("id");
            int team_name = (int) teamData.get("name");
            int selectContestTeamCount = contestRepository.selectContestTeamCount(matchContestId);
            if (selectContestTeamCount >= totalTeam) {
                callCreateDuplicateContest(matchContestId);
                non_joined_teams.add(String.valueOf(team_name));
                continue;
            }

            float depositWallet = (float) updatedWalletData.get("deposit_wallet");
            float winningWallet = (float) updatedWalletData.get("winning_wallet");
            float winningWalletActual = (float) updatedWalletData.get("winning_wallet_actual");
            float bonusWallet = (float) updatedWalletData.get("bonus_wallet");
            float usedBonus = 0.00f;
            float usedDeposit = 0.00f;
            float usedWinning = 0.00f;
            Float need_pay = entry_fees;

            float BONUS_WALLET_PER = 0.00f;
            String BONUS_WALLET_PER_TYPE = "P";

            float cashBonusUsedValue = (float) contetDetail.get("cash_bonus_used_value");
            String cashBonusUsedType = (String) contetDetail.get("cash_bonus_used_type");

            BONUS_WALLET_PER = cashBonusUsedValue;
            BONUS_WALLET_PER_TYPE = cashBonusUsedType;

            if (BONUS_WALLET_PER_TYPE.equals("F") && BONUS_WALLET_PER > need_pay) {
                BONUS_WALLET_PER = need_pay;
            }

            if (need_pay > 0) {

                if (bonusWallet > 0) {
                    usedBonus = entry_fees * (BONUS_WALLET_PER / 100);
                    if (BONUS_WALLET_PER_TYPE.equals("F")) {
                        usedBonus = BONUS_WALLET_PER;
                    }
                    usedBonus = Util.numberFormate(usedBonus, 2);

                    if (usedBonus > bonusWallet) {
                        usedBonus = bonusWallet;
                    }

                    need_pay -= usedBonus;
                }

                if (need_pay > 0) {

                    if (depositWallet > 0) {
                        usedDeposit = need_pay;
                        if (usedDeposit > depositWallet) {
                            usedDeposit = depositWallet;
                        }
                        need_pay -= usedDeposit;
                    }
                }

                if (need_pay > 0) {
                    if (winningWallet > 0) {
                        usedWinning = need_pay;
                        if (usedWinning > winningWallet) {
                            usedWinning = winningWallet;
                        }
                        need_pay -= usedWinning;
                    }

                }

            }

            if (need_pay > 0) {
                non_joined_teams.add(String.valueOf(team_name));
                continue;
            }

            LinkedHashMap<String, Object> extraData = new LinkedHashMap<String, Object>();
            extraData.put("teamId", teamId);
            extraData.put("entryFee", entry_fees);
            extraData.put("usedBonus", usedBonus);
            extraData.put("usedDeposit", usedDeposit);
            extraData.put("usedWinning", usedWinning);
            extraData.put("depositWallet", depositWallet);
            extraData.put("winningWallet", winningWallet);
            extraData.put("winningWalletActual", winningWalletActual);
            extraData.put("bonusWallet", bonusWallet);
            extraData.put("totalTeam", totalTeam);

            TblBasketballCustomerContest customerJoinContest = contestRepository.customerJoinContest(matchDataOnly,
                    baseRequest.authUserId, matchContestId, extraData);

            if (customerJoinContest == null) {
                non_joined_teams.add(String.valueOf(team_name));
                continue;
            } else {
//                contestRepository.incrementContestTeamCount(matchContestId);
                updatedWalletData.put("deposit_wallet", depositWallet - usedDeposit);
                updatedWalletData.put("winning_wallet", winningWallet - usedWinning);
                updatedWalletData.put("winning_wallet_actual", winningWalletActual - usedWinning);
                updatedWalletData.put("bonus_wallet", bonusWallet - usedBonus);
                mainTotalJoinedTeamsCount++;

                HashMap<String, Object> mail = new HashMap<String, Object>();
                mail.put("amount", entry_fees);
                mailData.add(mail);

                customerJoinedTeamsCount++;
            }
        }

        contestRepository.updateCustomerContestTeams(baseRequest.authUserId, matchDataOnly.getUniqueId(),
                matchContestId);

        contestRepository.updateCustomerMatchContestInfo(matchDataOnly.getUniqueId(), baseRequest.authUserId);

        if (mainTotalJoinedTeamsCount >= totalTeam) {
            callCreateDuplicateContest(matchContestId);
        }

        HashMap<String, Object> updatedProfileData = customerService.getUpdatedProfileData(baseRequest.authUserId);
        if (mailData.size() > 0 && matchDataOnly.isContestJoinedMailActive()) {
            TblTemplate template = customerService.getCustomerRepository().getTemplate("E", "join_contest");
            if (template != null) {
                contestRepository.saveJoinedContestMail(template, mailData, matchDataOnly,
                        (String) updatedProfileData.get("fullname"), (String) updatedProfileData.get("email"));
            }

        }
        if (non_joined_teams.size() > 0) {
            String non_joined_teams_string = "";
            for (String string : non_joined_teams) {
                if (non_joined_teams_string.isEmpty()) {
                    non_joined_teams_string = string;
                } else {
                    non_joined_teams_string += "," + string;
                }
            }

            return new JoinContestResponse(0, false,
                    "Team " + non_joined_teams_string + " not joined due to some error.", updatedProfileData,
                    matchContestId);
        } else {
            return new JoinContestResponse(0, false, "Contest joined successfully.", updatedProfileData,
                    matchContestId);
        }
    }

    public BaseResponse customerSwitchTeam(BaseRequest baseRequest) {
        int matchUniqueId = Integer.parseInt(baseRequest.getParam("match_unique_id"));
        String matchContestId = baseRequest.getParam("match_contest_id");
        int customerTeamIdOld = Integer.parseInt(baseRequest.getParam("customer_team_id_old"));
        int customerTeamIdNew = Integer.parseInt(baseRequest.getParam("customer_team_id_new"));

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(matchUniqueId);
        if (matchDataOnly == null || !matchDataOnly.isFixtureMatch()) {
            return new BaseResponse(0, true,
                    "The deadline has passed! Check out the contests you've joined for this match.", null);
        }

        int ifTeamAlreadyJoinInContest = contestRepository.ifTeamAlreadyJoinInContest(baseRequest.authUserId,
                new Integer[]{customerTeamIdNew}, Integer.parseInt(matchContestId));
        if (ifTeamAlreadyJoinInContest > 0) {
            return new BaseResponse(1, true, "Team already joined.", null);
        }

        int customerSwitchTeam = contestRepository.customerSwitchTeam(baseRequest.authUserId, matchUniqueId,
                Integer.parseInt(matchContestId), customerTeamIdOld, customerTeamIdNew);
        if (customerSwitchTeam <= 0) {
            return new BaseResponse(1, true, "Unable to proceed.", null);
        }

        contestRepository.updateCustomerContestTeams(baseRequest.authUserId, matchUniqueId,
                Integer.parseInt(matchContestId));

        contestRepository.updateSwitchTeamInWalletHistory(baseRequest.authUserId, matchUniqueId,
                Integer.parseInt(matchContestId), customerTeamIdOld, customerTeamIdNew);

        return new BaseResponse(1, false, "Team successfully Switched.", null);

    }

    public BaseResponse getPrivateContestSettings(BaseRequest baseRequest) {
        HashMap<String, String> settingData = customerService.getSettingData();

        LinkedHashMap<String, Object> privateContestSetting = new LinkedHashMap<>();

        privateContestSetting.put("PRIVATE_CONTEST_MAX_CONTEST_SIZE",
                settingData.get("PRIVATE_CONTEST_MAX_CONTEST_SIZE"));
        privateContestSetting.put("PRIVATE_CONTEST_MAX_PRIZE_POOL", settingData.get("PRIVATE_CONTEST_MAX_PRIZE_POOL"));

        return new BaseResponse(0, false, "settings.", privateContestSetting);

    }

    public BaseResponse getPrivateContestEntryFee(BaseRequest baseRequest) {
        HashMap<String, String> settingData = customerService.getSettingData();
        String contest_size = baseRequest.getParam("contest_size");
        String prize_pool = baseRequest.getParam("prize_pool");

        long contestSize = Long.parseLong(contest_size);
        float prizePool = Float.parseFloat(prize_pool);

        float PRIVATE_CONTEST_MAX_PRIZE_POOL = Float.parseFloat(settingData.get("PRIVATE_CONTEST_MAX_PRIZE_POOL"));

        long PRIVATE_CONTEST_MAX_CONTEST_SIZE = Long.parseLong(settingData.get("PRIVATE_CONTEST_MAX_CONTEST_SIZE"));

        if (PRIVATE_CONTEST_MAX_PRIZE_POOL < prizePool || prizePool < 0) {

            return new BaseResponse(0, true, "Invalid prize pool.", null);

        }

        if (PRIVATE_CONTEST_MAX_CONTEST_SIZE < contestSize || contestSize < 0) {

            return new BaseResponse(0, true, "Invalid Contest size.", null);
        }

        float PRIVATE_CONTEST_COMMISSION = Float.parseFloat(settingData.get("PRIVATE_CONTEST_COMMISSION"));
        float PRIVATE_CONTEST_MIN_FEE = Float.parseFloat(settingData.get("PRIVATE_CONTEST_MIN_FEE"));
        float entryFee = (PRIVATE_CONTEST_COMMISSION / 100) * prizePool;
        entryFee = (entryFee + prizePool) / contestSize;

        entryFee = Util.numberFormate(entryFee, 2);

        if (entryFee < PRIVATE_CONTEST_MIN_FEE) {

            return new BaseResponse(0, true, "Invalid entry fee.", null);
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        output.put("entry_fees", entryFee);
        return new BaseResponse(0, false, "Entry fees.", output);
    }

    public BaseResponse getPrivateContestWinningBreakup(BaseRequest baseRequest) {
        HashMap<String, String> settingData = customerService.getSettingData();
        String contest_size = baseRequest.getParam("contest_size");
        String prize_pool = baseRequest.getParam("prize_pool");

        int contestSize = Integer.parseInt(contest_size);
        float prizePool = Float.parseFloat(prize_pool);

        float PRIVATE_CONTEST_MAX_PRIZE_POOL = Float.parseFloat(settingData.get("PRIVATE_CONTEST_MAX_PRIZE_POOL"));

        long PRIVATE_CONTEST_MAX_CONTEST_SIZE = Long.parseLong(settingData.get("PRIVATE_CONTEST_MAX_CONTEST_SIZE"));

        if (PRIVATE_CONTEST_MAX_PRIZE_POOL < prizePool || prizePool < 0) {

            return new BaseResponse(0, true, "Invalid prize pool.", null);

        }

        if (PRIVATE_CONTEST_MAX_CONTEST_SIZE < contestSize || contestSize < 0) {

            return new BaseResponse(0, true, "Invalid Contest size.", null);
        }

        HashMap<String, Object> categoryData = contestRepository.getPrivateContestCategory();
        if (categoryData == null) {
            return new BaseResponse(0, true, "Invalid Private contest category id.", null);
        }

        HashMap<String, Object> privateContestBrackupIdsForContestSize = contestRepository
                .getPrivateContestBrackupIdsForContestSize(contestSize);

        if (privateContestBrackupIdsForContestSize == null) {
            return new BaseResponse(0, true, "Invalid BreakupIds.", null);
        }

        String breakupIdsD = privateContestBrackupIdsForContestSize.get("breakupIds").toString();
        String[] split = breakupIdsD.split(",");
        List<Integer> breakupIds = new ArrayList<Integer>();
        for (int i = 0; i < split.length; i++) {
            breakupIds.add(Integer.parseInt(split[i]));
        }

        if (breakupIds.isEmpty()) {
            return new BaseResponse(0, true, "Invalid Winning breakup id.", null);
        }

        LinkedHashMap<String, Object> privateContestBreakupsFromIds = contestRepository
                .getPrivateContestBreakupsFromIds(breakupIds, prizePool);
        if (privateContestBreakupsFromIds == null) {
            return new BaseResponse(0, true, "Invalid Winning breakup id.", null);
        }

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("private_contest_category", categoryData);
        output.put("winning_breakups", privateContestBreakupsFromIds.values());

        return new BaseResponse(0, false, "winning breakups.", output);
    }

    public BaseResponse createPrivateContest(BaseRequest baseRequest) {
        HashMap<String, String> settingData = customerService.getSettingData();

        String contest_size = baseRequest.getParam("contest_size");
        String prize_pool = baseRequest.getParam("prize_pool");
        String winning_breakup_id = baseRequest.getParam("winning_breakup_id");
        String matchId = baseRequest.getParam("match_id");
        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));
        String preJoin = baseRequest.getParam("pre_join");
        String isMultiple = baseRequest.getParam("is_multiple");
        String teamId = baseRequest.getParam("team_id");

        int contestSize = Integer.parseInt(contest_size);
        float prizePool = Float.parseFloat(prize_pool);

        float PRIVATE_CONTEST_MAX_PRIZE_POOL = Float.parseFloat(settingData.get("PRIVATE_CONTEST_MAX_PRIZE_POOL"));

        long PRIVATE_CONTEST_MAX_CONTEST_SIZE = Long.parseLong(settingData.get("PRIVATE_CONTEST_MAX_CONTEST_SIZE"));

        if (PRIVATE_CONTEST_MAX_PRIZE_POOL < prizePool || prizePool < 0) {

            return new BaseResponse(0, true, "Invalid prize pool.", null);

        }

        if (PRIVATE_CONTEST_MAX_CONTEST_SIZE < contestSize || contestSize < 0) {

            return new BaseResponse(0, true, "Invalid Contest size.", null);
        }

        float PRIVATE_CONTEST_COMMISSION = Float.parseFloat(settingData.get("PRIVATE_CONTEST_COMMISSION"));
        float PRIVATE_CONTEST_MIN_FEE = Float.parseFloat(settingData.get("PRIVATE_CONTEST_MIN_FEE"));
        float entryFee = (PRIVATE_CONTEST_COMMISSION / 100) * prizePool;
        entryFee = (entryFee + prizePool) / contestSize;

        entryFee = Util.numberFormate(entryFee, 2);

        if (entryFee < PRIVATE_CONTEST_MIN_FEE) {

            return new BaseResponse(0, true, "Invalid entry fee.", null);
        }

        HashMap<String, Object> categoryData = contestRepository.getPrivateContestCategory();
        if (categoryData == null) {
            return new BaseResponse(0, true, "Invalid Private contest category id.", null);
        }

        List<Integer> winngbreakUpIds = new ArrayList<Integer>();
        winngbreakUpIds.add(Integer.parseInt(winning_breakup_id));
        LinkedHashMap<String, Object> privateContestBreakupsFromIds = contestRepository
                .getPrivateContestBreakupsFromIds(winngbreakUpIds, prizePool);
        if (privateContestBreakupsFromIds == null) {
            return new BaseResponse(0, true, "Invalid Winning breakup id.", null);
        }

        privateContestBreakupsFromIds = (LinkedHashMap<String, Object>) privateContestBreakupsFromIds
                .get(winning_breakup_id);

        if (privateContestBreakupsFromIds == null) {
            return new BaseResponse(0, true, "Invalid Winning breakup id.", null);
        }

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(match_unique_id);
        if (matchDataOnly == null) {
            return new BaseResponse(0, true, "Invalid match.", null);
        }

        if (!matchDataOnly.isFixtureMatch()) {
            return new BaseResponse(0, true,
                    "The deadline has passed! Check out the contests you've joined for this match.", null);
        }

        int checkCustomerTeamIdExist = teamsService.getTeamsRepository()
                .checkCustomerTeamIdExist(baseRequest.authUserId, match_unique_id, Integer.parseInt(teamId));
        if (checkCustomerTeamIdExist == 0) {
            return new BaseResponse(0, true, "No team created.", null);
        }

        LinkedHashMap<String, LinkedHashMap<String, Object>> updatedWalletData = customerService.getCustomerRepository()
                .getUpdatedWalletData(baseRequest.authUserId);

        Float depositWallet = (Float) updatedWalletData.get("wallet").get("deposit_wallet");
        Float winningWallet = (Float) updatedWalletData.get("wallet").get("winning_wallet");
        Float winningWalletActual = (Float) updatedWalletData.get("wallet").get("winning_wallet_actual");
        Float bonusWallet = (Float) updatedWalletData.get("wallet").get("bonus_wallet");

        Float usedBonus = (float) 0.00;
        Float usedDeposit = (float) 0.00;
        Float usedWinning = (float) 0.00;

        Float BONUS_WALLET_PER = (float) categoryData.get("cash_bonus_used_value");
        String BONUS_WALLET_PER_TYPE = categoryData.get("cash_bonus_used_type").toString();

        Float need_pay = entryFee;

        if (BONUS_WALLET_PER_TYPE.equals("F") && BONUS_WALLET_PER > need_pay) {
            BONUS_WALLET_PER = need_pay;
        }

        if (need_pay > 0) {

            if (bonusWallet > 0) {
                usedBonus = entryFee * (BONUS_WALLET_PER / 100);
                if (BONUS_WALLET_PER_TYPE.equals("F")) {
                    usedBonus = BONUS_WALLET_PER;
                }
                usedBonus = Util.numberFormate(usedBonus, 2);
                if (usedBonus > bonusWallet) {
                    usedBonus = bonusWallet;
                }

                need_pay -= usedBonus;
            }

            if (need_pay > 0) {

                if (depositWallet > 0) {
                    usedDeposit = need_pay;
                    if (usedDeposit > depositWallet) {
                        usedDeposit = depositWallet;
                    }
                    need_pay -= usedDeposit;
                }
            }

            if (need_pay > 0) {
                if (winningWallet > 0) {
                    usedWinning = need_pay;
                    if (usedWinning > winningWallet) {
                        usedWinning = winningWallet;
                    }
                    need_pay -= usedWinning;
                }

            }

        }

        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("wallet", updatedWalletData.get("wallet"));
        output.put("used_bonus", usedBonus);
        output.put("used_deposit", usedDeposit);
        output.put("used_winning", usedWinning);
        output.put("need_pay", need_pay);
        output.put("entry_fees", entryFee);
        output.put("to_pay", entryFee - usedBonus);

        HashSet<Float> amount_suggest = new HashSet<Float>();
        if (need_pay > 0) {
            float a = ((int) (need_pay / 10)) * 10;
            if (a < need_pay) {
                a = a + 10;
            }
            float aa = a * 2;
            float aaa = (aa * 2) + a;
            amount_suggest.add(a);
            amount_suggest.add(aa);
            amount_suggest.add(aaa);

        }
        output.put("amount_suggest", amount_suggest);
        if (preJoin.equals("Y")) {
            return new BaseResponse(0, false, "Contest joined detail.", output);
        } else {
            if (need_pay > 0) {
                return new BaseResponse(0, true, "Balance low, please recharge wallet.", null);
            }
        }

        LinkedHashMap<String, Object> extraData = new LinkedHashMap<String, Object>();
        extraData.put("entryFee", entryFee);
        extraData.put("isMultiple", isMultiple);
        extraData.put("prizePool", prizePool);
        extraData.put("contestSize", contestSize);
        extraData.put("teamId", teamId);
        extraData.put("usedBonus", usedBonus);
        extraData.put("usedDeposit", usedDeposit);
        extraData.put("usedWinning", usedWinning);
        extraData.put("depositWallet", depositWallet);
        extraData.put("winningWallet", winningWallet);
        extraData.put("winningWalletActual", winningWalletActual);
        extraData.put("bonusWallet", bonusWallet);
        List<HashMap<String, Object>> mailData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mail = new HashMap<String, Object>();
        mail.put("amount", entryFee);
        mailData.add(mail);

        TblBasketballContestMatch createPrivateContest = contestRepository.createPrivateContest(matchDataOnly,
                categoryData, privateContestBreakupsFromIds, baseRequest.authUserId, extraData);
        if (createPrivateContest == null) {
            return new BaseResponse(0, true, "Unable to proceed for create private contest.", null);
        }

        contestRepository.updateCustomerContestTeams(baseRequest.authUserId, matchDataOnly.getUniqueId(),
                createPrivateContest.getId());

        HashMap<String, Object> updatedProfileData = customerService.getUpdatedProfileData(baseRequest.authUserId);

        if (mailData.size() > 0 && matchDataOnly.isContestJoinedMailActive()) {
            TblTemplate template = customerService.getCustomerRepository().getTemplate("E", "join_contest");
            if (template != null) {
                contestRepository.saveJoinedContestMail(template, mailData, matchDataOnly,
                        (String) updatedProfileData.get("fullname"), (String) updatedProfileData.get("email"));
            }

        }

        return new JoinContestResponse(0, false, "Contest joined successfully.", updatedProfileData,
                createPrivateContest.getId());
    }

    public void updateBeatTheExpertTeam(int matchContestId, int customerTeamId) {
        contestRepository.updateBeatTheExpertTeam(matchContestId, customerTeamId);
    }

    public BaseResponse updateContestFavorite(BaseRequest baseRequest, int masterContestId) {
        return contestRepository.updateContestFavorite(baseRequest.authUserId, masterContestId);
    }


    public BaseResponse getMatchContestFav(BaseRequest baseRequest, int matchId, int matchUniqueId) {

        List<HashMap<String, Object>> matchContest = contestRepository.getMatchContestFav(matchId, matchUniqueId,
                baseRequest.authUserId);
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> output = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        LinkedHashMap<Integer, LinkedHashMap<String, Object>> outputPractice = new LinkedHashMap<Integer, LinkedHashMap<String, Object>>();
        int totalFavorite = 0;
        for (HashMap<String, Object> hashMap : matchContest) {

            LinkedHashMap<String, Object> perContests = new LinkedHashMap<String, Object>();
            perContests.put("id", hashMap.get("id"));
            perContests.put("total_team", hashMap.get("total_team"));
            perContests.put("total_price", hashMap.get("total_price"));
            perContests.put("entry_fees", hashMap.get("entry_fees"));
            perContests.put("per_user_team_allowed", hashMap.get("per_user_team_allowed"));
            JSONObject jsonObject1 = new JSONObject(hashMap.get("contest_json").toString());

            JSONArray totalWinners = jsonObject1.getJSONArray("per_max_p");
            JSONArray totalWinnerss = jsonObject1.getJSONArray("per_price");
            JSONArray totalWinnersGadget = jsonObject1.has("gadget") ? jsonObject1.getJSONArray("gadget") : null;
            perContests.put("total_winners", totalWinners.get(totalWinners.length() - 1));

            perContests.put("match_contest_id", hashMap.get("id"));
            perContests.put("confirm_win", hashMap.get("confirm_win"));
            perContests.put("first_winning_breakup", totalWinnerss.get(0));

            perContests.put("first_rank_gadget", "");
            if (totalWinnersGadget != null) {
                perContests.put("first_rank_gadget", totalWinnersGadget.get(0));
            }

            perContests.put("is_compression_allow", hashMap.get("is_compression_allow"));

            if (!Util.isEmpty((String) hashMap.get("customer_team_ids"))) {
                String customerTeamIds = (String) hashMap.get("customer_team_ids");
                perContests.put("joined_teams", customerTeamIds);
            } else {
                perContests.put("joined_teams", "");
            }

            if (!Util.isEmpty((String) hashMap.get("customer_team_names"))) {
                String joinedTeamsNames = (String) hashMap.get("customer_team_names");
                perContests.put("joined_teams_name", joinedTeamsNames);
            } else {
                perContests.put("joined_teams_name", "");
            }

            perContests.put("total_team_left", (int) hashMap.get("total_team") - (int) hashMap.get("total_joined_team"));
            perContests.put("slug", hashMap.get("slug"));
            perContests.put("more_entry_fees", hashMap.get("more_entry_fees"));
            perContests.put("multi_team_allowed", hashMap.get("multi_team_allowed"));
            perContests.put("actual_entry_fees", hashMap.get("actual_entry_fees"));
            if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                perContests.put("discount_image", "");
                perContests.put("discount_image_width", "0");
                perContests.put("discount_image_height", "0");
            } else {
                perContests.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                perContests.put("discount_image_width", hashMap.get("discount_image_width"));
                perContests.put("discount_image_height", hashMap.get("discount_image_height"));
            }

            perContests.put("is_beat_the_expert", "N");

            perContests.put("cash_bonus_used_type", hashMap.get("cash_bonus_used_type"));
            perContests.put("cash_bonus_used_value", hashMap.get("cash_bonus_used_value"));

            perContests.put("is_favorite", hashMap.get("isFavorite"));
            perContests.put("master_contest_id", hashMap.get("masterContestId"));
            totalFavorite += perContests.get("is_favorite").equals("Y") ? 1 : 0;
            if (output.containsKey((Integer) hashMap.get("cat_id"))) {

                LinkedHashMap<String, Object> linkedHashMap = output.get((Integer) hashMap.get("cat_id"));
                List<LinkedHashMap<String, Object>> contests = (List<LinkedHashMap<String, Object>>) linkedHashMap
                        .get("contests");
                contests.add(perContests);
            } else {
                List<LinkedHashMap<String, Object>> contests = new ArrayList<LinkedHashMap<String, Object>>();
                contests.add(perContests);

                LinkedHashMap<String, Object> contestCategory = new LinkedHashMap<String, Object>();
                contestCategory.put("id", hashMap.get("cat_id"));
                contestCategory.put("name", hashMap.get("name"));
                contestCategory.put("cash_bonus_used_type", hashMap.get("cat_cash_bonus_used_type"));
                contestCategory.put("cash_bonus_used_value", hashMap.get("cat_cash_bonus_used_value"));
                contestCategory.put("description", hashMap.get("description"));
                contestCategory.put("is_discounted", hashMap.get("is_discounted"));
                contestCategory.put("image", Util.generateImageUrl((String) hashMap.get("image"), FileUploadConstant.CONTEXTCATEGORY_IMAGE_THUMB_URL, FileUploadConstant.NO_IMG_URL));

                if (Util.isEmpty((String) hashMap.get("discount_image"))) {
                    contestCategory.put("discount_image", "");
                    contestCategory.put("discount_image_width", "0");
                    contestCategory.put("discount_image_height", "0");
                } else {
                    contestCategory.put("discount_image", Util.generateImageUrl((String) hashMap.get("discount_image"), FileUploadConstant.APP_ICON_CUSTOMIZE_IMAGE_LARGE_URL, ""));
                    contestCategory.put("discount_image_width", hashMap.get("discount_image_width"));
                    contestCategory.put("discount_image_height", hashMap.get("discount_image_height"));
                }
                contestCategory.put("contests", contests);
                output.put((Integer) hashMap.get("cat_id"), contestCategory);

            }


        }

        long matchCustomerTeamCountByMatchUniqueId = teamsService.getTeamsRepository()
                .getMatchCustomerTeamCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        long matchCustomerContestCountByMatchUniqueId = contestRepository
                .getMatchCustomerContestCountByMatchUniqueId(baseRequest.authUserId, matchUniqueId);

        TblBasketballMatch matchMiniDataOnly = matchesService.getMatchesRepository().getMatchMiniDataOnly(matchUniqueId);
        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(matchMiniDataOnly);

        HashMap<String, Object> detail = new HashMap<String, Object>();
        detail.put("total_teams", matchCustomerTeamCountByMatchUniqueId);
        detail.put("total_joined_contest", matchCustomerContestCountByMatchUniqueId);

        return new ContestBaseResponse(0, false, "Contest Listing.", output.values(), outputPractice.values(), detail, null, modifiedMatchData, totalFavorite);

    }

}
