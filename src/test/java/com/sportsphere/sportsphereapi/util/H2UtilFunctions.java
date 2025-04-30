package com.sportsphere.sportsphereapi.util;

public class H2UtilFunctions {

    public static String llToEarth(double lat, double lon) {
        return lat + "," + lon;
    }

    public static double earthDistance(String point1, String point2) {
        if (point1 == null || point2 == null) {
            throw new IllegalArgumentException("Invalid points");
        }

        String[] coords1 = point1.split(",");
        double lat1 = Double.parseDouble(coords1[0]);
        double lon1 = Double.parseDouble(coords1[1]);

        String[] coords2 = point2.split(",");
        double lat2 = Double.parseDouble(coords2[0]);
        double lon2 = Double.parseDouble(coords2[1]);

        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
