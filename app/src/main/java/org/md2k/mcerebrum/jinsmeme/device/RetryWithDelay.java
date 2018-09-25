/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.jinsmeme.device;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Provides methods for retrying an <code>Observable</code>.
 */
public class RetryWithDelay implements
        Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int retryDelayMillis;

    /**
     * Constructor
     * @param retryDelayMillis Time between retries in milliseconds.
     */
    public RetryWithDelay(final int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }

    /**
     * Tries to register the given <code>Obaservable</code>.
     * @param attempts <code>Observable</code> to retry.
     * @return The given <code>Observable</code>.
     */
    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
                .flatMap(new Func1<Throwable, Observable<?>>() {
                    /**
                     * When this Observable calls onNext, the original Observable will be retried.
                     * @param throwable
                     * @return
                     */
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        return Observable.timer(retryDelayMillis,
                                TimeUnit.MILLISECONDS);
                    }
                });
    }
}