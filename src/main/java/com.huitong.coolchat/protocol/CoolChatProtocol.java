package com.huitong.coolchat.protocol;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoolChatProtocol {

    private int length;
    private byte[] content;
}
