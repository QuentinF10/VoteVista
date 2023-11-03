import java.awt.image.BufferedImage;

public interface QRCodeResultListener {
    void onQRCodeResult(String info, BufferedImage image);
}
