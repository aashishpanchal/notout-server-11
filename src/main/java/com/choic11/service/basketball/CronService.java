package com.choic11.service.basketball;

import com.choic11.AsyncConfiguration;
import com.choic11.ContestPdf;
import com.choic11.GlobalConstant.AWSConstant;
import com.choic11.GlobalConstant.FileUploadConstant;
import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.Util;
import com.choic11.awss3.S3Helper;
import com.choic11.awss3.S3Helper.S3HelperListener;
import com.choic11.model.BaseRequest;
import com.choic11.model.TblTemplate;
import com.choic11.model.basketball.TblBasketballMatch;
import com.choic11.model.basketball.TblBasketballMatchPlayersStat;
import com.choic11.model.basketball.TblBasketballSeries;
import com.choic11.model.response.BaseResponse;
import com.choic11.repository.basketball.CronRepository;
import com.choic11.service.CustomerService;
import com.choic11.smtp.SmtpUtil;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.XvfbConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service("BasketballCronService")
public class CronService {

    @Autowired
    CronRepository cronRepository;

    @Autowired
    EntitySportsService entitySportsService;

    @Autowired
    CustomerService customerService;

    @Autowired
    MatchesService matchesService;

    @Autowired
    ContestService contestService;

    @Autowired
    TeamsService teamsService;

