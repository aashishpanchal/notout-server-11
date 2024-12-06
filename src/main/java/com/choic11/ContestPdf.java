package com.choic11;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class ContestPdf {

	JSONObject pdfData;

	public ContestPdf(JSONObject pdfData) {
		this.pdfData = pdfData;
	}

	public LinkedHashMap<String, Object> generatePdfhtml() {
		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("error", "");
		if (pdfData == null || pdfData.isEmpty()) {
			output.put("error", "pdfData is null");
			return output;
		}
		JSONArray teams = pdfData.optJSONArray("teams");
		String pdfLogo = pdfData.optString("pdfLogo");
		String matchName = pdfData.optString("matchName");
		String pricePool = pdfData.optString("pricePool");
		String entryFees = pdfData.optString("entryFees");
		String contestSlug = pdfData.optString("contestSlug");
		String totalJoinedTeams = String.valueOf(teams.length());
		if (teams == null || teams.length() == 0) {
			output.put("error", "teams length is 0");
			return output;
		}

		if (Util.isEmpty(pdfLogo)) {
			output.put("error", "pdfLogo not found");
			return output;
		}

		if (Util.isEmpty(matchName)) {
			output.put("error", "matchName not found");
			return output;
		}

		if (Util.isEmpty(pricePool)) {
			output.put("error", "pricePool not found");
			return output;
		}

		if (Util.isEmpty(entryFees)) {
			output.put("error", "entryFees not found");
			return output;
		}

		if (Util.isEmpty(contestSlug)) {
			output.put("error", "contestSlug not found");
			return output;
		}

		String message = "<table width=\"100%\" cellspacing=\"0\" border=\"0\">"
				+ "<colgroup span=\"2\" width=\"147\"></colgroup>" + "<colgroup width=\"150\"></colgroup>"
				+ "<colgroup width=\"147\"></colgroup>" + "<colgroup width=\"150\"></colgroup>"
				+ "<colgroup width=\"147\"></colgroup>" + "<colgroup width=\"150\"></colgroup>"
				+ "<colgroup width=\"147\"></colgroup>" + "<colgroup width=\"150\"></colgroup>"
				+ "<colgroup span=\"2\" width=\"147\"></colgroup>" + "<colgroup width=\"150\"></colgroup>" + "<tr>"
				+ "<td style=\"border-bottom: 1px solid #000000\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"> <img src="
				+ pdfLogo + "> </td>" +

				"<td style=\"border-bottom: 1px solid #000000; text-align: center\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"><b><font color=\"#000000\" face=\"Arial\" size=1>"
				+ matchName + "</font></b></td>" +

				"<td style=\"border-bottom: 1px solid #000000; text-align: center\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"><b><font color=\"#000000\" face=\"Arial\" size=1>Contest: Win Rs. "
				+ pricePool + "</font></b></td>" +

				"<td style=\"border-bottom: 1px solid #000000; text-align: center\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"><b><font color=\"#000000\" face=\"Arial\" size=1>Entry Fee Rs. "
				+ entryFees + "</font></b></td>" +

				"<td style=\"border-bottom: 1px solid #000000; text-align: center\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"><b><font color=\"#000000\" face=\"Arial\" size=1>Members: "
				+ totalJoinedTeams + "</font></b></td>" +

				"<td style=\"border-bottom: 1px solid #000000; text-align: center\" colspan=2 height=\"32\" align=\"left\" valign=middle bgcolor=\"#ffffff\"><b><font color=\"#000000\" face=\"Arial\" size=1>Invite code: "
				+ contestSlug + "</font></b></td>" + "</tr>" + "<tr style=\"background-color: #f2f2f2;\">"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000 font-size: 14px;font-weight: 600; border-left: 1px solid #000000; border-right: 1px solid #000000\" height=\"10\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>User (Team)</font></b></th>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-1 (Captain)</font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-2 (Vice Captain)</font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-3 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-4 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-5 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-6 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-7 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-8 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-9 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-10 </font></b></td>"
				+ "<th style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>Player-11 </font></b></td>"
				+ "</tr>";

		int i = 1;
		for (Object teamData : teams) {
			JSONObject team = (JSONObject) teamData;

			String teamName = team.optString("teamName");
			String name = team.optString("name");
			JSONArray playersData = team.optJSONArray("players");

			if (Util.isEmpty(teamName)) {
				output.put("error", "team teamName not found for position " + i);
				return output;
			}
			if (Util.isEmpty(name)) {
				output.put("error", "team name not found for position " + i);
				return output;
			}
			if (playersData == null || playersData.length() == 0) {
				output.put("error", "team players not found for position " + i);
				return output;
			}

			if (i % 2 == 0) {
				message += "<tr>";
			} else {
				message += "<tr style=\"background-color: #bfbfbf;\">";
			}

			message += "<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" height=\"10\" align=\"left\" valign=middle ><b><font face=\"Arial\" size=1>"
					+ teamName + "T" + name + ")</font></b></td>";

			for (Object playerData : playersData) {
				JSONObject player = (JSONObject) playerData;

				String playerName = player.optString("name");

				if (Util.isEmpty(playerName)) {
					output.put("error", "player name not found on team position " + i);
					return output;
				}

				message += "<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" valign=middle ><font face=\"Arial\" size=1>"
						+ playerName + "</font></td>";
			}

			message += "</tr>";

			i++;
		}

		message += "</table>";

		output.put("error", "");
		output.put("html", message);

		return output;
	}
}
