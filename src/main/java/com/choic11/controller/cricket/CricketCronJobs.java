package com.choic11.controller.cricket;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.service.cricket.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CricketCronJobs {

	@Autowired
	CronService cronService;

	@Scheduled(fixedDelay = 13 * 60 * 1000)
	public void updateNewAvailableMatchCountCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.updateNewAvailableMatchCountCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void getPlayerDetailCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.getPlayerDetailCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void matchProgressCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.matchProgressCron();
		}
	}

	@Scheduled(fixedDelay = 10 * 1000)
	public void matchScorecardCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.matchScorecardCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchLineupCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.matchLineupCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendLineupNotificationCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.sendLineupNotificationCron();
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendMailCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.sendMailCron();
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void distributeAffiliatePercentageCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.distributeAffiliatePercentageCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchAbondantContestCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.matchAbondantContestCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void generateContestPdfCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.generateContestPdfCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void liveMatchUpdateRankingCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.liveMatchUpdateRankingCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void distributeReferralCashbonusCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.distributeReferralCashbonusCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void distributeAffiliateAmountCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.distributeAffiliateAmountCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void generateMatchLeaderboardCron() {
		if (GlobalConstant.isCronEnabled()) {
			cronService.generateMatchLeaderboardCron(0);
		}
	}

}
