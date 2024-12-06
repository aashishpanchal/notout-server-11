package com.choic11.controller.soccer;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.service.soccer.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SoccerCronJobs {

	@Autowired
	CronService cronService;

	@Scheduled(fixedDelay = 13 * 60 * 1000)
	public void updateNewAvailableMatchCountCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.updateNewAvailableMatchCountCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void getPlayerDetailCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.getPlayerDetailCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void matchProgressCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.matchProgressCron();
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchLineupCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.matchLineupCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendLineupNotificationCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.sendLineupNotificationCron();
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendMailCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.sendMailCron();
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void distributeAffiliatePercentageCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.distributeAffiliatePercentageCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchAbondantContestCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.matchAbondantContestCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void generateContestPdfCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.generateContestPdfCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void liveMatchUpdateRankingCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.liveMatchUpdateRankingCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void distributeReferralCashbonusCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.distributeReferralCashbonusCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void distributeAffiliateAmountCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.distributeAffiliateAmountCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void generateMatchLeaderboardCronSoccer() {
		if (GlobalConstant.isCronEnabledSoccer()) {
			cronService.generateMatchLeaderboardCron(0);
		}
	}

}
