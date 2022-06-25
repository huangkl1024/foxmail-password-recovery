<div align="center">
  <h1>Foxmail 密码解密</h1>

<!-- Badges -->
<p>
  <a href="https://github.com/OutOfMemoryEx/foxmail-password-recovery/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/OutOfMemoryEx/foxmail-password-recovery" alt="contributors" />
  </a>
  <a href="">
    <img src="https://img.shields.io/github/last-commit/OutOfMemoryEx/foxmail-password-recovery" alt="last update" />
  </a>
  <a href="https://github.com/OutOfMemoryEx/foxmail-password-recovery/network/members">
    <img src="https://img.shields.io/github/forks/OutOfMemoryEx/foxmail-password-recovery" alt="forks" />
  </a>
  <a href="https://github.com/OutOfMemoryEx/foxmail-password-recovery/stargazers">
    <img src="https://img.shields.io/github/stars/OutOfMemoryEx/foxmail-password-recovery" alt="stars" />
  </a>
  <a href="https://github.com/OutOfMemoryEx/foxmail-password-recovery/issues/">
    <img src="https://img.shields.io/github/issues/OutOfMemoryEx/foxmail-password-recovery" alt="open issues" />
  </a>
  <a href="https://github.com/OutOfMemoryEx/foxmail-password-recovery/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/OutOfMemoryEx/foxmail-password-recovery.svg" alt="license" />
  </a>
</p>
</div>

<br />

> 本项目基于 [Foxmail-Password-Recovery](https://github.com/StarZHF/Foxmail-Password-Recovery) 移植

### :space_invader: Foxmail 测试版本

Foxmail 7.2.23.116


### :running: 本地运行

通过 git 克隆项目

```bash
  git clone https://github.com/OutOfMemoryEx/foxmail-password-recovery
```

使用 idea 打开项目

找到 `FoxmailPasswordRecoveryTest` 类，修改 `foxmailInstalledPath` 变量值为自己电脑 foxmail 安装路径

运行 `FoxmailPasswordRecoveryTest#decrypt()` 测试方法

## :eyes: Usage



```java
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
```



<!-- License -->
## :warning: 开源许可证

查看 LICENSE.txt 了解更多信息.
