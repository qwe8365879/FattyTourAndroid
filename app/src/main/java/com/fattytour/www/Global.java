package com.fattytour.www;

/**
 * Created by Junjie on 4/01/2017.
 */

class Global {
    public static CommunicationCore communicationCore = new CommunicationCore();
    public class NotificationName{
        public static final String getAirportTransOrder = "GetAirportTransOrder";
        public static final String airportTransOrderControlSuccess = "AirportTransOrderControlSuccess";
        public static final String airportTransOrderControlFailed = "AirportTransOrderControlFailed";
        public static final String loginSuccess = "LoginSuccess";
        public static final String loginFailed = "LoginFailed";
        public static final String connectionError = "ConnectionError";
    }
    public class LoaclStorageKey{
        public static final String username = "username";
        public static final String password = "password";
    }

}


