package com.exaroton.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * A page of a paginated list. Lists are paginated with 20 items per page, pages after the last page are empty.
 *
 * @param <T> item type
 */
@SuppressWarnings("unused")
@ApiStatus.AvailableSince("2.6.0")
@ApiStatus.NonExtendable
public class Page<T> implements Iterable<T> {
    /**
     * Items on this page
     */
    private List<T> items = List.of();

    /**
     * The requested page (starting at 1)
     */
    private int page;

    /**
     * The last page with items (at least 1)
     */
    private int lastPage;

    /**
     * Total number of items in all pages
     */
    private int total;

    /**
     * @return items on this page
     */
    public @NotNull List<T> getItems() {
        return items == null ? List.of() : items;
    }

    /**
     * @return the number of this page (starting at 1)
     */
    public int getPage() {
        return page;
    }

    /**
     * @return the number of the last page with items (at least 1)
     */
    public int getLastPage() {
        return lastPage;
    }

    /**
     * @return total number of items in all pages
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return true if there is a page after this one
     */
    public boolean hasNextPage() {
        return page < lastPage;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return getItems().iterator();
    }
}