    public BaseResponse matchProgressCron() {
        LinkedHashMap<Integer, Object> matchProgressCron = cronRepository.matchProgressCron();
        if (matchProgressCron.isEmpty()) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            return new BaseResponse(0, false, "Matches sifted to live", matchProgressCron);
        }

    }

    public BaseResponse distributeAffiliatePercentageCron(int matchUniqueId) {
        LinkedHashMap<Integer, Object> distributeAffiliatePercentageCron = cronRepository
                .distributeAffiliatePercentageCron(matchUniqueId);
        if (distributeAffiliatePercentageCron.isEmpty()) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            startDistributeAffiliatePercentage(distributeAffiliatePercentageCron.values());
            return new BaseResponse(0, false, "Matches Affiliate percent distributed",
                    distributeAffiliatePercentageCron);
        }

    }

    private void startDistributeAffiliatePercentage(Collection<Object> matches) {

        AsyncConfiguration.getAffiliatePerDistributeexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    for (Object object : matches) {
                        LinkedHashMap<String, Object> match = (LinkedHashMap<String, Object>) object;
                        int matchUniqueId = (int) match.get("uniqueId");

                        boolean distributeAffiliatePercentage = cronRepository.distributeAffiliatePercentage(match);
                        boolean distributeCustomerAffiliatePercentage = cronRepository
                                .distributeCustomerAffiliatePercentage(match);

                        if (distributeAffiliatePercentage && distributeCustomerAffiliatePercentage) {
                            cronRepository.updateIsDisAffiPerOnMatch(null, matchUniqueId, "Y");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public BaseResponse matchLineupMannual(BaseRequest baseRequest) {
        List<Integer> players = new ArrayList<Integer>();
        List<Integer> substitute = new ArrayList<Integer>();

        try {
            String playersString = baseRequest.getParam("players");
            if (!Util.isEmpty(playersString)) {
                String[] split = playersString.split(",");
                for (String playerUniqueId : split) {
                    players.add(Integer.parseInt(playerUniqueId));
                }
            }
        } catch (Exception e) {
            players.clear();
        }
        try {
            String substituteString = baseRequest.getParam("substitute");
            if (!Util.isEmpty(substituteString)) {
                String[] split = substituteString.split(",");
                for (String playerUniqueId : split) {
                    substitute.add(Integer.parseInt(playerUniqueId));
                }
            }
        } catch (Exception e) {
            substitute.clear();
        }

        if (players.isEmpty()) {
            return new BaseResponse(0, true, "Please add players for lineup", null);
        }

        if (players.size() < 4) {
            return new BaseResponse(0, true, "Please add minimum 4 players for lineup", null);
        }

        int match_unique_id = Integer.parseInt(baseRequest.getParam("match_unique_id"));
        LinkedHashMap<Integer, Object> matchProgressCron = cronRepository.matchLineupCron(match_unique_id);
        if (matchProgressCron.isEmpty()) {
            return new BaseResponse(0, true, "No match found.", null);
        } else {
            for (Object data : matchProgressCron.values()) {
                LinkedHashMap<String, Object> match = (LinkedHashMap<String, Object>) data;

                LinkedHashMap<String, Object> matchLineup = new LinkedHashMap<String, Object>();
                matchLineup.put("players", players);
                matchLineup.put("substitute", substitute);

                match.put("matchLineup", matchLineup);
                match.put("fromMannual", true);

                getAndUpdateMatchLineup(match);
            }
            return new BaseResponse(0, false, "Matches lineup updated", matchProgressCron);
        }
    }

    public BaseResponse matchLineupCron(int match_unique_id) {
        LinkedHashMap<Integer, Object> matchProgressCron = cronRepository.matchLineupCron(match_unique_id);
        if (matchProgressCron.isEmpty()) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            for (Object data : matchProgressCron.values()) {
                LinkedHashMap<String, Object> match = (LinkedHashMap<String, Object>) data;
                match.put("fromMannual", false);
                getAndUpdateMatchLineup(match);

            }
            return new BaseResponse(0, false, "Matches lineup updated", matchProgressCron);
        }
    }

    public BaseResponse matchAbondantContestCron(int match_unique_id) {
        LinkedHashMap<Integer, Object> matchAbondantContestCron = cronRepository
                .matchAbondantContestCron(match_unique_id);
        if (matchAbondantContestCron.isEmpty()) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            for (Object data : matchAbondantContestCron.values()) {
                LinkedHashMap<String, Object> match = (LinkedHashMap<String, Object>) data;
                startAbondantContestForMatch(match);
            }
            return new BaseResponse(0, false, "Matches lineup updated", matchAbondantContestCron);
        }
    }

    public BaseResponse liveMatchUpdateRankingCron(int match_unique_id) {
        List<HashMap<String, Object>> liveMatchUpdateRankingCron = cronRepository
                .liveMatchUpdateRankingCron(match_unique_id);
        if (liveMatchUpdateRankingCron.size() == 0) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            for (HashMap<String, Object> match : liveMatchUpdateRankingCron) {
                LinkedHashMap<String, Object> pointSystemForGameTypeId = cronRepository
                        .getPointSystemForGameTypeId((int) match.get("gameTypeId"));
                JSONObject playersForMatchUniqueId = cronRepository
                        .getPlayersForMatchUniqueId((int) match.get("uniqueId"));

                if (pointSystemForGameTypeId.size() == 0 || playersForMatchUniqueId.isEmpty()) {
                    cronRepository.updateMatchPointsUpdatedAt((int) match.get("uniqueId"), null, null);
                } else {
                    startRankUpdatingForMatch(match, pointSystemForGameTypeId, playersForMatchUniqueId);
                }

            }
            return new BaseResponse(0, false, "Matches ranking updated start", liveMatchUpdateRankingCron);
        }
    }

    public BaseResponse distributeReferralCashbonusCron(int matchUniqueId) {
        List<HashMap<String, Object>> distributeReferralCashbonusCron = cronRepository
                .distributeReferralCashbonusCron(matchUniqueId);
        if (distributeReferralCashbonusCron.size() == 0) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            HashMap<String, Object> hashMap = distributeReferralCashbonusCron.get(0);

            matchUniqueId = (int) hashMap.get("uniqueId");

            TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(matchUniqueId);
            if (matchDataOnly == null) {
                return new BaseResponse(0, true, "No match found.", null);
            }

            if (!matchDataOnly.isCompletedMatch()) {
                return new BaseResponse(0, true, "Result not declared.", null);
            }

            if (matchDataOnly.isMatchResultRunning()) {
                return new BaseResponse(0, true, "Result in running please wait.", null);
            }

            if (matchDataOnly.isReferralDistributed()) {
                return new BaseResponse(0, true, "Already distributted.", null);
            }

            if (matchDataOnly.isReferralDistributedRunning()) {
                return new BaseResponse(0, true, "Already distributted running.", null);
            }
            HashMap<String, String> referCashbonus = customerService.getReferCashbonus();
            if (referCashbonus != null && !referCashbonus.isEmpty()) {
                int updateMatchReferralDistributed = cronRepository
                        .updateMatchReferralDistributed(matchDataOnly.getUniqueId(), "R");
                if (updateMatchReferralDistributed > 0) {
                    startReferralDistributeForMatch(matchDataOnly, referCashbonus);
                }
            } else {
                cronRepository.updateMatchReferralDistributed(matchDataOnly.getUniqueId(), "Y");
            }
            return new BaseResponse(0, false, "Match referral distribute start... ", matchDataOnly);
        }
    }

    public BaseResponse distributeAffiliateAmountCron(int matchUniqueId) {
        List<HashMap<String, Object>> distributeReferralCashbonusCron = cronRepository
                .distributeAffiliateAmountCron(matchUniqueId);
        if (distributeReferralCashbonusCron.size() == 0) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            HashMap<String, Object> hashMap = distributeReferralCashbonusCron.get(0);
            matchUniqueId = (int) hashMap.get("uniqueId");
            TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(matchUniqueId);
            if (matchDataOnly == null) {
                return new BaseResponse(0, true, "No match found.", null);
            }

            if (!matchDataOnly.isCompletedMatch()) {
                return new BaseResponse(0, true, "Result not declared.", null);
            }

            if (matchDataOnly.isMatchResultRunning()) {
                return new BaseResponse(0, true, "Result in running please wait.", null);
            }

            if (!matchDataOnly.isReferralDistributed()) {
                return new BaseResponse(0, true, "Referral not distributted please wait..", null);
            }

            if (matchDataOnly.isReferralDistributedRunning()) {
                return new BaseResponse(0, true, "Referral distributing running please wait..", null);
            }

            if (matchDataOnly.isAffiliateDistributed()) {
                return new BaseResponse(0, true, "Already distributted.", null);
            }

            if (matchDataOnly.isAffiliateDistributedRunning()) {
                return new BaseResponse(0, true, "Already distributted running.", null);
            }
            int updateMatchAffiliateDistributed = cronRepository
                    .updateMatchAffiliateDistributed(matchDataOnly.getUniqueId(), "R");
            if (updateMatchAffiliateDistributed > 0) {
                startAffiliateDistributeForMatch(matchDataOnly);
            }

            return new BaseResponse(0, false, "Match affiliate distribute start... ", matchDataOnly);
        }
    }

    public BaseResponse generateMatchLeaderboardCron(int matchUniqueId) {
        List<HashMap<String, Object>> generateMatchLeaderboardCron = cronRepository
                .generateMatchLeaderboardCron(matchUniqueId);
        if (generateMatchLeaderboardCron.size() == 0) {
            return new BaseResponse(0, false, "No match found.", null);
        } else {
            HashMap<String, Object> hashMap = generateMatchLeaderboardCron.get(0);

            matchUniqueId = (int) hashMap.get("uniqueId");
            TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(matchUniqueId);
            if (matchDataOnly == null) {
                return new BaseResponse(0, true, "No match found.", null);
            }

            if (!matchDataOnly.isCompletedMatch()) {
                return new BaseResponse(0, true, "Result not declared.", null);
            }

            if (matchDataOnly.isMatchResultRunning()) {
                return new BaseResponse(0, true, "Result is running please wait.", null);
            }

            if (!matchDataOnly.isReferralDistributed()) {
                return new BaseResponse(0, true, "Referral not distributted please wait..", null);
            }

            if (matchDataOnly.isReferralDistributedRunning()) {
                return new BaseResponse(0, true, "Referral distributing running please wait..", null);
            }

            if (matchDataOnly.isLeaderboardCreated()) {
                return new BaseResponse(0, true, "Already created.", null);
            }

            if (matchDataOnly.isLeaderboardCreatedRunning()) {
                return new BaseResponse(0, true, "Already leaderboard running.", null);
            }
            int updateMatchLeaderboardCreated = cronRepository
                    .updateMatchLeaderboardCreated(matchDataOnly.getUniqueId(), "R");
            if (updateMatchLeaderboardCreated > 0) {
                startLeaderboardCreatingForMatch(matchDataOnly);
            }

            return new BaseResponse(0, false, "Match leaderboard creation start... ", matchDataOnly);
        }
    }

    public BaseResponse declareMatchResult(int match_unique_id) {

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchData(match_unique_id);

        if (matchDataOnly == null) {
            return new BaseResponse(0, true, "No match found.", null);
        }

        if (matchDataOnly.isPastMatch()) {
            return new BaseResponse(0, true, "Action is already taken on match.", null);
        }

        if (matchDataOnly.isCompletedMatch()) {
            return new BaseResponse(0, true, "Result already declared.", null);
        }

        if (!matchDataOnly.isInreviewMatch()) {
            return new BaseResponse(0, true, "Match is not in review.", null);
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(matchDataOnly);

        int updateMatchprogress = cronRepository.updateMatchResultStart(match_unique_id, "R");

        if (updateMatchprogress > 0) {
            startResultDeclareForMatch(matchDataOnly, modifiedMatchData);
        }
        return new BaseResponse(0, false, "Result declared successfully", matchDataOnly);

    }

    public BaseResponse declareMatchResultTest(int match_unique_id) {

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchData(match_unique_id);

        if (matchDataOnly == null) {
            return new BaseResponse(0, true, "No match found.", null);
        }

        LinkedHashMap<String, Object> modifiedMatchData = matchesService.getModifiedMatchData(matchDataOnly);

        startResultDeclareForMatchTest(matchDataOnly, modifiedMatchData);

        return new BaseResponse(0, false, "Result declared successfully", matchDataOnly);

    }

    public BaseResponse declareSeriesLeaderboardResult(int seriesId) {

        TblBasketballSeries tblBasketballSeries = cronRepository.getSeriesData(seriesId);

        if (tblBasketballSeries == null) {
            return new BaseResponse(0, true, "No series found.", null);
        }

        if (tblBasketballSeries.isResultDeclared()) {
            return new BaseResponse(0, true, "Result already declared.", null);
        }

        if (tblBasketballSeries.isResultRunning()) {
            return new BaseResponse(0, true, "Result already in running condition.", null);
        }


        int updateSeriesProgress = cronRepository.updateSeriesLeaderboardResultStart(seriesId, "R");

        if (updateSeriesProgress > 0) {
            startSeriesLeaderboardResultDeclareForMatch(tblBasketballSeries);
        }
        return new BaseResponse(0, false, "Result declared successfully", null);

    }


    public BaseResponse abodentMatch(int match_unique_id) {

        TblBasketballMatch matchDataOnly = matchesService.getMatchesRepository().getMatchDataOnly(match_unique_id);

        if (matchDataOnly == null) {
            return new BaseResponse(0, true, "No match found.", null);
        }

        if (matchDataOnly.isPastMatch()) {
            return new BaseResponse(0, true, "Action is already taken on match.", null);
        }

        if (matchDataOnly.isAboundantMatch()) {
            return new BaseResponse(0, true, "Aboundant already.", null);
        }

        if (!matchDataOnly.isInreviewMatch()) {
            return new BaseResponse(0, true, "Match is not in review.", null);
        }

        int updateMatchprogress = cronRepository.updateMatchResultStart(match_unique_id, "AB");

        if (updateMatchprogress > 0) {
            startAboundantDeclareForMatch(matchDataOnly);
        }
        return new BaseResponse(0, false, "Match Aboundant successfully", matchDataOnly);

    }

    public BaseResponse abondantMatchContest(int matchContestId) {

        HashMap<String, Object> contestData = cronRepository.getMatchContestAdminAbondant(matchContestId);

        if (contestData == null) {
            return new BaseResponse(0, true, "Invalid Contest.", null);
        }

        String isAbondant = (String) contestData.get("isAbondant");
        if (!isAbondant.equals("N")) {
            return new BaseResponse(0, true, "Contest already abondant.", null);
        }

        AsyncConfiguration.getContestAbondantexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    HashMap<String, Object> abodentContest = cronRepository.abodentContest(contestData);
                    if (abodentContest != null && !abodentContest.isEmpty()
                            && abodentContest.containsKey("completed")) {
                        int matchContestId = (int) contestData.get("id");
                        int matchUniqueId = (int) contestData.get("matchUniqueId");
                        cronRepository.updateCustomerMatchAbondantContestInfo(matchUniqueId, matchContestId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return new BaseResponse(0, false, "Contest Abondant successfully.", null);
    }

    public BaseResponse getPlayerDetailCron() {
        List<HashMap<String, Object>> playerDetailCron = cronRepository.getPlayerDetailCron();

        if (playerDetailCron == null || playerDetailCron.size() == 0) {
            return new BaseResponse(0, true, "No player found for fetch detail", null);
        }

        getAndUpdatePlayerDetail(playerDetailCron);
        return new BaseResponse(0, false, "Player detail fetch starting", playerDetailCron);
    }

    public BaseResponse updateNewAvailableMatchCountCron() {
        LinkedHashMap<String, Object> upcomingMatchesBySeries = entitySportsService.getUpcomingMatchesBySeries(null,
                "0");
        if (upcomingMatchesBySeries.isEmpty()) {
            return new BaseResponse(0, true, "No latest match found from entity", null);
        }

        LinkedHashMap<String, Object> ourSystemRunningMatches = cronRepository.getOurSystemRunningMatches();

        Set<String> keySet = upcomingMatchesBySeries.keySet();

        List<LinkedHashMap<String, Object>> newMatchAvailable = new ArrayList<LinkedHashMap<String, Object>>();
        List<LinkedHashMap<String, Object>> matchTimeChanged = new ArrayList<LinkedHashMap<String, Object>>();

        for (String matchUniqueId : keySet) {

            LinkedHashMap<String, Object> entityMatch = (LinkedHashMap<String, Object>) upcomingMatchesBySeries
                    .get(matchUniqueId);

            if (ourSystemRunningMatches.containsKey(matchUniqueId)) {

                LinkedHashMap<String, Object> ourMatch = (LinkedHashMap<String, Object>) ourSystemRunningMatches
                        .get(matchUniqueId);

                long ourMatchTime = (long) ourMatch.get("matchDate");

                long time = Long.parseLong(String.valueOf(entityMatch.get("timestamp_start")));

                long diff = Math.abs(ourMatchTime - time);
                if (diff >= 62) {
                    entityMatch.put("updatedMatchTime", time);
                    matchTimeChanged.add(entityMatch);
                }

            } else {
                newMatchAvailable.add(entityMatch);
            }
        }

        cronRepository.updateNewAvailableMatchCount(newMatchAvailable, matchTimeChanged);

        LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("newMatchAvailable", newMatchAvailable);
        output.put("matchTimeChanged", matchTimeChanged);

        return new BaseResponse(0, false, "latest match found", output);
    }

    public BaseResponse generateContestPdfCron(int matchContestId) {
        List<HashMap<String, Object>> generateContestPdfCron = cronRepository.generateContestPdfCron(matchContestId);
        if (generateContestPdfCron == null || generateContestPdfCron.size() == 0) {
            return new BaseResponse(0, true, "No Contest found for pdf generate.", null);
        }
        HashMap<String, String> output = new HashMap<String, String>();
        for (HashMap<String, Object> contest : generateContestPdfCron) {
            int selectedMatchContestId = (int) contest.get("id");
            int matchUniqueId = (int) contest.get("matchUniqueId");
            cronRepository.updateContestPdfProgress(selectedMatchContestId, "Y", null);
            String matchName = (String) contest.get("matchName");
            float totalPrice = (float) contest.get("totalPrice");
            float entryFees = (float) contest.get("entryFees");
            String slug = (String) contest.get("slug");
            List<JSONObject> teams = (List<JSONObject>) contest.get("teams");
            JSONObject pdfData = new JSONObject();
            pdfData.put("pdfLogo", FileUploadConstant.CONTEST_PDF_LOGO_URL);
            pdfData.put("matchName", matchName);
            pdfData.put("pricePool", String.valueOf(totalPrice));
            pdfData.put("entryFees", String.valueOf(entryFees));
            pdfData.put("contestSlug", slug);
            pdfData.put("teams", teams);
            ContestPdf contestPdf = new ContestPdf(pdfData);
            LinkedHashMap<String, Object> generatePdfhtml = contestPdf.generatePdfhtml();
            String error = (String) generatePdfhtml.get("error");
            if (!Util.isEmpty(error)) {
                output.put(String.valueOf(selectedMatchContestId), error);
            } else {
                output.put(String.valueOf(selectedMatchContestId), "Pdf generated.");
                String Pdfhtml = (String) generatePdfhtml.get("html");

                String pdfname = GlobalConstant.PROJECTTYPE + "_" + matchUniqueId + "_" + selectedMatchContestId
                        + "_basketball.pdf";
                generateContestPdfAndSave(selectedMatchContestId, Pdfhtml, pdfname);
            }
        }
        return new BaseResponse(0, false, "Contestes pdf generating strat...", output);
    }

    public void generateContestPdfAndSave(int selectedMatchContestId, String Pdfhtml, String pdfFileName) {
        if (Util.isEmpty(Pdfhtml)) {
            return;
        }

        AsyncConfiguration.getContestPdfexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    XvfbConfig xc = new XvfbConfig();
                    xc.addParams(new Param("--auto-servernum"), new Param("--server-num=1"));

                    String findExecutable = WrapperConfig.findExecutable();
                    WrapperConfig wc = new WrapperConfig(findExecutable);
                    wc.setXvfbConfig(xc);
                    Pdf pdf = new Pdf(wc);
                    pdf.addParam(new Param("--no-outline"), new Param("--orientation", "landscape"),
                            new Param("--margin-left", "1mm"), new Param("--margin-right", "1mm"),
                            new Param("--margin-top", "1mm"), new Param("--margin-bottom", "1mm"),
                            new Param("--enable-javascript"));
                    pdf.addPageFromString(Pdfhtml);
                    pdf.setAllowMissingAssets();
                    String outputFilePath = GlobalConstant.getContestPdfLocalDirectory() + pdfFileName;
                    pdf.saveAs(outputFilePath);
                    final File newPdfFile = new File(outputFilePath);
                    if (newPdfFile.exists()) {
                        HashMap<String, String> awsConstant = AWSConstant.getAwsConstant();
                        String bucketPath = awsConstant.get("AWS_BUCKET") + "/" + FileUploadConstant.PDF_PATH;
                        if (bucketPath.endsWith("/")) {
                            bucketPath = bucketPath.substring(0, bucketPath.length() - 1);
                        }
                        S3Helper.uploadFileToS3Bucket(bucketPath, newPdfFile.getName(), newPdfFile,
                                new S3HelperListener() {

                                    @Override
                                    public void onFileUploadComplete(boolean success, String message) {
                                        if (success) {
                                            cronRepository.updateContestPdfProgress(selectedMatchContestId, "S",
                                                    newPdfFile.getName());
                                            try {
                                                newPdfFile.delete();
                                            } catch (Exception ignore) {

                                            }
                                        }

                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getAndUpdateMatchLineup(LinkedHashMap<String, Object> match) {

        if (match == null) {
            return;
        }

        AsyncConfiguration.getLineUpexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    boolean fromMannual = (boolean) match.get("fromMannual");
                    int matchUniqueId = (int) match.get("uniqueId");
                    String playerUniqueIds = match.get("playerUniqueIds").toString().trim();
                    String subsituteUniqueIds = match.get("subsituteUniqueIds").toString().trim();

                    LinkedHashMap<String, Object> matchLineup = null;
                    if (fromMannual) {
                        matchLineup = (LinkedHashMap<String, Object>) match.get("matchLineup");
                    } else {
                        JSONObject playersForMatchUniqueId = cronRepository.getPlayersForMatchUniqueId(matchUniqueId);
                        matchLineup = entitySportsService.getMatchLineup(null, String.valueOf(matchUniqueId), playersForMatchUniqueId);
                    }

                    List<Integer> players = (List<Integer>) matchLineup.get("players");
                    List<Integer> substitute = (List<Integer>) matchLineup.get("substitute");
                    if (players.size() >= 4) {
                        List<Integer> oldPlayers = new ArrayList<Integer>();
                        List<Integer> oldSubstitute = new ArrayList<Integer>();
                        if (!playerUniqueIds.isEmpty()) {
                            String[] split = playerUniqueIds.split(",");
                            for (String value : split) {
                                oldPlayers.add(Integer.parseInt(value));
                            }
                        }

                        if (!subsituteUniqueIds.isEmpty()) {
                            String[] split2 = subsituteUniqueIds.split(",");
                            for (String value : split2) {
                                oldSubstitute.add(Integer.parseInt(value));
                            }
                        }

                        String newPlayerUniqueIds = "";
                        String newSubsituteUniqueIds = "";

                        List<Integer> playersCopy = new ArrayList<Integer>(players);
                        List<Integer> substituteCopy = new ArrayList<Integer>(substitute);

                        for (Integer value : playersCopy) {
                            if (!newPlayerUniqueIds.isEmpty()) {
                                newPlayerUniqueIds += ",";
                            }
                            newPlayerUniqueIds += String.valueOf(value);
                        }

                        for (Integer value : substituteCopy) {
                            if (!newSubsituteUniqueIds.isEmpty()) {
                                newSubsituteUniqueIds += ",";
                            }
                            newSubsituteUniqueIds += String.valueOf(value);
                        }

                        players.removeAll(oldPlayers);
                        oldPlayers.removeAll(playersCopy);

                        substitute.removeAll(oldSubstitute);
                        oldSubstitute.removeAll(substituteCopy);

                        boolean updateMatchLineup = cronRepository.updateMatchLineup(matchUniqueId,
                                players, oldPlayers, substitute, oldSubstitute, playerUniqueIds, newPlayerUniqueIds,
                                newSubsituteUniqueIds, fromMannual);

                        if (updateMatchLineup && playerUniqueIds.isEmpty()) {
                            cronRepository.saveLineupNotificationForCustomers(matchUniqueId);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void getAndUpdatePlayerDetail(List<HashMap<String, Object>> playerDetailCron) {
        if (playerDetailCron == null) {
            return;
        }

        for (HashMap<String, Object> hashMap : playerDetailCron) {
            AsyncConfiguration.getPlayerDetailexecutor().submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        String message = "SUCCESS";

                        int playerUniqueId = (int) hashMap.get("uniqueId");

                        LinkedHashMap<String, Object> playerDetail = entitySportsService.playerDetail(null, String.valueOf(playerUniqueId));

                        if (playerDetail != null && !playerDetail.isEmpty()) {

                            boolean updatePlayerDetail = cronRepository.updatePlayerDetail(playerUniqueId, playerDetail);

                            message = updatePlayerDetail ? playerUniqueId + " SUCCESS" : playerUniqueId + " FAILED";

                        } else {
                            message = playerUniqueId + " NOT FOUND";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void startAbondantContestForMatch(LinkedHashMap<String, Object> match) {
        if (match == null) {
            return;
        }

        AsyncConfiguration.getContestAbondantexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    int matchUniqueId = (int) match.get("uniqueId");

                    int updateMatchContestAbondant = cronRepository.updateMatchContestAbondant(matchUniqueId, "R");
                    if (updateMatchContestAbondant > 0) {
                        List<HashMap<String, Object>> matchContestForAbondant = cronRepository
                                .getMatchContestForAbondant(matchUniqueId);
                        boolean isAllSuccess = true;
                        for (HashMap<String, Object> contest : matchContestForAbondant) {
                            boolean recalculateContestPrize = recalculateContestPrize(contest);
                            if (!recalculateContestPrize) {
                                isAllSuccess = false;
                            }
                        }
                        if (isAllSuccess) {
                            cronRepository.updateMatchContestAbondant(matchUniqueId, "Y");
                            cronRepository.updateCustomerMatchAbondantContestInfo(matchUniqueId, 0);
                            generatePlayerSelectedCount(matchUniqueId);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startRankUpdatingForMatch(HashMap<String, Object> match,
                                          LinkedHashMap<String, Object> pointSystemForGameTypeId, JSONObject playersForMatchUniqueId) {
        if (match == null) {
            return;
        }

        AsyncConfiguration.getContestRankexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    int matchUniqueId = (int) match.get("uniqueId");

                    JSONObject pointSystems = generatePointSystems(pointSystemForGameTypeId);
                    JSONObject matchFantasySummary = entitySportsService.getMatchFantasySummary(String.valueOf(matchUniqueId), pointSystems,
                            playersForMatchUniqueId);

                    String matchProgress = null;
                    String matchCompletedStatus = null;
                    JSONObject scorecard_data = null;
                    if (matchFantasySummary.has("scorecard_data")) {
                        scorecard_data = matchFantasySummary.getJSONObject("scorecard_data");
                    }
                    if (matchFantasySummary.has("match_completed_status")) {
                        matchCompletedStatus = matchFantasySummary.getString("match_completed_status");
                    }
                    if (matchFantasySummary.has("man_of_the_match")) {
                        JSONObject man_of_the_match = (JSONObject) matchFantasySummary.get("man_of_the_match");
                        if (man_of_the_match != null && !man_of_the_match.isEmpty()) {
                            matchProgress = "IR";
                        }
                    }
                    cronRepository.updateMatchPointsUpdatedAt(matchUniqueId, matchProgress,
                            matchCompletedStatus);
                    cronRepository.updateMatchScoreCard(matchUniqueId, scorecard_data);

                    JSONObject players = (JSONObject) matchFantasySummary.get("players");
                    if (players != null && !players.isEmpty()) {

                        LinkedHashMap<String, TblBasketballMatchPlayersStat> playersStatsForMatchUniqueId = cronRepository
                                .getPlayersStatsForMatchUniqueId(matchUniqueId);

                        boolean needUpdateRanking = cronRepository.saveOrUpdatePlayerStats(matchUniqueId,
                                players, playersStatsForMatchUniqueId, playersForMatchUniqueId);

                        Util.printLog("needUpdateRanking", needUpdateRanking + "");
                        if (needUpdateRanking) {
                            List<HashMap<String, Object>> matchContestForRanking = cronRepository
                                    .getMatchContestForRanking(matchUniqueId);

                            if (matchContestForRanking.size() > 0) {

                                for (HashMap<String, Object> contest : matchContestForRanking) {
                                    cronRepository.updateMatchContestRanking(contest, playersForMatchUniqueId);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startReferralDistributeForMatch(TblBasketballMatch matchDataOnly, HashMap<String, String> referCashbonus) {

        if (matchDataOnly == null) {
            return;
        }

        AsyncConfiguration.getReferralCashBonusexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    boolean distributeReferralCashbonusCron = cronRepository.startReferralDistributeForMatch(matchDataOnly,
                            referCashbonus);
                    if (distributeReferralCashbonusCron) {
                        cronRepository.updateMatchReferralDistributed(matchDataOnly.getUniqueId(), "Y");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void startAffiliateDistributeForMatch(TblBasketballMatch matchDataOnly) {
        if (matchDataOnly == null) {
            return;
        }
        AsyncConfiguration.getAffiliateDistributeexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    boolean startAffiliateDistributeForMatch = cronRepository
                            .startAffiliateDistributeForMatch(matchDataOnly);
                    boolean startCustomerAffiliateDistributeForMatch = false;
                    if (startAffiliateDistributeForMatch) {
                        startCustomerAffiliateDistributeForMatch = cronRepository
                                .startCustomerAffiliateDistributeForMatch(matchDataOnly);
                    }
                    if (startAffiliateDistributeForMatch && startCustomerAffiliateDistributeForMatch) {
                        cronRepository.updateMatchAffiliateDistributed(matchDataOnly.getUniqueId(), "Y");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startLeaderboardCreatingForMatch(TblBasketballMatch matchDataOnly) {
        if (matchDataOnly == null) {
            return;
        }
        AsyncConfiguration.getLeaderboardCreatedexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    boolean startLeaderboardCreatingForMatch = cronRepository
                            .startLeaderboardCreatingForMatch(matchDataOnly);
                    if (startLeaderboardCreatingForMatch) {
                        cronRepository.updateMatchLeaderboardCreated(matchDataOnly.getUniqueId(), "Y");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void startAboundantDeclareForMatch(TblBasketballMatch matchDataOnly) {
        if (matchDataOnly == null) {
            return;
        }

        AsyncConfiguration.getContestResultexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    List<HashMap<String, Object>> matchContestForResult = cronRepository
                            .getMatchContestForResult(matchDataOnly.getUniqueId());

                    boolean allResultDeclread = true;
                    for (HashMap<String, Object> contest : matchContestForResult) {

                        contest.put("matchName", matchDataOnly.getName());
                        contest.put("fromMatchAB", true);

                        HashMap<String, Object> abodentContest = cronRepository.abodentContest(contest);
                        if (abodentContest == null || abodentContest.isEmpty()
                                || !abodentContest.containsKey("completed")) {
                            allResultDeclread = false;
                        }
                    }
                    if (allResultDeclread) {
                        cronRepository.updateMatchResultCompleted(matchDataOnly.getUniqueId());
                        cronRepository.updateCustomerMatchAbondantContestInfo(matchDataOnly.getUniqueId(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startResultDeclareForMatch(TblBasketballMatch matchDataOnly,
                                           LinkedHashMap<String, Object> modifiedMatchData) {
        if (matchDataOnly == null) {
            return;
        }

        AsyncConfiguration.getContestResultexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    List<Integer> generateDreamTeamForMatch = generateDreamTeamForMatch(matchDataOnly.getUniqueId(),
                            matchDataOnly.getTeam1Id(), matchDataOnly.getTeam2Id());

                    if (generateDreamTeamForMatch != null && generateDreamTeamForMatch.size() > 0) {
                        cronRepository.updateMatchDreamTeamPlayers(matchDataOnly.getUniqueId(),
                                generateDreamTeamForMatch);
                    }

                    HashMap<String, Object> totalTaxPercent = customerService.getTotalTaxPercent();

                    List<HashMap<String, Object>> matchContestForResult = cronRepository
                            .getMatchContestForResult(matchDataOnly.getUniqueId());

                    boolean allResultDeclread = true;
                    TblTemplate winningTamplate = null;
                    if (matchDataOnly.isContestWinMailActive()) {
                        winningTamplate = customerService.getCustomerRepository().getTemplate("E", "win_contest");

                        String content = winningTamplate.getContent();

                        content = content.replace("{APP_LOGO}", GlobalConstant.WIN_MAIL_LOGO);
                        content = content.replace("{MATCH_DATE}", matchDataOnly.getMatchFormattedDateOnly());
                        content = content.replace("{SERIES_NAME}",
                                ((LinkedHashMap<String, Object>) modifiedMatchData.get("series")).get("name").toString());
                        content = content.replace("{TEAM1_LOGO}",
                                ((LinkedHashMap<String, Object>) modifiedMatchData.get("team1")).get("image").toString());
                        content = content.replace("{TEAM2_LOGO}",
                                ((LinkedHashMap<String, Object>) modifiedMatchData.get("team2")).get("image").toString());

                        winningTamplate.setContent(content);
                    }

                    for (HashMap<String, Object> contest : matchContestForResult) {

                        contest.put("matchName", matchDataOnly.getName());

                        HashMap<String, Object> saveResultForContest = cronRepository.saveResultForContest(contest,
                                totalTaxPercent, winningTamplate);
                        if (saveResultForContest == null || saveResultForContest.isEmpty()
                                || !saveResultForContest.containsKey("completed")) {
                            allResultDeclread = false;
                        }

                    }
                    if (allResultDeclread) {
                        cronRepository.updateMatchResultCompleted(matchDataOnly.getUniqueId());
                        updatePlayerPointsInSeries(matchDataOnly.getSeriesId(), matchDataOnly.getGameTypeId());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startResultDeclareForMatchTest(TblBasketballMatch matchDataOnly,
                                               LinkedHashMap<String, Object> modifiedMatchData) {
        if (matchDataOnly == null) {
            return;
        }


        HashMap<String, Object> totalTaxPercent = customerService.getTotalTaxPercent();

        List<HashMap<String, Object>> matchContestForResult = cronRepository
                .getMatchContestForResult(matchDataOnly.getUniqueId());

        boolean allResultDeclread = true;
        TblTemplate winningTamplate = null;
        if (matchDataOnly.isContestWinMailActive()) {
            winningTamplate = customerService.getCustomerRepository().getTemplate("E", "win_contest");

            String content = winningTamplate.getContent();

            content = content.replace("{APP_LOGO}", GlobalConstant.WIN_MAIL_LOGO);
            content = content.replace("{MATCH_DATE}", matchDataOnly.getMatchFormattedDateOnly());
            content = content.replace("{SERIES_NAME}",
                    ((LinkedHashMap<String, Object>) modifiedMatchData.get("series")).get("name").toString());
            content = content.replace("{TEAM1_LOGO}",
                    ((LinkedHashMap<String, Object>) modifiedMatchData.get("team1")).get("image").toString());
            content = content.replace("{TEAM2_LOGO}",
                    ((LinkedHashMap<String, Object>) modifiedMatchData.get("team2")).get("image").toString());

            winningTamplate.setContent(content);
        }

        for (HashMap<String, Object> contest : matchContestForResult) {

            contest.put("matchName", matchDataOnly.getName());

            HashMap<String, Object> saveResultForContest = cronRepository.saveResultForContest(contest,
                    totalTaxPercent, winningTamplate);
            if (saveResultForContest == null || saveResultForContest.isEmpty()
                    || !saveResultForContest.containsKey("completed")) {
                allResultDeclread = false;
            }

        }
    }

    public void startSeriesLeaderboardResultDeclareForMatch(TblBasketballSeries tblBasketballSeries) {
        if (tblBasketballSeries == null) {
            return;
        }

        AsyncConfiguration.getSeriesLeaderboardResultExecutor().submit(new Runnable() {

            @Override
            public void run() {


                HashMap<String, Object> totalTaxPercent = customerService.getTotalTaxPercent();

                boolean allResultDeclread = true;
                TblTemplate winningTamplate = null;
                winningTamplate = customerService.getCustomerRepository().getTemplate("E", "win_leaderboard");

                String content = winningTamplate.getContent();

                content = content.replace("{APP_LOGO}", GlobalConstant.WIN_MAIL_LOGO);
                content = content.replace("{SERIES_NAME}",tblBasketballSeries.getName());

                winningTamplate.setContent(content);

                HashMap<String, Object> saveResultForSeries = cronRepository.saveResultForSeriesLeaderboard(tblBasketballSeries,
                        totalTaxPercent, winningTamplate);
                if (saveResultForSeries == null || saveResultForSeries.isEmpty()
                        || !saveResultForSeries.containsKey("completed")) {
                    allResultDeclread = false;
                }

//                if (allResultDeclread) {
//                }
            }
        });
    }

    public void updatePlayerPointsInSeries(int seriesId, int gameTypeId) {
        cronRepository.updatePlayerPointsInSeries(seriesId, gameTypeId);
    }

    public List<Integer> generateDreamTeamForMatch(int uniqueId, int team1Id, int team2Id) {
        LinkedHashMap<String, Object> teamSettings = matchesService.getMatchesRepository().getTeamSettings();

        int MAX_PLAYERS_PER_TEAM = (int) Float.parseFloat(teamSettings.get("MAX_PLAYERS_PER_TEAM").toString());
        float MAX_CREDITS = Float.parseFloat(teamSettings.get("MAX_CREDITS").toString());
        int MIN_WICKETKEEPER = (int) Float.parseFloat(teamSettings.get("MIN_WICKETKEEPER").toString());
        int MAX_WICKETKEEPER = (int) Float.parseFloat(teamSettings.get("MAX_WICKETKEEPER").toString());
        int MIN_BATSMAN = (int) Float.parseFloat(teamSettings.get("MIN_BATSMAN").toString());
        int MAX_BATSMAN = (int) Float.parseFloat(teamSettings.get("MAX_BATSMAN").toString());
        int MIN_ALLROUNDER = (int) Float.parseFloat(teamSettings.get("MIN_ALLROUNDER").toString());
        int MAX_ALLROUNDER = (int) Float.parseFloat(teamSettings.get("MAX_ALLROUNDER").toString());
        int MIN_BOWLER = (int) Float.parseFloat(teamSettings.get("MIN_BOWLER").toString());
        int MAX_BOWLER = (int) Float.parseFloat(teamSettings.get("MAX_BOWLER").toString());
        int MIN_CENTER = (int) Float.parseFloat(teamSettings.get("MIN_CENTER").toString());
        int MAX_CENTER = (int) Float.parseFloat(teamSettings.get("MAX_CENTER").toString());

        List<HashMap<String, Object>> playersForDreamTeam = cronRepository
                .getPlayersForDreamTeam(uniqueId);
        if (playersForDreamTeam != null && !playersForDreamTeam.isEmpty()) {
            List<HashMap<String, Object>> wicketkeapers = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> batsmans = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> allrounders = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> bowlers = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> centers = new ArrayList<HashMap<String, Object>>();

            List<JSONObject> dream_team_globle = new ArrayList<JSONObject>();
            List<JSONObject> unselectedPlayers = new ArrayList<JSONObject>();
            List<JSONObject> team1unselectedPlayers = new ArrayList<JSONObject>();
            List<JSONObject> team2unselectedPlayers = new ArrayList<JSONObject>();
            List<JSONObject> JSONObject = new ArrayList<JSONObject>();

            int selectedBatsmanCount = 0;
            int selectedBowlerCount = 0;
            int selectedCenterCount = 0;
            int selectedAllrounderCount = 0;
            int selectedWicketkeapersCount = 0;

            int team1SelectedPlayerCount = 0;
            int team2SelectedPlayerCount = 0;

            float dream_team_globle_credit_points = 0;

            for (HashMap<String, Object> hashMap : playersForDreamTeam) {

                String playingRole = (String) hashMap.get("playingRole");
                if (playingRole != null && !playingRole.trim().isEmpty()) {
                    playingRole = playingRole.trim().toLowerCase();

                    int playerTeamId = (int) hashMap.get("teamId");
                    float playerCredits = (float) hashMap.get("credits");

                    if (playingRole.equals("point guard")) {
                        wicketkeapers.add(hashMap);

                        if (selectedWicketkeapersCount < MAX_WICKETKEEPER) {

                            if (playerTeamId == team1Id) {
                                if (team1SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team1SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedWicketkeapersCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team1unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            } else {
                                if (team2SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team2SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedWicketkeapersCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team2unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            }

                        } else {
                            unselectedPlayers.add(new JSONObject(hashMap));
                            if (playerTeamId == team1Id) {
                                team1unselectedPlayers.add(new JSONObject(hashMap));

                            } else {
                                team2unselectedPlayers.add(new JSONObject(hashMap));
                            }
                        }

                    }
                    else if (playingRole.equals("shooting guard")) {

                        batsmans.add(hashMap);

                        if (selectedBatsmanCount < MAX_BATSMAN) {

                            if (playerTeamId == team1Id) {
                                if (team1SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team1SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedBatsmanCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team1unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            } else {
                                if (team2SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team2SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedBatsmanCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team2unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            }

                        } else {
                            unselectedPlayers.add(new JSONObject(hashMap));
                            if (playerTeamId == team1Id) {
                                team1unselectedPlayers.add(new JSONObject(hashMap));

                            } else {
                                team2unselectedPlayers.add(new JSONObject(hashMap));
                            }
                        }

                    }
                    else if (playingRole.equals("small forward")) {

                        allrounders.add(hashMap);

                        if (selectedAllrounderCount < MAX_ALLROUNDER) {

                            if (playerTeamId == team1Id) {
                                if (team1SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team1SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedAllrounderCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team1unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            } else {
                                if (team2SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team2SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedAllrounderCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team2unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            }

                        } else {
                            unselectedPlayers.add(new JSONObject(hashMap));
                            if (playerTeamId == team1Id) {
                                team1unselectedPlayers.add(new JSONObject(hashMap));

                            } else {
                                team2unselectedPlayers.add(new JSONObject(hashMap));
                            }
                        }

                    }
                    else if (playingRole.equals("power forward")) {
                        bowlers.add(hashMap);

                        if (selectedBowlerCount < MAX_BOWLER) {

                            if (playerTeamId == team1Id) {
                                if (team1SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team1SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedBowlerCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team1unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            } else {
                                if (team2SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team2SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedBowlerCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team2unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            }

                        } else {
                            unselectedPlayers.add(new JSONObject(hashMap));
                            if (playerTeamId == team1Id) {
                                team1unselectedPlayers.add(new JSONObject(hashMap));

                            } else {
                                team2unselectedPlayers.add(new JSONObject(hashMap));
                            }
                        }
                    } else if (playingRole.equals("center")) {
                        centers.add(hashMap);

                        if (selectedCenterCount < MAX_CENTER) {

                            if (playerTeamId == team1Id) {
                                if (team1SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team1SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedCenterCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team1unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            } else {
                                if (team2SelectedPlayerCount < MAX_PLAYERS_PER_TEAM) {
                                    team2SelectedPlayerCount++;
                                    dream_team_globle.add(new JSONObject(hashMap));
                                    dream_team_globle_credit_points += playerCredits;
                                    selectedCenterCount++;
                                } else {
                                    unselectedPlayers.add(new JSONObject(hashMap));
                                    team2unselectedPlayers.add(new JSONObject(hashMap));
                                }
                            }

                        } else {
                            unselectedPlayers.add(new JSONObject(hashMap));
                            if (playerTeamId == team1Id) {
                                team1unselectedPlayers.add(new JSONObject(hashMap));

                            } else {
                                team2unselectedPlayers.add(new JSONObject(hashMap));
                            }
                        }
                    }
                }

            }

            if (!dream_team_globle.isEmpty()) {

                Collections.sort(dream_team_globle, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        float valA = a.getFloat("points");
                        float valB = b.getFloat("points");

                        if (valA == valB) {
                            return 0;
                        } else if (valA < valB) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                while (dream_team_globle.size() > 8) {

                    for (int i = 0; i < dream_team_globle.size(); i++) {

                        JSONObject jsonObject = dream_team_globle.get(i);

                        String playingRole = (String) jsonObject.get("playingRole");

                        int playerTeamId = jsonObject.getInt("teamId");
                        float playerCredits = jsonObject.getFloat("credits");

                        playingRole = playingRole.trim().toLowerCase();
                        int minCount = 0;
                        if (playingRole.equals("point guard")) {
                            minCount = MIN_WICKETKEEPER;
                        } else if (playingRole.equals("shooting guard")) {
                            minCount = MIN_BATSMAN;
                        } else if (playingRole.equals("small forward")) {
                            minCount = MIN_ALLROUNDER;
                        } else if (playingRole.equals("power forward")) {
                            minCount = MIN_BOWLER;
                        } else if (playingRole.equals("center")) {
                            minCount = MIN_CENTER;
                        }
                        boolean checkMinConditionForTeam = checkMinConditionForTeam(dream_team_globle, playingRole,
                                minCount);
                        if (checkMinConditionForTeam) {
                            JSONObject remove = dream_team_globle.remove(i);
                            unselectedPlayers.add(remove);
                            if (playerTeamId == team1Id) {
                                team1SelectedPlayerCount--;
                                team1unselectedPlayers.add(remove);
                            } else {
                                team2SelectedPlayerCount--;
                                team2unselectedPlayers.add(remove);
                            }
                            dream_team_globle_credit_points -= playerCredits;
                            break;
                        }

                    }

                }

                List<Integer> dreamPlayers = new ArrayList<Integer>();
                for (JSONObject player : dream_team_globle) {
                    dreamPlayers.add(player.getInt("playerUniqueId"));
                }

                return dreamPlayers;

            }

        }

        return null;

    }

    private boolean checkMinConditionForTeam(List<JSONObject> dream_team_globle, String playingRole, int minCount) {

        int existPlayerType = 0;
        for (JSONObject player : dream_team_globle) {
            String playingRole1 = (String) player.get("playingRole");
            playingRole1 = playingRole1.trim().toLowerCase();
            if (playingRole.equals(playingRole1)) {
                existPlayerType++;
            }
        }
        return existPlayerType >= minCount;

    }

    public BaseResponse checkContestCompress(HashMap<String, Object> contest) {
        HashMap<String, Object> compressContest = cronRepository.compressContest(contest, true);

        return new BaseResponse(0, false, "testing", compressContest);

    }

    public boolean recalculateContestPrize(HashMap<String, Object> contest) {

        long clashcount = (long) contest.get("clashcount");
        int totalTeams = (int) contest.get("totalTeam");
        int joinedTeams = (int) contest.get("totalJoinedTeam");
        float compressionAllowPercentage = (float) contest.get("compressionAllowPercentage");
        float confirmWinContestPercentage = (float) contest.get("confirmWinContestPercentage");

        float joinedTeamPer = ((float) joinedTeams / totalTeams) * 100;

        String confirmWin = (String) contest.get("confirmWin");
        String isCompressionAllow = (String) contest.get("isCompressionAllow");


        if (joinedTeams <= 1) {
            HashMap<String, Object> abodentContest = cronRepository.abodentContest(contest);
            if (abodentContest.containsKey("completed")) {
                return true;
            } else {
                return false;
            }
        }

        if (confirmWin.equals("N") && joinedTeamPer < confirmWinContestPercentage) {
            if (isCompressionAllow.equals("N")) {
                HashMap<String, Object> abodentContest = cronRepository.abodentContest(contest);
                if (abodentContest.containsKey("completed")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (joinedTeamPer < compressionAllowPercentage) {
                    HashMap<String, Object> abodentContest = cronRepository.abodentContest(contest);
                    if (abodentContest.containsKey("completed")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    HashMap<String, Object> compressContest = cronRepository.compressContest(contest, false);
                    if (compressContest.containsKey("completed")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

//        if (clashcount == 1) {
//            contest.put("fromClash", true);
//            HashMap<String, Object> abodentContest = cronRepository.abodentContest(contest);
//            if (abodentContest.containsKey("completed")) {
//                return true;
//            } else {
//                return false;
//            }
//        }

        if (confirmWin.equals("Y") && joinedTeamPer < confirmWinContestPercentage) {
            if (isCompressionAllow.equals("N")) {
                return true;
            } else {
                HashMap<String, Object> compressContest = cronRepository.compressContest(contest, false);
                if (compressContest.containsKey("completed")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public BaseResponse sendLineupNotificationCron() {
        List<HashMap<String, Object>> matchLineup = cronRepository.sendLineupNotificationCron();

        if (matchLineup == null || matchLineup.size() == 0) {
            return new BaseResponse(0, false, "No Matches lineup notification found", null);
        }

        List<Integer> needDeletedIds = new ArrayList<>();
        for (HashMap<String, Object> hashMap : matchLineup) {
            int id = (int) hashMap.get("id");
            needDeletedIds.add(id);
        }
        cronRepository.deleteLineupNotificationMulti(needDeletedIds);

        customerService.sendNotificationAndSaveCron(matchLineup);
        return new BaseResponse(0, false, "Matches lineup send cron", matchLineup);

    }

    public BaseResponse sendMailCron() {
        List<HashMap<String, Object>> emails = cronRepository.sendMailCron();

        if (emails == null || emails.size() == 0) {
            return new BaseResponse(0, false, "No mail found", null);
        }
        List<Integer> needDeletedIds = new ArrayList<>();
        for (HashMap<String, Object> hashMap : emails) {
            int id = (int) hashMap.get("id");
            needDeletedIds.add(id);
        }
        cronRepository.deleteEmailCronMulti(needDeletedIds);
        AsyncConfiguration.getEmailSendexecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    for (HashMap<String, Object> hashMap : emails) {
                        int id = (int) hashMap.get("id");
                        String subject = (String) hashMap.get("subject");
                        String content = (String) hashMap.get("message");
                        String toEmail = (String) hashMap.get("toemail");
                        String toName = (String) hashMap.get("toname");

                        SmtpUtil.sendSmtpmail(subject, content, toEmail, toName);

                        cronRepository.deleteEmailCron(id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return new BaseResponse(0, false, "Basketball email send cron", emails);
    }

    public JSONObject generatePointSystems(LinkedHashMap<String, Object> pointSystemForGameTypeId) {

        JSONObject game_type_point = new JSONObject();

        if (pointSystemForGameTypeId.containsKey("Points Scored")) {
            game_type_point.put("Points_Scored",
                    Float.parseFloat(pointSystemForGameTypeId.get("Points Scored").toString()));
        } else {
            game_type_point.put("Points_Scored", 0f);
        }

        if (pointSystemForGameTypeId.containsKey("Rebounds")) {
            game_type_point.put("Rebounds",
                    Float.parseFloat(pointSystemForGameTypeId.get("Rebounds").toString()));
        } else {
            game_type_point.put("Rebounds", 0f);
        }

        if (pointSystemForGameTypeId.containsKey("Assists")) {
            game_type_point.put("Assists",
                    Float.parseFloat(pointSystemForGameTypeId.get("Assists").toString()));
        } else {
            game_type_point.put("Assists", 0f);
        }

        if (pointSystemForGameTypeId.containsKey("Steals")) {
            game_type_point.put("Steals",
                    Float.parseFloat(pointSystemForGameTypeId.get("Steals").toString()));
        } else {
            game_type_point.put("Steals", 0f);
        }

        if (pointSystemForGameTypeId.containsKey("Blocks")) {
            game_type_point.put("Blocks",
                    Float.parseFloat(pointSystemForGameTypeId.get("Blocks").toString()));
        } else {
            game_type_point.put("Blocks", 0f);
        }

        if (pointSystemForGameTypeId.containsKey("Turn Overs")) {
            game_type_point.put("Turn_Overs",
                    Float.parseFloat(pointSystemForGameTypeId.get("Turn Overs").toString()));
        } else {
            game_type_point.put("Turn_Overs", 0f);
        }

        return game_type_point;

    }

    public BaseResponse createCustomerTeamJoinContestAdmin(BaseRequest baseRequest) {

        int customerId = Integer.parseInt(baseRequest.getParam("user_id"));
        int customerTeamId = Integer.parseInt(baseRequest.getParam("customer_team_id"));
        String isUpdate = baseRequest.getParam("is_update");

        if (isUpdate.equals("Y") && customerTeamId <= 0) {
            return new BaseResponse(0, true, "customer_team_id required for is_update=Y", null);
        }

        LinkedHashMap<String, Object> customerProfileData = customerService.getCustomerRepository()
                .getCustomerProfileData(customerId);
        String isFake = (String) customerProfileData.get("isFake");
        String isAdmin = (String) customerProfileData.get("isAdmin");

        if (isFake.equals("0")) {
            return new BaseResponse(0, true, "Invalid Customer", null);
        }
        baseRequest.authUserId = customerId;
        baseRequest.setParam("fromadmin", "1");

        if (isUpdate.equals("Y")) {
            return teamsService.updateCustomerTeam(baseRequest);
        }

        int matchContestId = Integer.parseInt(baseRequest.getParam("match_contest_id"));

        float contestEntryFee = 0;

        HashMap<String, Object> contestDetailMini = contestService.getContestRepository()
                .contestDetailMini(matchContestId);
        if (contestDetailMini == null) {
            return new BaseResponse(0, true, "Contest detail not found.", null);
        } else if (((String) contestDetailMini.get("isBeatTheExpert")).equals("Y")
                && ((int) contestDetailMini.get("teamId") > 0) && (isAdmin.equals("1")) && (isUpdate.equals("N"))) {
            return new BaseResponse(0, true, "Expert Team already created you can update team only.", null);
        }

        if (((String) contestDetailMini.get("isBeatTheExpert")).equals("Y")) {
            contestEntryFee = (float) contestDetailMini.get("entryFees");
        }

        if (customerTeamId == 0) {
            BaseResponse createCustomerTeam = teamsService.createCustomerTeam(baseRequest);
            if (createCustomerTeam.isError()) {
                return createCustomerTeam;
            }
            LinkedHashMap<String, Object> newCustomerTeam = (LinkedHashMap<String, Object>) createCustomerTeam
                    .getData();
            customerTeamId = (int) newCustomerTeam.get("id");
        }
        if (customerTeamId == 0) {
            return new BaseResponse(0, true, "somthing went wrong.", null);
        }
        baseRequest.setParam("customer_team_id", String.valueOf(customerTeamId));

        if (contestEntryFee > 0) {
            baseRequest.setParam("entry_fees", String.valueOf(contestEntryFee));
        }

        BaseResponse customerJoinContestMulti = contestService.customerJoinContestMulti(baseRequest);

        if (isAdmin.equals("1") && ((String) contestDetailMini.get("isBeatTheExpert")).equals("Y")) {
            contestService.updateBeatTheExpertTeam(matchContestId, customerTeamId);
        }

        return customerJoinContestMulti;

    }

    public BaseResponse updateCustomerMatchAbondantContestInfo(int matchUniqueId, int matchContestId) {
        cronRepository.updateCustomerMatchAbondantContestInfo(matchUniqueId, matchContestId);
        return new BaseResponse(0, false, "done", null);
    }

    public BaseResponse generatePlayerSelectedCount(int match_unique_id){
        List<HashMap<String, Object>> hashMaps = cronRepository.generatePlayerSelectedCount(match_unique_id);
        return new BaseResponse(0, false, "Player Selected Updated.", hashMaps);
    }

}
