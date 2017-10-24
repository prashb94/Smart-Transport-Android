package ch.ethz.vs.se.team7.carbonfootprintmonitor;

/**
 * Created by Prashanth on 10/23/2017.
 */

public class LastKnownLocationAndSpeed {

    private static String lastDetectedLocation;

    private static String lastDetectedSpeed;

    public static String getLastDetectedLocation(){
        if(lastDetectedLocation != null)
            return lastDetectedLocation;
        else
            return "0.0,0.0";
    }

    public static String getLastDetectedSpeed(){
        if(lastDetectedSpeed != null)
            return lastDetectedSpeed;
        else
            return "0.0 m/s";
    }

    public static void setLastDetectedLocation(String lastDetectedLocation) {
        LastKnownLocationAndSpeed.lastDetectedLocation = lastDetectedLocation;
    }

    public static void setLastDetectedSpeed(String lastDetectedSpeed) {
        LastKnownLocationAndSpeed.lastDetectedSpeed = lastDetectedSpeed;
    }
}
