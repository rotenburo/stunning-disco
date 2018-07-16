import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.base.AppBase;
import com.util.IOUtil;


public class resize2 extends AppBase {
	public static void main(String[] args) throws Exception{
		resize2 ins = new resize2();
		ins.processing();
	}

	@Override
	public void processing() throws Exception{


		String folderName = IOUtil.getString("Folder?：");
		File folder = new File(folderName);
		File[] arrFile = folder.listFiles();
			for(File f:arrFile){
				if(f.isFile()){
					//which file?
					String fileName = f.getPath();
					String Name = f.getName();
					BufferedImage img = ImageIO.read(new File(fileName));

					//get new size
					Dimension imgSize = new Dimension(img.getWidth(), img.getHeight());
					Dimension boundary = new Dimension(500, 500);
					Dimension newSize = getScaledDimension(imgSize,boundary);

					//resizing
					BufferedImage resizedImg = resizing(img,(int)newSize.getWidth(),(int)newSize.getHeight());

					//rectangle Img(500*500)
					BufferedImage backImg = mkBackImg();

					//combine
					BufferedImage combinedImg = combining(backImg,resizedImg);

					//out file
					String newfileName = fileName.substring(0, fileName.lastIndexOf("\\"))+File.separator+"resize"+File.separator+Name;
					imgSave(combinedImg,newfileName);
					comppress(newfileName);
				}
			}

	}
	private void comppress(String newfileName) throws IOException {
		File input = new File(newfileName);
		BufferedImage image = ImageIO.read(input);
		String getfileName=newfileName.substring(newfileName.lastIndexOf("\\")+1,newfileName.length());
		String[] arr = getfileName.split("\\.");
		String compfileName =newfileName.substring(0, newfileName.lastIndexOf("\\"))+File.separator+ "(comp)"+arr[0]+"."+arr[1];
		File compressedImageFile = new File(compfileName);
		OutputStream os =new FileOutputStream(compressedImageFile);

		Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
		ImageWriter writer = (ImageWriter) writers.next();

		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		writer.setOutput(ios);

		ImageWriteParam param = writer.getDefaultWriteParam();

		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(0.5f);
		writer.write(null, new IIOImage(image, null, null), param);

		os.close();
		ios.close();
		writer.dispose();
	}

	/**
	 * make background canvas using some image
	 * @return		500*500 canvas
	 * @throws IOException
	 */
	private BufferedImage mkBackImg() throws IOException {
		BufferedImage b_img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D    graphics = b_img.createGraphics();

		graphics.setPaint (Color.WHITE);
		graphics.fillRect ( 0, 0, 500, 500 );
		return b_img;
	}

	private BufferedImage combining(BufferedImage backImg, BufferedImage resizedImg) {

		//for centering
		int Bw = 0;
		int Bh = 0;
		if(resizedImg.getWidth()==500)
			Bh = (500-resizedImg.getHeight())/2;
		else if(resizedImg.getHeight()==500)
			Bw = (500-resizedImg.getWidth())/2;
		else
			Bh = (500-resizedImg.getHeight())/2;
			Bw = (500-resizedImg.getWidth())/2;

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(backImg.getWidth(), resizedImg.getWidth());
		int h = Math.max(backImg.getHeight(), resizedImg.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(backImg, 0, 0, null);
		g.drawImage(resizedImg, Bw, Bh, null);
		return combined;
	}

	/**
	 * save bufferedImage to file
	 * @param img			bufferedImage image
	 * @param fileName		saved file
	 * @throws IOException
	 */
	private void imgSave(BufferedImage img, String fileName) throws IOException {
		File outputfile = new File(fileName);
		ImageIO.write(img, "jpg", outputfile);
	}

	public static BufferedImage resizing(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

	    int original_width = imgSize.width;
	    int original_height = imgSize.height;
	    int bound_width = boundary.width;
	    int bound_height = boundary.height;
	    int new_width = original_width;
	    int new_height = original_height;

	    // first check if we need to scale width
	    if (original_width > bound_width) {
	        //scale width to fit
	        new_width = bound_width;
	        //scale height to maintain aspect ratio
	        new_height = (new_width * original_height) / original_width;
	    }

	    // then check if we need to scale even with the new height
	    if (new_height > bound_height) {
	        //scale height to fit instead
	        new_height = bound_height;
	        //scale width to maintain aspect ratio
	        new_width = (new_height * original_width) / original_height;
	    }

	    return new Dimension(new_width, new_height);
	}

}
