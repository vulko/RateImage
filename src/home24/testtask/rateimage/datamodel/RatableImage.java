package home24.testtask.rateimage.datamodel;

public class RatableImage {
	
	private String sku = null;
	private String path = null;
	private boolean liked = false;
	
	public RatableImage(String path, boolean liked) {
		this.path = path;
		this.liked = liked;
	}

	public RatableImage(String sku, String path, boolean liked) {
		this.sku = sku;
		this.path = path;
		this.liked = liked;
	}

	public boolean isLiked() { return liked; }
	public void setLiked(boolean liked) { this.liked = liked; }
	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }
	public String getSku() { return sku; }
	public void setSku(String sku) { this.sku = sku; }

}