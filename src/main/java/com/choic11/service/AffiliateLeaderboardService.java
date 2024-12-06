package com.choic11.service;

import com.choic11.model.BaseRequest;
import com.choic11.model.response.BaseResponse;
import com.choic11.repository.AffiliateLeaderboardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AffiliateLeaderboardService {

    @Autowired
    AffiliateLeaderboardRepository affiliateLeaderboardRepository;

    @Autowired
    private SessionFactory factory;

    private Session getNewSession() {

        return factory.openSession();

    }

    private Session getSession() {
        Session session = factory.getCurrentSession();
        if (session == null) {
            return factory.openSession();
        }
        return session;
    }

    public BaseResponse getAffiliateLeaderboard(BaseRequest baseRequest){
        BaseResponse baseResponse = new BaseResponse(0,false,"Leaderboard data",affiliateLeaderboardRepository.getAffiliateLeaderboard(baseRequest));
        return baseResponse;
    }

    public BaseResponse getResultForAffiliateLeaderboardDeposit(BaseRequest baseRequest) throws Exception {
        affiliateLeaderboardRepository.getResultForAffiliateLeaderboard(baseRequest);
        BaseResponse baseResponse = new BaseResponse(0,false,"Result declared successfully",null);
        return baseResponse;
    }

}
