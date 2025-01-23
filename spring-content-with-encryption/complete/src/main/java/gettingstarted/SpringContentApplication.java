package gettingstarted;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.encryption.config.EncryptingContentStoreConfiguration;
import org.springframework.content.encryption.config.EncryptingContentStoreConfigurer;
import org.springframework.content.encryption.keys.VaultTransitDataEncryptionKeyWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultOperations;

@SpringBootApplication
public class SpringContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringContentApplication.class, args);
	}

	@Configuration
	public static class Config extends AbstractVaultConfiguration {

		@Override
		public VaultEndpoint vaultEndpoint() {

			String host = VaultContainerSupport.getVaultContainer().getHost();
			int port = VaultContainerSupport.getVaultContainer().getMappedPort(8200);

			VaultEndpoint vault = VaultEndpoint.create(host, port);
			vault.setScheme("http");
			return vault;
		}

		@Override
		public ClientAuthentication clientAuthentication() {
			return new TokenAuthentication("root-token");
		}

		@Bean
		public EncryptingContentStoreConfigurer<FileContentStore> config(VaultOperations vaultOperations) {
			return new EncryptingContentStoreConfigurer<FileContentStore>() {
				@Override
				public void configure(EncryptingContentStoreConfiguration config) {
					config.dataEncryptionKeyWrappers(List.of(
									new VaultTransitDataEncryptionKeyWrapper(vaultOperations.opsForTransit(), "fsfile")
							))
							.encryptionKeyContentProperty("key");
				}
			};
		}
	}
}
