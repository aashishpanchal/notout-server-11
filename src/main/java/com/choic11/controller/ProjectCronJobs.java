package com.choic11.controller;

import com.choic11.GlobalConstant.GlobalConstant;
import com.choic11.service.ProjectCronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProjectCronJobs {

	@Autowired
	ProjectCronService projectCronService;

	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void updateAffiliateLeaderboardRank() {
		if (GlobalConstant.isCronenabledforproject()) {
			projectCronService.updateAffiliateLeaderboardRank(0);
		}

	}

}
