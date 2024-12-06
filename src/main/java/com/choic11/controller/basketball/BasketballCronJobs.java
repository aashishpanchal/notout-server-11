package com.choic11.controller.basketball;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.service.basketball.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BasketballCronJobs {

	@Autowired
	CronService cronService;

	@Scheduled(fixedDelay = 13 * 60 * 1000)
	public void updateNewAvailableMatchCountCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.updateNewAvailableMatchCountCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void getPlayerDetailCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.getPlayerDetailCron();
		}

	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void matchProgressCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.matchProgressCron();
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchLineupCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.matchLineupCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendLineupNotificationCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.sendLineupNotificationCron();
		}
	}

	@Scheduled(fixedDelay = 5 * 1000)
	public void sendMailCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.sendMailCron();
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void distributeAffiliatePercentageCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.distributeAffiliatePercentageCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void matchAbondantContestCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.matchAbondantContestCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void generateContestPdfCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.generateContestPdfCron(0);
		}
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void liveMatchUpdateRankingCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.liveMatchUpdateRankingCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void distributeReferralCashbonusCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.distributeReferralCashbonusCron(0);
		}
	}

	@Scheduled(fixedDelay = 5 * 60 * 1000)
	public void distributeAffiliateAmountCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.distributeAffiliateAmountCron(0);
		}
	}

	@Scheduled(fixedDelay = 3 * 60 * 1000)
	public void generateMatchLeaderboardCronBasketball() {
		if (GlobalConstant.isCronEnabledBasketball()) {
			cronService.generateMatchLeaderboardCron(0);
		}
	}

}
