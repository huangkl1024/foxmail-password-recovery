package com.huangkailong.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangkl
 * @since 1.0.0
 */
public class FoxmailPasswordRecovery {
    private static final String STORAGE_DIR_NAME = "Storage";
    private static final String ACCOUNTS_DIR_NAME = "Accounts";
    private static final String ACCOUNT_REC0_FILE_NAME = "Account.rec0";

    /**
     * foxmail 安装路径
     */
    private final String foxmailInstalledPath;

    public FoxmailPasswordRecovery(String foxmailInstalledPath) {
        Assert.notBlank(foxmailInstalledPath, "FoxmailInstalledPath cannot be blank!");
        Assert.isTrue(FileUtil.isDirectory(foxmailInstalledPath),
            "FoxmailInstalledPath does not a directory!");
        this.foxmailInstalledPath = foxmailInstalledPath;
    }

    /**
     * 解密所有的账号密码信息
     *
     * @return {@link  FoxmailPasswordInfo} 列表
     */
    public List<FoxmailPasswordInfo> decrypt() {
        File storageDirFile = new File(foxmailInstalledPath, STORAGE_DIR_NAME);
        List<File> accountDirectories =
            FileUtil.loopFiles(storageDirFile, 1, FileUtil::isDirectory);
        return accountDirectories.stream().map(accountDirectory -> {
            String account = accountDirectory.getName();
            File accountRec0File =
                FileUtil.file(accountDirectory, ACCOUNTS_DIR_NAME, ACCOUNT_REC0_FILE_NAME);
            byte[] content = FileUtil.readBytes(accountRec0File);
            FoxmailVersion foxmailVersion = getFoxmailVersion(content);
            String hashPassword = findHashPassword(foxmailVersion, content);
            String password = decodePassword(foxmailVersion, hashPassword);
            return new FoxmailPasswordInfo(account, password);
        }).collect(Collectors.toList());
    }


    /**
     * 解密密码
     *
     * @param foxmailVersion {@link  FoxmailVersion}
     * @param hashPassword   加密的密码
     * @return 密码原文
     */
    public static String decodePassword(FoxmailVersion foxmailVersion, String hashPassword) {
        StringBuilder decodedPassword = new StringBuilder();

        int[] key = {'~', 'd', 'r', 'a', 'G', 'o', 'n', '~'};
        int[] v7Key = {'~', 'F', '@', '7', '%', 'm', '$', '~'};
        int fc0 = Integer.parseInt("5A", 16);


        if (foxmailVersion == FoxmailVersion.V7) {
            key = v7Key;
            fc0 = Integer.parseInt("71", 16);
        }

        int size = hashPassword.length() / 2;
        int index = 0;
        int[] b = new int[size];
        for (int i = 0; i < size; i++) {
            b[i] = Integer.parseInt(hashPassword.substring(index, index + 2), 16);
            index = index + 2;
        }

        int[] c = new int[b.length];

        c[0] = b[0] ^ fc0;
        System.arraycopy(b, 1, c, 1, b.length - 1);

        while (b.length > key.length) {
            int[] newA = new int[key.length * 2];
            System.arraycopy(key, 0, newA, 0, key.length);
            System.arraycopy(key, 0, newA, key.length, key.length);
            key = newA;
        }

        int[] d = new int[b.length];

        for (int i = 1; i < b.length; i++) {
            d[i - 1] = b[i] ^ key[i - 1];
        }

        int[] e = new int[d.length];

        for (int i = 0; i < d.length - 1; i++) {
            if (d[i] - c[i] < 0) {
                e[i] = d[i] + 255 - c[i];

            } else {
                e[i] = d[i] - c[i];
            }

            decodedPassword.append((char) e[i]);
        }
        return decodedPassword.toString();
    }


    private String findHashPassword(FoxmailVersion version, byte[] content) {
        StringBuilder buffer = new StringBuilder();
        boolean accountFound = false;
        for (int i = 0; i < content.length; i++) {
            // 过滤掉不是字母数字字符
            // 0x3d ascii 对应为 =
            if (isAlphanumericCharacter(content[i]) && content[i] != 0x3d) {
                buffer.append((char) content[i]);
                // 检查下一个单词是否为 Account 或 POP3Account
                StringBuilder account = new StringBuilder();
                if ("Account".equals(buffer.toString()) ||
                    "POP3Account".equals(buffer.toString())) {

                    // 偏移
                    int index = i + 9;

                    // 6.5 版本需要再增加额外的偏移
                    if (version == FoxmailVersion.V6) {
                        index = i + 2;
                    }

                    // 循环，直到提取出全部数据 (数据为字母数字型，非字母数字型意味着数据结束。)
                    while (isAlphanumericCharacter(content[index])) {
                        account.append((char) content[index]);
                        index++;
                    }

                    // 账号已找到
                    accountFound = true;


                    i = index;
                }
                // 找到账号后，确定是否找到 Password 或 POP3Password
                else if (accountFound && ("Password".equals(buffer.toString()) ||
                    "POP3Password".equals(buffer.toString()))) {
                    int index = i + 9;
                    if (version == FoxmailVersion.V6) {
                        index = i + 2;
                    }
                    StringBuilder pw = new StringBuilder();

                    while (isAlphanumericCharacter(content[index])) {
                        pw.append((char) content[index]);
                        index++;
                    }

                    return pw.toString();
                }
            } else {
                buffer = new StringBuilder();
            }
        }
        throw new RuntimeException("can not find password in this file!");
    }

    /**
     * 是否为字母数字字符
     */
    private boolean isAlphanumericCharacter(byte b) {
        return b > 0x20 && b < 0x7f;
    }


    private FoxmailVersion getFoxmailVersion(byte[] content) {
        byte firstByte = content[0];
        if (firstByte == 0x0D) {
            // Version 6.X
            return FoxmailVersion.V6;
        } else if (firstByte == 0x52) {
            // Version 7.0 and 7.1
            return FoxmailVersion.V7;
        } else {
            return FoxmailVersion.UNKNOWN;
        }
    }

    private enum FoxmailVersion {
        /**
         * V6 版本
         */
        V6,
        /**
         * V7 版本
         */
        V7,
        /**
         * 未知
         */
        UNKNOWN
    }
}
