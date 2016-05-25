/*
 * Copyright 2015-2016 Real Logic Ltd.
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
package uk.co.real_logic.fix_gateway.session;

import io.aeron.logbuffer.ControlledFragmentHandler.Action;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.status.AtomicCounter;
import uk.co.real_logic.fix_gateway.decoder.LogonDecoder;
import uk.co.real_logic.fix_gateway.messages.SessionState;
import uk.co.real_logic.fix_gateway.protocol.GatewayPublication;

import static uk.co.real_logic.fix_gateway.builder.Validation.CODEC_VALIDATION_DISABLED;

// TODO: apply back-pressure from failed message sends to on* methods
public class InitiatorSession extends Session
{
    public InitiatorSession(
        final int heartbeatInterval,
        final long connectionId,
        final EpochClock clock,
        final SessionProxy proxy,
        final GatewayPublication publication,
        final SessionIdStrategy sessionIdStrategy,
        final long sendingTimeWindow,
        final AtomicCounter receivedMsgSeqNo,
        final AtomicCounter sentMsgSeqNo,
        final int libraryId,
        final int sessionBufferSize,
        final int initialSequenceNumber,
        final SessionState state)
    {
        super(
            heartbeatInterval,
            connectionId,
            clock,
            state,
            proxy,
            publication,
            sessionIdStrategy,
            sendingTimeWindow,
            receivedMsgSeqNo,
            sentMsgSeqNo,
            libraryId,
            sessionBufferSize,
            initialSequenceNumber);
    }

    public Action onLogon(
        final int heartbeatInterval,
        final int msgSeqNo,
        final long sessionId,
        final CompositeKey sessionKey,
        final long sendingTime,
        final long origSendingTime,
        final String username,
        final String password,
        final boolean isPossDupOrResend,
        final boolean resetSeqNumFlag)
    {
        if (msgSeqNo == expectedReceivedSeqNum() && state() == SessionState.SENT_LOGON)
        {
            state(SessionState.ACTIVE);
            this.sessionKey = sessionKey;
            proxy.setupSession(sessionId, sessionKey);
            if (CODEC_VALIDATION_DISABLED || (validateHeartbeat(heartbeatInterval) == null &&
                validateSendingTime(sendingTime) == null))
            {
                id(sessionId);
                heartbeatIntervalInS(heartbeatInterval);
                onMessage(msgSeqNo, LogonDecoder.MESSAGE_TYPE_BYTES, sendingTime, origSendingTime, isPossDupOrResend);
                publication.saveLogon(libraryId, connectionId, sessionId);
            }
        }
        else
        {
            if (resetSeqNumFlag)
            {
                proxy.setupSession(sessionId, sessionKey);

                final Action action = resetSeqNumLogon(heartbeatInterval, msgSeqNo, username, password);
                if (action != null)
                {
                    return action;
                }
            }

            onMessage(msgSeqNo, LogonDecoder.MESSAGE_TYPE_BYTES, sendingTime, origSendingTime, isPossDupOrResend);
        }

        return Action.CONTINUE;
    }

    public int poll(final long time)
    {
        int actions = 0;
        if (state() == SessionState.CONNECTED && id() != UNKNOWN)
        {
            state(SessionState.SENT_LOGON);
            final int heartbeatIntervalInS = (int) (heartbeatIntervalInMs() / 1000);
            final int sentSeqNum = newSentSeqNum();
            final long position = proxy.logon(heartbeatIntervalInS, sentSeqNum, username(), password(), false);
            if (position >= 0)
            {
                lastSentMsgSeqNum(sentSeqNum);
            }
            actions++;
        }
        return actions + super.poll(time);
    }

}
