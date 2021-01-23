/*
 * Copyright 2014 jamietech. All rights reserved.
 * https://github.com/jamietech/MinecraftServerPing
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package us.mcstats.serverstats.ping;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
@Getter
@ToString
public class MCPingResponse {
    private Description description;
    private Players players;
    private Version version;
    private String favicon;

    @Setter
    private long ping;

    @Getter
    @ToString
    public class Description {
        private String text;
        public String getStrippedText() {
            return MCPingUtil.stripColors(this.text);
        }
    }

    @Getter
    @ToString
    public class Players {
        private int max;
        private int online;
        private List<Player> sample;
    }

    @Getter
    @ToString
    public class Player {
        private String name;
        private String id;
    }

    @Getter
    @ToString
    public class Version {
        private String name;
        private int protocol;
    }
}
