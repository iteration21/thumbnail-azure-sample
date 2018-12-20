package example.config;

import java.util.Optional;

import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import example.functions.Thumbnail.Input;
import example.functions.Thumbnail.Output;

/**
 * @author optim-y-takahashi
 */
public class Handler extends AzureSpringBootRequestHandler<Input, Output> {

	@FunctionName("thumbnail")
	public Output execute(
			@HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
			@BindingName("blobName") String blobName,
			ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        
        final Input input = new Input();
        input.setBlobName(blobName);
        
        context.getLogger().info("java.version: " + System.getProperty("java.version"));
        context.getLogger().info("java.vendor: " + System.getProperty("java.vendor"));
        context.getLogger().info("java.vendor.url: " + System.getProperty("java.vendor.url"));
        context.getLogger().info("java.home: " + System.getProperty("java.home"));
        context.getLogger().info("os.name: " + System.getProperty("os.name"));
        context.getLogger().info("os.arch: " + System.getProperty("os.arch"));
        context.getLogger().info("os.version: " + System.getProperty("os.version"));

		return handleRequest(input, context);
	}
}