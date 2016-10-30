package nils.and.lamp.app.Core;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.PriorityQueue;

public class Nearby {
    public static final double radiusEarth = 6371.0;

    public static double distanceFromTo(Location me, Location you) {
        double phi_me = Math.toRadians(me.getLatitude());
        double phi_you = Math.toRadians(you.getLatitude());
        double delta_phi = Math.toRadians(you.getLatitude() - me.getLatitude());
        double delta_lambda = Math.toRadians(you.getLongitude() - me.getLongitude());

        double alpha = Math.sin(delta_phi / 2) * Math.sin(delta_phi / 2) +
                Math.cos(phi_me) * Math.cos(phi_you) *
                        Math.sin(delta_lambda / 2) * Math.sin(delta_lambda / 2);
        double gamma = 2 * Math.atan2(Math.sqrt(alpha), Math.sqrt(1 - alpha));

        return radiusEarth * gamma;
    }

    private class DistanceTarget implements Comparable<DistanceTarget> {
        private Location mLocation;

        public Location getLocation() {
            return mLocation;
        }

        private double mDistance;

        public double distance() {
            return mDistance;
        }

        public DistanceTarget(double d, Location l) {
            this.mDistance = d;
            this.mLocation = l;
        }

        @Override
        public int compareTo(@NonNull DistanceTarget distanceTarget) {
            return Double.compare(distance(), distanceTarget.distance());
        }

        @Override
        public int hashCode() {
            return getLocation().hashCode() ^ 13 + (int) Double.doubleToLongBits(distance());
        }
    }

    // k nearest neighbours
    public Location[] kNN(int k, Location me, Location[] candidates) {
        Location[] ret = new Location[k];
        PriorityQueue<DistanceTarget> pq = new PriorityQueue<>();
        for (Location c : candidates) {
            pq.offer(new DistanceTarget(distanceFromTo(me, c), c));
        }
        for (int i = 0; i < k; i++) {
            ret[i] = pq.poll().getLocation();
        }
        return ret;
    }
}
