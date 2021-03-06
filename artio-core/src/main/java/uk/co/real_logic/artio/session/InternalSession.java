/*
 * Copyright 2015-2018 Real Logic Ltd, Adaptive Financial Consulting Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.artio.session;

import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.status.AtomicCounter;
import uk.co.real_logic.artio.messages.SessionState;
import uk.co.real_logic.artio.protocol.GatewayPublication;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

/**
 * Exposes Session methods to internal APIs that we don't want to expose to the outside world
 */
public class InternalSession extends Session
{
    public InternalSession(
        final int heartbeatIntervalInS,
        final long connectionId,
        final EpochClock clock,
        final SessionState state,
        final SessionProxy proxy,
        final GatewayPublication publication,
        final SessionIdStrategy sessionIdStrategy,
        final long sendingTimeWindowInMs,
        final AtomicCounter receivedMsgSeqNo,
        final AtomicCounter sentMsgSeqNo,
        final int libraryId,
        final int initialSentSequenceNumber,
        final int sequenceIndex,
        final long reasonableTransmissionTimeInMs,
        final MutableAsciiBuffer asciiBuffer)
    {
        super(
            heartbeatIntervalInS,
            connectionId,
            clock,
            state,
            proxy,
            publication,
            sessionIdStrategy,
            sendingTimeWindowInMs,
            receivedMsgSeqNo,
            sentMsgSeqNo,
            libraryId,
            initialSentSequenceNumber,
            sequenceIndex,
            reasonableTransmissionTimeInMs,
            asciiBuffer);
    }

    public int poll(final long time)
    {
        return super.poll(time);
    }

    public void disable()
    {
        super.disable();
    }

    public void libraryConnected(final boolean libraryConnected)
    {
        super.libraryConnected(libraryConnected);
    }

    public void logonListener(final SessionLogonListener logonListener)
    {
        super.logonListener(logonListener);
    }

    public void address(final String connectedHost, final int connectedPort)
    {
        super.address(connectedHost, connectedPort);
    }

    public void username(final String username)
    {
        super.username(username);
    }

    public void password(final String password)
    {
        super.password(password);
    }

    public void logonTime(final long logonTime)
    {
        super.logonTime(logonTime);
    }
}
