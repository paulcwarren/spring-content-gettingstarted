package gettingstarted;

import org.testcontainers.vault.VaultContainer;

public class VaultContainerSupport {

    private static VaultContainer vaultContainer = null;

    public static VaultContainer getVaultContainer() {

        if (vaultContainer == null) {
            vaultContainer = new VaultContainer<>()
                    .withVaultToken("root-token")
                    .withVaultPort(8200)
                    .withInitCommand("secrets enable transit");

            vaultContainer.start();
        }

        return vaultContainer;
    }
}
