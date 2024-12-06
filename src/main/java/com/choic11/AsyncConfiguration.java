package com.choic11;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AsyncConfiguration {

	static ThreadPoolTaskExecutor smsSendexecutor;
	static ThreadPoolTaskExecutor emailSendexecutor;
	static ThreadPoolTaskExecutor notificationSendexecutor;

	static ThreadPoolTaskExecutor lineUpexecutor;
	static ThreadPoolTaskExecutor scoreBoardexecutor;

	static ThreadPoolTaskExecutor contestAbondantexecutor;
	static ThreadPoolTaskExecutor contestResultexecutor;
	static ThreadPoolTaskExecutor contestRankexecutor;
	static ThreadPoolTaskExecutor contestPdfexecutor;

	static ThreadPoolTaskExecutor referralCashBonusexecutor;

	static ThreadPoolTaskExecutor affiliateDistributeexecutor;
	static ThreadPoolTaskExecutor affiliatePerDistributeexecutor;

	static ThreadPoolTaskExecutor leaderboardCreatedexecutor;
	static ThreadPoolTaskExecutor playerDetailexecutor;

	static ThreadPoolTaskExecutor updateAffiliateLeaderboardRankExecutor;

	static ThreadPoolTaskExecutor seriesLeaderboardResultExecutor;

	static {
		smsSendexecutor = new ThreadPoolTaskExecutor();
		smsSendexecutor.setCorePoolSize(10);
		smsSendexecutor.setMaxPoolSize(10);
		smsSendexecutor.setQueueCapacity(100);
		smsSendexecutor.setThreadNamePrefix("SmsThread-");
		smsSendexecutor.initialize();

		emailSendexecutor = new ThreadPoolTaskExecutor();
		emailSendexecutor.setCorePoolSize(10);
		emailSendexecutor.setMaxPoolSize(10);
		emailSendexecutor.setQueueCapacity(100);
		emailSendexecutor.setThreadNamePrefix("EmailThread-");
		emailSendexecutor.initialize();

		notificationSendexecutor = new ThreadPoolTaskExecutor();
		notificationSendexecutor.setCorePoolSize(10);
		notificationSendexecutor.setMaxPoolSize(10);
		notificationSendexecutor.setQueueCapacity(100);
		notificationSendexecutor.setThreadNamePrefix("NotificationThread-");
		notificationSendexecutor.initialize();

		lineUpexecutor = new ThreadPoolTaskExecutor();
		lineUpexecutor.setCorePoolSize(3);
		lineUpexecutor.setMaxPoolSize(10);
		lineUpexecutor.setQueueCapacity(100);
		lineUpexecutor.setThreadNamePrefix("LineupThread-");
		lineUpexecutor.initialize();

		playerDetailexecutor = new ThreadPoolTaskExecutor();
		playerDetailexecutor.setCorePoolSize(3);
		playerDetailexecutor.setMaxPoolSize(10);
		playerDetailexecutor.setQueueCapacity(100);
		playerDetailexecutor.setThreadNamePrefix("playerDetailThread-");
		playerDetailexecutor.initialize();

		scoreBoardexecutor = new ThreadPoolTaskExecutor();
		scoreBoardexecutor.setCorePoolSize(3);
		scoreBoardexecutor.setMaxPoolSize(10);
		scoreBoardexecutor.setQueueCapacity(100);
		scoreBoardexecutor.setThreadNamePrefix("ScoreboardThread-");
		scoreBoardexecutor.initialize();

		contestAbondantexecutor = new ThreadPoolTaskExecutor();
		contestAbondantexecutor.setCorePoolSize(1);
		contestAbondantexecutor.setMaxPoolSize(1);
		contestAbondantexecutor.setQueueCapacity(100);
		contestAbondantexecutor.setThreadNamePrefix("AbondantThread-");
		contestAbondantexecutor.initialize();

		contestResultexecutor = new ThreadPoolTaskExecutor();
		contestResultexecutor.setCorePoolSize(1);
		contestResultexecutor.setMaxPoolSize(1);
		contestResultexecutor.setQueueCapacity(100);
		contestResultexecutor.setThreadNamePrefix("ResultThread-");
		contestResultexecutor.initialize();

		contestRankexecutor = new ThreadPoolTaskExecutor();
		contestRankexecutor.setCorePoolSize(1);
		contestRankexecutor.setMaxPoolSize(1);
		contestRankexecutor.setQueueCapacity(100);
		contestRankexecutor.setThreadNamePrefix("RankThread-");
		contestRankexecutor.initialize();

		contestPdfexecutor = new ThreadPoolTaskExecutor();
		contestPdfexecutor.setCorePoolSize(1);
		contestPdfexecutor.setMaxPoolSize(1);
		contestPdfexecutor.setQueueCapacity(100);
		contestPdfexecutor.setThreadNamePrefix("ContestPdf-");
		contestPdfexecutor.initialize();

		referralCashBonusexecutor = new ThreadPoolTaskExecutor();
		referralCashBonusexecutor.setCorePoolSize(1);
		referralCashBonusexecutor.setMaxPoolSize(1);
		referralCashBonusexecutor.setQueueCapacity(100);
		referralCashBonusexecutor.setThreadNamePrefix("ReferralThread-");
		referralCashBonusexecutor.initialize();

		affiliateDistributeexecutor = new ThreadPoolTaskExecutor();
		affiliateDistributeexecutor.setCorePoolSize(1);
		affiliateDistributeexecutor.setMaxPoolSize(1);
		affiliateDistributeexecutor.setQueueCapacity(100);
		affiliateDistributeexecutor.setThreadNamePrefix("AffiliateThread-");
		affiliateDistributeexecutor.initialize();

		affiliatePerDistributeexecutor = new ThreadPoolTaskExecutor();
		affiliatePerDistributeexecutor.setCorePoolSize(1);
		affiliatePerDistributeexecutor.setMaxPoolSize(1);
		affiliatePerDistributeexecutor.setQueueCapacity(100);
		affiliatePerDistributeexecutor.setThreadNamePrefix("AffiliatePerThread-");
		affiliatePerDistributeexecutor.initialize();

		leaderboardCreatedexecutor = new ThreadPoolTaskExecutor();
		leaderboardCreatedexecutor.setCorePoolSize(1);
		leaderboardCreatedexecutor.setMaxPoolSize(1);
		leaderboardCreatedexecutor.setQueueCapacity(100);
		leaderboardCreatedexecutor.setThreadNamePrefix("LeaderboardThread-");
		leaderboardCreatedexecutor.initialize();

		updateAffiliateLeaderboardRankExecutor = new ThreadPoolTaskExecutor();
		updateAffiliateLeaderboardRankExecutor.setCorePoolSize(1);
		updateAffiliateLeaderboardRankExecutor.setMaxPoolSize(1);
		updateAffiliateLeaderboardRankExecutor.setQueueCapacity(100);
		updateAffiliateLeaderboardRankExecutor.setThreadNamePrefix("AffLeaderboardThread-");
		updateAffiliateLeaderboardRankExecutor.initialize();

		seriesLeaderboardResultExecutor = new ThreadPoolTaskExecutor();
		seriesLeaderboardResultExecutor.setCorePoolSize(1);
		seriesLeaderboardResultExecutor.setMaxPoolSize(1);
		seriesLeaderboardResultExecutor.setQueueCapacity(100);
		seriesLeaderboardResultExecutor.setThreadNamePrefix("SeriesLeaderboardResultThread-");
		seriesLeaderboardResultExecutor.initialize();

	}

	public static ThreadPoolTaskExecutor getContestPdfexecutor() {
		return contestPdfexecutor;
	}

	public static ThreadPoolTaskExecutor getPlayerDetailexecutor() {
		return playerDetailexecutor;
	}

	public static ThreadPoolTaskExecutor getSmsSendexecutor() {
		return smsSendexecutor;
	}

	public static ThreadPoolTaskExecutor getEmailSendexecutor() {
		return emailSendexecutor;
	}

	public static ThreadPoolTaskExecutor getNotificationSendexecutor() {
		return notificationSendexecutor;
	}

	public static ThreadPoolTaskExecutor getLineUpexecutor() {
		return lineUpexecutor;
	}

	public static ThreadPoolTaskExecutor getScoreBoardexecutor() {
		return scoreBoardexecutor;
	}

	public static ThreadPoolTaskExecutor getContestAbondantexecutor() {
		return contestAbondantexecutor;
	}

	public static ThreadPoolTaskExecutor getContestResultexecutor() {
		return contestResultexecutor;
	}

	public static ThreadPoolTaskExecutor getContestRankexecutor() {
		return contestRankexecutor;
	}

	public static ThreadPoolTaskExecutor getReferralCashBonusexecutor() {
		return referralCashBonusexecutor;
	}

	public static ThreadPoolTaskExecutor getAffiliateDistributeexecutor() {
		return affiliateDistributeexecutor;
	}

	public static ThreadPoolTaskExecutor getAffiliatePerDistributeexecutor() {
		return affiliatePerDistributeexecutor;
	}

	public static ThreadPoolTaskExecutor getLeaderboardCreatedexecutor() {
		return leaderboardCreatedexecutor;
	}

	public static ThreadPoolTaskExecutor updateAffiliateLeaderboardRankExecutor() {
		return updateAffiliateLeaderboardRankExecutor;
	}

	public static ThreadPoolTaskExecutor getSeriesLeaderboardResultExecutor() {
		return seriesLeaderboardResultExecutor;
	}

}
