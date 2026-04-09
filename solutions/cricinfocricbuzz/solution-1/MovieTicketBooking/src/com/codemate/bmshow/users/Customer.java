package com.codemate.bmshow.users;

import java.util.List;
import java.util.ArrayList;

import com.codemate.bmshow.booking.Booking;

public class Customer extends Person {
	public boolean makeBooking(Booking booking) { return false; }
	public List<Booking> getBookings() { return new ArrayList<>(); }
}
