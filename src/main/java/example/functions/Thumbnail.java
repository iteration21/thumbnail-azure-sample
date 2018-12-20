package example.functions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.microsoft.azure.storage.blob.BlobRange;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.TransferManager;
import com.microsoft.rest.v2.util.FlowableUtil;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import example.config.Properties;
import example.functions.Thumbnail.Input;
import example.functions.Thumbnail.Output;
import lombok.Data;

/**
 * @author optim-y-takahashi
 */
public class Thumbnail implements Function<Input, Output> {

	private final Properties properties;
	private final ContainerURL containerURL;

	public Thumbnail(final Properties properties, final ContainerURL containerURL) {
		this.properties = properties;
		this.containerURL = containerURL;		
	}
	
	@Override
	public Output apply(Input input) {
		System.out.println(input);

		final String blobName = input.getBlobName();
		final String thumbnailBlobName = input.getBlobName() + "-thumbnail";
		try {
			final File file = Files.createTempFile("serverless-source-image", null).toFile();
			final BlockBlobURL blockBlobURL = containerURL.createBlockBlobURL(blobName);

			blockBlobURL.download(new BlobRange().withOffset(0).withCount(4 * 1024 * 1024L), null, false, null)
					.flatMapCompletable(response -> {
						final AsynchronousFileChannel channel = AsynchronousFileChannel.open(
								Paths.get(file.getAbsolutePath()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
						return FlowableUtil.writeFile(response.body(null), channel);
					}).doOnComplete(() -> logInfo("File is downloaded to %s.", file))
					.doOnError(error -> logError("Failed to download file from blob %s with error %s.",
							blockBlobURL.toURL(), error.getMessage()))
					.blockingAwait();

			final BufferedImage source = ImageIO.read(file);
			final ResampleOp resampleOp = new ResampleOp(
					DimensionConstrain.createMaxDimension(properties.getMaxWidth(), properties.getMaxHeight(), true));
			resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			final BufferedImage resized = resampleOp.filter(source, null);
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(resized, properties.getImageFormat(), os);
			final InputStream is = new ByteArrayInputStream(os.toByteArray());

			final File thumbnali = Files.createTempFile("serverless-thumbnali-image", null).toFile();
			FileUtils.copyInputStreamToFile(is, thumbnali);
			final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(thumbnali.toPath());
			final BlockBlobURL blockBlobThumbnailURL = containerURL.createBlockBlobURL(thumbnailBlobName);

			TransferManager
					.uploadFileToBlockBlob(fileChannel, blockBlobThumbnailURL, BlockBlobURL.MAX_STAGE_BLOCK_BYTES, null)
					.ignoreElement().doOnComplete(() -> logInfo("File %s is uploaded.", thumbnali.toPath()))
					.doOnError(error -> logError("Failed to upload file %s with error %s.", thumbnali.toPath(),
							error.getMessage()))
					.blockingAwait();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new Output(thumbnailBlobName);
	}

	@Data
	public static final class Input {
		private String blobName;
	}

	@Data
	public static final class Output {
		private String blobName;

		public Output(final String blobName) {
			this.blobName = blobName;
		}
	}

	private static void logInfo(String log, Object... params) {
		System.out.println(String.format(log, params));
	}

	private static void logError(String log, Object... params) {
		System.err.println(String.format(log, params));
	}

}
