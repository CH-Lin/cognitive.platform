package prj.cognitive.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import javax.imageio.ImageIO;

public class Images {
	private static final String IMAGE_FORMAT = Config.get(Images.class, "IMAGE_FORMAT", "jpg");

	private static final String MIME_TYPE = Config.get(Images.class, "MIME_TYPE", "image/jpeg");

	public static byte[] toByteArray(BufferedImage source) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(source, IMAGE_FORMAT, outputStream);
			return outputStream.toByteArray();
		} catch (IOException exc) {
			return null;
		}
	}

	public static String toBase64(BufferedImage source) {
		return Base64.getEncoder().encodeToString(toByteArray(source));
	}

	public static String toBase64Uri(BufferedImage source) {
		return "data:" + MIME_TYPE + ";base64," + toBase64(source);
	}

	public static Optional<BufferedImage> fromBase64(String data) {
		try(ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data))) {
			return Optional.of(ImageIO.read(input));
		} catch (IOException exc) {
			return Optional.empty();
		}
	}

	public static BufferedImage resize(BufferedImage source, int width, int height) {
		AffineTransform transform = new AffineTransform();
		transform.scale(
				(double) width / (double) source.getWidth(),
				(double) height / (double) source.getHeight()
		);

		return (new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR)).filter(
				source,
				new BufferedImage(
						width,
						height,
						source.getType()
				)
		);
	}

	public static BufferedImage withMinimumSize(BufferedImage source, int width, int height) {
		double ratio = Math.max(
				((double) width) / ((double) source.getWidth()),
				((double) height) / ((double) source.getHeight())
		);

		if (ratio <= 1.0) {
			return source;
		} else {
			return resize(
					source,
					(int) Math.round(ratio * source.getWidth()),
					(int) Math.round(ratio * source.getHeight())
			);
		}
	}
}
