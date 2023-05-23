import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
	BufferedImage image = new BufferedImage(null, null, false, null);

	// ImageIO.read(PerformanceTest.class.getResource("donnees/lena.jpg"));
	public static void main(String[] args) throws IOException {
		Photo lena = new Photo();

		lena.noising(lena.getPhoto(), 10);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(lena.getph)));
        frame.pack();
        frame.setVisible(true);
    }

	}	
}
