package example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author optim-y-takahashi
 */
@Data
@ConfigurationProperties("app")
public class Properties {
	private String imageFormat;
	private int maxWidth,maxHeight;

}
