/*
 * UNCLASSIFIED
 *
 * Revision History:
 *
 *   Date     SEC.No.  Programmer           Description
 * ---------- ------- -------------------- -------------------------------------
 * 2026-07-31 IMDL001  Zopiloman            Initial release
 *
 */
package mx.sedena.itzamna.mtdlconnector.core;

import java.util.Date;

import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;

/**
 * Defines an abstract message that provides MTDL latitude, longitude, time,
 * angle, speed and altitude fields.
 */
@SuppressWarnings("serial")
public abstract class AbstractTrackPosition extends Message {

    /** The angle. */
    private byte angle = 0;

    /** The latitude. */
    private int latitude = 0;

    /** The longitude. */
    private int longitude = 0;

    /** The speed. */
    private short speed = 0;

    /** The altitude. */
    private short altitude = 0;

    /** The time. */
    private long time = 0;

    /**
     * Instantiates a new abstract track position.
     *
     * @param header
     *           the header
     */
    public AbstractTrackPosition(MessageHeader header) {
        super(header);
    }

    // =============================================
    // Native MTDL units; possibly BAMS
    // http://www.globalspec.com/reference/14722/160210/Chapter-7-5-3-Binary-Angular-Measure
    // =============================================
    /**
     * Gets the angle.
     *
     * @return the angle
     */
    public byte getAngle() {
        return angle;
    }

    /**
     * Sets the angle.
     *
     * @param angle
     *           the new angle
     */
    public void setAngle(byte angle) {
        this.angle = angle;
    }

    /**
     * Gets the latitude.
     *
     * @return the latitude
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude
     *           the new latitude
     */
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude.
     *
     * @return the longitude
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude
     *           the new longitude
     */
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the speed.
     *
     * @return the speed
     */
    public short getSpeed() {
        return speed;
    }

    /**
     * Sets the speed.
     *
     * @param speed
     *           the new speed
     */
    public void setSpeed(short speed) {
        this.speed = speed;
    }

    // =============================================
    // End BAMS units
    // =============================================

    // Meters
    /**
     * Gets the altitude.
     *
     * @return the altitude
     */
    public short getAltitude() {
        return altitude;
    }

    /**
     * Sets the altitude.
     *
     * @param altitude
     *           the new altitude
     */
    public void setAltitude(short altitude) {
        this.altitude = altitude;
    }

    // UNIX epoch
    /**
     * Gets the time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the time.
     *
     * @param time
     *           the new time
     */
    public void setTime(long time) {
        this.time = time;
    }

    // =============================================
    // Conversions
    // =============================================
    /**
     * Gets the angle degrees.
     *
     * @return the angle degrees
     */
    public double getAngleDegrees() {
        return fromB7(angle);
    }

    /**
     * Sets the angle degrees.
     *
     * @param angle
     *           the new angle degrees
     */
    public void setAngleDegrees(double angle) {
        this.angle = toB7(angle);
    }

    /**
     * Gets the latitude degrees.
     *
     * @return the latitude degrees
     */
    public double getLatitudeDegrees() {
        return fromB31(latitude);
    }

    /**
     * Sets the latitude degrees.
     *
     * @param latitude
     *           the new latitude degrees
     */
    public void setLatitudeDegrees(double latitude) {
        this.latitude = toB31(latitude);
    }

    /**
     * Gets the longitude degrees.
     *
     * @return the longitude degrees
     */
    public double getLongitudeDegrees() {
        return fromB31(longitude);
    }

    /**
     * Sets the longitude degrees.
     *
     * @param longitude
     *           the new longitude degrees
     */
    public void setLongitudeDegrees(double longitude) {
        this.longitude = toB31(longitude);
    }

    /**
     * Gets the speed meters per second.
     *
     * @return the speed meters per second
     */
    public double getSpeedMetersPerSecond() {
        return fromBS(speed);
    }

    /**
     * Sets the speed meters per second.
     *
     * @param speed
     *           the new speed meters per second
     */
    public void setSpeedMetersPerSecond(double speed) {
        this.speed = toBS(speed);
    }

    // =============================================

    /**
     * Gets the track info.
     *
     * @return the track info
     */
    public String getTrackInfo() {
        String block = "Track Angle: " + getAngleDegrees() +
                System.getProperty("line.separator");
        block += "Latitude: " + getLatitudeDegrees() +
                System.getProperty("line.separator");
        block += "Longitude: " + getLongitudeDegrees() +
                System.getProperty("line.separator");
        block += "Speed: " + getSpeedMetersPerSecond() +
                System.getProperty("line.separator");
        block += "Altitude: " + getAltitude() +
                System.getProperty("line.separator");
        block += "Time: " + new Date(getTime() * 1000).toString() +
                System.getProperty("line.separator");
        return block;
    }

    // =============================================
    // BAMS math maybe, don't know or care.
    // =============================================
    /**
     * From b31.
     *
     * @param val
     *           the val
     * @return the double
     */
    public static double fromB31(int val) {
        double v = val;
        v /= Math.pow(2.0d, 31.0d);
        v *= 180.0d;
        assert val == toB31(v);
        return v;
    }

    /**
     * To b31.
     *
     * @param val
     *           the val
     * @return the int
     */
    public static int toB31(double val) {
        double v = val;
        v /= 180.0d;
        v *= Math.pow(2.0d, 31.0d);
        return (int) v;
    }

    /**
     * From b7.
     *
     * @param val
     *           the val
     * @return the double
     */
    private static double fromB7(byte val) {
        double v = val;
        v /= Math.pow(2.0d, 7.0d);
        v *= 180.0d;
        assert val == toB7(v);
        return v;
    }

    /**
     * To b7.
     *
     * @param val
     *           the val
     * @return the byte
     */
    private static byte toB7(double val) {
        double v = val;
        v /= 180.0d;
        v *= Math.pow(2.0d, 7.0d);
        return (byte) v;
    }

    /**
     * From bs.
     *
     * @param val
     *           the val
     * @return the double
     */
    private static double fromBS(short val) {
        double v = val;
        v /= Math.pow(2.0d, 5.0d);
        assert val == toBS(v);
        return v;

    }

    /**
     * To bs.
     *
     * @param val
     *           the val
     * @return the short
     */
    private static short toBS(double val) {
        double v = val;
        v *= Math.pow(2.0d, 5.0d);
        return (short) v;
    }
}
// UNCLASSIFIED
