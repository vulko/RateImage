package home24.testtask.rateimage.datamodel;

import java.util.ArrayList;

public class AppModel {

	// Singleton
	private static final AppModel mInstance = new AppModel();
	private AppModel() {}
	public static AppModel getInstance() { return mInstance; }
	
	// Liked items
	private ArrayList<RatableImage> mLikedImageList = new ArrayList<RatableImage>();
	public ArrayList<RatableImage> getLikedImageList() { return mLikedImageList; }

}
