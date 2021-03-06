/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package stateful.server;

import com.sun.xml.ws.developer.Stateful;
import com.sun.xml.ws.developer.StatefulWebServiceManager;

import javax.jws.WebService;
import javax.xml.ws.soap.Addressing;
import java.util.ArrayList;
import java.util.List;

/**
 * A book instance.
 *
 * @since 2.1 EA2
 */
@Stateful @WebService @Addressing
public class Book {
    /**
     * The ID of the book.
     * In this example, it's used just to show that we are indeed
     * maintaining multiple instances and requests are dispatched
     * to the right instance.
     */
    private final String id;

    /**
     * Reviews of this book.
     *
     * <p>
     * The beauty of a stateful web service support is that you can
     * use instance fields like this to store object state.
     * In the real world, you'd probably be persisting this object
     * to database by perhaps using JPA, but this example doesn't show that.
     *
     * <p>
     * To allow concurrenct access, we use a copy-on-write pattern here,
     * hence 'volatile'.
     */
    private volatile List<String> reviews = new ArrayList<String>();

    // unlike stateless web service, the JAX-WS RI will never
    // create an instance. So you can define a constructor with arguments,
    // and in fact you'd normally want to do that so that you can initialize
    // the object with proper state.
    public Book(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Posts a review.
     *
     * <p>
     * Synchronized to handle concurrent submissions.
     */
    public synchronized void postReview(String review) {
        List<String> l = new ArrayList<String>(reviews);
        l.add(review);
        reviews = l;
    }

    /**
     * Gets all the reviews posted so far.
     */
    public List<String> getReviews() {
        return reviews;
    }

    /**
     * The equals implementation so that two {@link Book}s with the same ID
     * won't be exported twice.
     */
    public boolean equals(Object that) {
        if (that == null || this.getClass() != that.getClass()) return false;
        return this.id.equals(((Book)that).id);
    }

    /**
     * Hashcode implementation consistent with {@link #equals(Object)}.
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * This object is injected by the JAX-WS RI, and exposes various
     * operations to support stateful web services. Consult its javadoc
     * for more information.
     */
    public static StatefulWebServiceManager<Book> manager;
}
