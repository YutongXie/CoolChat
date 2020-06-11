package com.huitong.coolchat.protocol;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoolChatMessage {

    private int length;
    private byte[] content;
}
