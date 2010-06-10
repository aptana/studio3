package org.tmatesoft.sqljet.core.table;

import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * <p>
 * Implementation of SQLJet busy handlers. Used by default in SqlJetDb.
 * </p>
 * 
 * <p>
 * Performs some number of retries (by default 10 or SQLJET_BUSY_RETRIES system
 * property value) per every time interval (by default 100 milliseconds or
 * SQLJET_BUSY_SLEEP system property value in milliseconds).
 * </p>
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetDefaultBusyHandler implements ISqlJetBusyHandler {

    /**
     * Name of system property which defines retries count by default.
     */
    public static final String SQLJET_BUSY_RETRIES_PROPERTY = "SQLJET_BUSY_RETRIES";

    /**
     * Name of system property which defines time wait by default.
     */
    public static final String SQLJET_BUSY_SLEEP_PROPERTY = "SQLJET_BUSY_SLEEP";

    private static final int DEFAULT_RETRIES = 10;
    private static final int DEFAULT_SLEEP = 100;

    private int retries;
    private int sleep;
    private boolean cancel = false;

    /**
     * Creates busy handler with default parameters.
     */
    public SqlJetDefaultBusyHandler() {
        retries = SqlJetUtility.getIntSysProp(SQLJET_BUSY_RETRIES_PROPERTY, DEFAULT_RETRIES);
        sleep = SqlJetUtility.getIntSysProp(SQLJET_BUSY_SLEEP_PROPERTY, DEFAULT_SLEEP);
    }

    /**
     * Creates busy handler with custom parameters.
     * 
     * @param retries number of retries to perform
     * @param sleep sleep time interval in milliseconds between retries to lock database.
     */
    public SqlJetDefaultBusyHandler(final int retries, final int sleep) {
        if (retries > 0) {
            this.retries = retries;
        } else {
            this.retries = SqlJetUtility.getIntSysProp(SQLJET_BUSY_RETRIES_PROPERTY, DEFAULT_RETRIES);
        }
        if (sleep > 0) {
            this.sleep = sleep;
        } else {
            this.sleep = SqlJetUtility.getIntSysProp(SQLJET_BUSY_SLEEP_PROPERTY, DEFAULT_SLEEP);
        }
    }

    /**
     * Returns number of attempts to make to lock database.
     * 
     * @return number of attempts.
     */
    public int getRetries() {
        return retries;
    }

    /**
     * Sets number of attempts to make to lock database.
     * 
     * @param retries number of attempts.
     */
    public void setRetries(int retries) {
        this.retries = retries;
    }

    /**
     * Returns sleep time interval in milliseconds between retries to lock database.
     * 
     * @return sleep interval time in milliseconds.
     */
    public int getSleep() {
        return sleep;
    }

    /**
     * Sets sleep time interval in milliseconds between retries to lock database.
     * 
     * @param sleep interval time in milliseconds.
     */
    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    /**
     * Allow cancel urgently busy retries. To cancel set it to true - in this
     * case busy handler will not wait in next retry.
     * 
     * @param cancel
     *            if true then busy handler will not wait.
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Check is busy handler to cancel.
     * 
     * @return true if lock attempts should be cancelled.
     */
    public boolean isCancel() {
        return cancel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler#call(int)
     */
    public boolean call(int number) {
        if (cancel) {
            cancel = false;
            return false;
        } else if (number > retries) {
            return false;
        } else {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }
    }

}