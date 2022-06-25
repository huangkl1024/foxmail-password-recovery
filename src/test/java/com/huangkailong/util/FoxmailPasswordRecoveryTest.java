package com.huangkailong.util;

import java.util.List;
import org.junit.jupiter.api.Test;

class FoxmailPasswordRecoveryTest {

    @Test
    void decrypt() {
        String foxmailInstalledPath = "D:\\Program Files\\Foxmail";
        FoxmailPasswordRecovery foxmailPasswordRecovery =
            new FoxmailPasswordRecovery(foxmailInstalledPath);
        List<FoxmailPasswordInfo> foxmailPasswordInfos = foxmailPasswordRecovery.decrypt();
        System.out.println(foxmailPasswordInfos);
    }
}
