package com.codemate.cric.users;

import com.codemate.cric.datatypes.Person;
import com.codemate.cric.matchdetails.Match;
import com.codemate.cric.teamdetails.Team;

public class Admin {
	private Person person;

	public boolean addMatch(Match match) { return false; }

	public boolean addTeam(Team team) { return false; }

	public boolean addTournament(Tournament tournament) { return false; }
}
