package com.codemate.bmshow.users;

import com.codemate.bmshow.movie.Movie;
import com.codemate.bmshow.movie.Show;

public class Admin extends Person {
	public boolean addMovie(Movie movie) { return false; }
	public boolean addShow(Show show) { return false; }
	public boolean blockUser(Customer customer) { return false; }
}
