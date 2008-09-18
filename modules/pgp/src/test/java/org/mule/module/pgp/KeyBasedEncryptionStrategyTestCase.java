/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.pgp;

import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.FileUtils;

import java.io.FileInputStream;
import java.net.URL;

public class KeyBasedEncryptionStrategyTestCase extends AbstractMuleTestCase
{
    private KeyBasedEncryptionStrategy kbStrategy;

    protected void doSetUp() throws Exception
    {
        PGPKeyRingImpl keyM = new PGPKeyRingImpl();
        URL url;

        url = Thread.currentThread().getContextClassLoader().getResource("./serverPublic.gpg");
        keyM.setPublicKeyRingFileName(url.getFile());

        url = Thread.currentThread().getContextClassLoader().getResource("./serverPrivate.gpg");
        keyM.setSecretKeyRingFileName(url.getFile());

        keyM.setSecretAliasId("0x6168F39C");
        keyM.setSecretPassphrase("TestingPassphrase");
        keyM.initialise();

        kbStrategy = new KeyBasedEncryptionStrategy();
        kbStrategy.setKeyManager(keyM);
        kbStrategy.initialise();
    }

    protected void doTearDown() throws Exception
    {
        kbStrategy = null;
    }

    public void testDecrypt() throws Exception
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource("./encrypted-signed.asc");

        int length = (int) FileUtils.newFile(url.getFile()).length();
        byte[] msg = new byte[length];

        FileInputStream in = new FileInputStream(url.getFile());
        in.read(msg);
        in.close();

        String result = new String(kbStrategy.decrypt(msg, null));

        System.out.println(result);

        assertNotNull(result);
    }

    public void testEncrypt() throws Exception
    {
        String msg = "Test Message";
        PGPCryptInfo cryptInfo = new PGPCryptInfo(kbStrategy.getKeyManager().getKeyBundle(
            "Mule client <mule_client@mule.com>"), true);

        String result = new String(kbStrategy.encrypt(msg.getBytes(), cryptInfo));

        System.out.println(result);

        assertNotNull(result);
    }
}
