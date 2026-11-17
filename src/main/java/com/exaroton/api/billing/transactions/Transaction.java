package com.exaroton.api.billing.transactions;

import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * A credit transaction of an account, credit pool or server
 *
 * @see com.exaroton.api.ExarotonClient#getAccountTransactions(int)
 * @see com.exaroton.api.billing.pools.CreditPool#getTransactions(int)
 * @see com.exaroton.api.server.Server#getTransactions(int)
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
public final class Transaction {
    /**
     * Unique transaction ID
     */
    private String id;

    /**
     * Type of the transaction
     */
    private String type;

    /**
     * Credits of the transaction from the perspective of the list owner
     */
    private double credits;

    /**
     * Rate used to calculate the credits, in credits per interval
     */
    private Integer rate;

    /**
     * Interval of the rate in seconds
     */
    private Integer interval;

    /**
     * Factor used to calculate the credits, e.g. the GB of RAM
     */
    private Double amount;

    /**
     * Whether the transaction is still running
     */
    private boolean active;

    /**
     * Whether the transaction is paid
     */
    private boolean paid;

    /**
     * Unix timestamp of the start of the transaction
     */
    private long startTime;

    /**
     * Unix timestamp of the end of the transaction
     */
    private Long endTime;

    /**
     * Unique ID of the account of the transaction
     */
    private String account;

    /**
     * Unique ID of the credit pool of the transaction
     */
    private String pool;

    /**
     * Unique ID of the server of the transaction
     */
    private String server;

    /**
     * @return unique transaction ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the type of the transaction, e.g. run (running a server), retention (keeping a server's files), payment,
     * refund, pool-deposit, pool-withdraw, redeem-code or a compensation (comp-*)
     *
     * @return transaction type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the credits of the transaction from the perspective of the account, credit pool or server the list belongs
     * to: positive for income, negative for usage. This grows while the transaction is active.
     *
     * @return credits of the transaction
     */
    public double getCredits() {
        return credits;
    }

    /**
     * @return rate used to calculate the credits, in credits per {@link #getInterval() interval}
     */
    public Optional<Integer> getRate() {
        return Optional.ofNullable(rate);
    }

    /**
     * @return interval of the {@link #getRate() rate}
     */
    public Optional<Duration> getInterval() {
        return Optional.ofNullable(interval).map(Duration::ofSeconds);
    }

    /**
     * @return factor used to calculate the credits, e.g. the GB of RAM
     */
    public Optional<Double> getAmount() {
        return Optional.ofNullable(amount);
    }

    /**
     * @return true if the transaction is still running (e.g. the server is still online)
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return true if the transaction is paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @return start of the transaction
     */
    public Instant getStartTime() {
        return Instant.ofEpochSecond(startTime);
    }

    /**
     * @return end of the transaction, empty while it is active
     */
    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(endTime).map(Instant::ofEpochSecond);
    }

    /**
     * @return unique ID of the account of the transaction
     */
    public Optional<String> getAccount() {
        return Optional.ofNullable(account);
    }

    /**
     * @return unique ID of the credit pool of the transaction
     */
    public Optional<String> getPool() {
        return Optional.ofNullable(pool);
    }

    /**
     * @return unique ID of the server of the transaction
     */
    public Optional<String> getServer() {
        return Optional.ofNullable(server);
    }
}
