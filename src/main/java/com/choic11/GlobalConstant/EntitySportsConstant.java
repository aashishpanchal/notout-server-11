package com.choic11.GlobalConstant;

public class EntitySportsConstant {

    public static final String ENTITY_CRICKET_TOKEN = "69612b9145af012df4cf6cc49e44f3f4";
    public static final String ENTITY_CRICKET_MATCHES = "https://rest.entitysport.com/v2/matches/?status=1&per_page=100&pre_squad=true&token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_MATCH_SQUAD_ROSTER = "https://rest.entitysport.com/v2/competitions/{SERIES_ID}/squads/{MATCH_ID}?token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_MATCH_LINEUP = "https://rest.entitysport.com/v2/matches/{MATCH_ID}/squads?token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_PLAYER_FINDER = "https://rest.entitysport.com/v2/players?per_page=500&search={PLAYER_NAME}&token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_PLAYER_DETAIL = "https://rest.entitysport.com/v2/players/{PLAYER_ID}?token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_MATCH_SCOREBOARD = "https://rest.entitysport.com/v2/matches/{MATCH_ID}/scorecard?token="
            + ENTITY_CRICKET_TOKEN;
    public static final String ENTITY_CRICKET_MATCH_SCOREBOARD_LIVE = "https://rest.entitysport.com/v2/matches/{MATCH_ID}/live?token="
            + ENTITY_CRICKET_TOKEN;



    public static final String ENTITY_SOCCER_TOKEN = "5f472d377a46083d77f8d212da5406a4";
    public static final String ENTITY_SOCCER_MATCHES = "https://soccer.entitysport.com/matches?order=asc&status=1&per_page=500&pre_squad=true&token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_MATCH_SQUAD_ROSTER = "https://soccer.entitysport.com/matches/{MATCH_ID}/newfantasy?token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_MATCH_LINEUP = "https://soccer.entitysport.com/matches/{MATCH_ID}/info?token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_PLAYER_FINDER = "https://soccer.entitysport.com/players?search={PLAYER_NAME}&token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_PLAYER_DETAIL = "https://soccer.entitysport.com/player/{PLAYER_ID}/profile?token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_MATCH_SCOREBOARD = "https://soccer.entitysport.com/matches/{MATCH_ID}/newfantasy?token="
            + ENTITY_SOCCER_TOKEN;
    public static final String ENTITY_SOCCER_MATCH_SCOREBOARD_LIVE = "https://rest.entitysport.com/v2/matches/{MATCH_ID}/live?token="
            + ENTITY_SOCCER_TOKEN;

    public static final String ENTITY_BASKETBALL_TOKEN = "b2c1a14273c9c5b90687eb7e96250f9a";
    public static final String ENTITY_BASKETBALL_MATCHES = "https://basketball.entitysport.com/matches?order=asc&status=1&per_page=500&token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_MATCH_SQUAD_ROSTER = "https://basketball.entitysport.com/matches/{MATCH_ID}/fantasysquad?token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_MATCH_LINEUP = "https://basketball.entitysport.com/matches/{MATCH_ID}/info?token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_PLAYER_FINDER = "https://basketball.entitysport.com/players?per_page=50&search={PLAYER_NAME}&token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_PLAYER_DETAIL = "https://basketball.entitysport.com/player/{PLAYER_ID}/profile?token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_MATCH_SCOREBOARD = "https://basketball.entitysport.com/matches/{MATCH_ID}/stats?token="
            + ENTITY_BASKETBALL_TOKEN;
    public static final String ENTITY_BASKETBALL_MATCH_SCOREBOARD_LIVE = "https://rest.entitysport.com/v2/matches/{MATCH_ID}/live?token="
            + ENTITY_BASKETBALL_TOKEN;
}
