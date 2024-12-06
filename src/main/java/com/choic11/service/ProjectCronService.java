package com.choic11.service;

import com.choic11.AsyncConfiguration;
import com.choic11.repository.AffiliateLeaderboardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("ProjectCronService")
public class ProjectCronService {

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

    @Autowired
    AffiliateLeaderboardRepository affiliateLeaderboardRepository;

    @Transactional(readOnly = true)
    public void updateAffiliateLeaderboardRank(int leaderboardId) {
        List<HashMap<String, Object>> leaderboardList = new ArrayList<>();
        if (leaderboardId == 0) {
            String hqlQuery = "SELECT talb.id as id FROM TblAffiliateLeaderboard talb WHERE talb.status =:status AND talb.isDeleted =:isDeleted AND talb.resultDeclared =:resultDeclared";
            Query queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("status", "A");
            queryObj.setParameter("isDeleted", "N");
            queryObj.setParameter("resultDeclared", "N");
            leaderboardList = (List<HashMap<String, Object>>) queryObj.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        } else {
            String hqlQuery = "SELECT talb.id as id FROM TblAffiliateLeaderboard talb WHERE talb.id =:id AND talb.status =:status AND talb.isDeleted =:isDeleted AND talb.resultDeclared =:resultDeclared";
            Query queryObj = getSession().createQuery(hqlQuery);
            queryObj.setParameter("status", "A");
            queryObj.setParameter("isDeleted", "N");
            queryObj.setParameter("resultDeclared", "N");
            queryObj.setParameter("id", leaderboardId);
            leaderboardList = (List<HashMap<String, Object>>) queryObj.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        }

        if (leaderboardList.size() > 0) {
            final List<HashMap<String, Object>> finalList = leaderboardList;
            AsyncConfiguration.updateAffiliateLeaderboardRankExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (HashMap<String, Object> data : finalList) {
                            int id = (int) data.get("id");
                            affiliateLeaderboardRepository.updateAffiliateLeaderboardRank(id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
