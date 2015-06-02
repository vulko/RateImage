package home24.testtask.rateimage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import home24.testtask.rateimage.R;
import home24.testtask.rateimage.backend.RemoteDataLoader;
import home24.testtask.rateimage.datamodel.AppModel;
import home24.testtask.rateimage.datamodel.RatableImage;


public class RateImageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks< List<RatableImage> > {
	
	private static final int LOADER_ID = 0;

	private GestureDetector mGestureDetector;
	private ViewFlipper mViewFlipper = null;
	private Animation flipInAnimation = null;
	private Animation flipOutLeftAnimation = null;
	private Animation flipOutRightAnimation = null;
	private View mImgLoadingBar = null;
	private ImageView mFirstImageHolder = null;
	private ImageView mSecondImageHolder = null;
	private boolean flippingEnabled;
	private ProgressDialog mLoadingDataProgressDlg = null;
	
	private List<RatableImage> mImagesList = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main_screen);
        
        // init view flipper and animations
        mViewFlipper = (ViewFlipper) findViewById(R.id.imageFlipper);       
        flipInAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_in);
        flipOutLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_out_left);
        flipOutRightAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_out_right);
        
        // init loading bars and image holders
        mImgLoadingBar = (View) findViewById(R.id.imgLoadingBar);
        mFirstImageHolder = (ImageView) findViewById(R.id.firstImageHolder);
        mSecondImageHolder = (ImageView) findViewById(R.id.secondImageHolder);
        mImgLoadingBar.setVisibility(View.GONE);
        
        // init gesture detector
        mGestureDetector = new GestureDetector(this, mGestureListener);
        
        // init like/dislike buttons
        ImageButton btnLike = (ImageButton) findViewById(R.id.btnLike);
        btnLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLikeImage();
			}
		});
        ImageButton btnDislike = (ImageButton) findViewById(R.id.btnDislike);
        btnDislike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDislikeImage();
			}
		});
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	// load images from server
    	flippingEnabled = false;
    	getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    	// show loading dialog
		mLoadingDataProgressDlg = ProgressDialog.show(RateImageActivity.this,
													  "Please wait ...",
													  "Downloading data...",
													  true);
    }
    
    private void showLikedImagesList() {
        Fragment listFragment = new LikedImagesListFragment();
        listFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, listFragment);
        ft.commit();    	
    }
    
	@Override
	public Loader<List<RatableImage>> onCreateLoader(int arg0, Bundle arg1) {
		// show loading animation and start loader
        mImgLoadingBar.setVisibility(View.VISIBLE);
		RemoteDataLoader loader = new RemoteDataLoader(this);
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<RatableImage>> arg0,	List<RatableImage> imgList) {
		// dismiss loading dialog
		mLoadingDataProgressDlg.dismiss();
		mLoadingDataProgressDlg = null;
		
		if (imgList != null && imgList.size() > 0) {
			mImagesList = imgList;
			// load images to ImageViews
    		loadRemoteImgToImageView(mImagesList.get(0).getPath(), mFirstImageHolder, mImgLoadingBar);
    		loadRemoteImgToImageView(mImagesList.get(1).getPath(), mSecondImageHolder, mImgLoadingBar);
    		// enable flipping
    		flippingEnabled = true;
		} else {
			// TODO handle if loading finished, but no items returned
			Toast.makeText(this, "Failed to load items! Apparently no internet connection.", Toast.LENGTH_LONG).show();
			// disable flipping
			flippingEnabled = false;
		}
	}

	@Override
	public void onLoaderReset(Loader<List<RatableImage>> arg0) {
		// TODO show dummy image or loading animation
		flippingEnabled = false;
	}    
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if (!flippingEnabled) {
    		return false;
    	}
    	
    	return mGestureDetector.onTouchEvent(event);
	}
    
    private void loadNextImage() {   	
    	if (mImagesList.size() > 0) {
			mImagesList.remove(0);    		
    	}
    	
    	// the following code works with assumption that there are 10 items in the list after loading
    	if (mImagesList.size() > 1) { // if list contains 2 or more items
        	if(mImagesList.size() % 2 == 0) {
        		// if the size is even, load new item to first ImageView in ViewFlipper
        		mSecondImageHolder.setImageResource(R.drawable.placeholder);
        		loadRemoteImgToImageView(mImagesList.get(0).getPath(), mFirstImageHolder, mImgLoadingBar);
        	} else {
        		// if the size is odd, load new items to second ImageView in ViewFlipper
        		mFirstImageHolder.setImageResource(R.drawable.placeholder);
        		loadRemoteImgToImageView(mImagesList.get(0).getPath(), mSecondImageHolder, mImgLoadingBar);
        	}
    	} else if (mImagesList.size() > 0) { // if list contains 1 last item
    		mFirstImageHolder.setImageResource(R.drawable.placeholder);
    		loadRemoteImgToImageView(mImagesList.get(0).getPath(), mSecondImageHolder, mImgLoadingBar);
    	} else { // if there are no more images, show list of liked ones
    		mSecondImageHolder.setVisibility(View.GONE);
    		mFirstImageHolder.setVisibility(View.GONE);    		
    		flippingEnabled = false;
    		// show list of liked images
    		showLikedImagesList();
		}
    }
    
    private void loadRemoteImgToImageView(String url, final ImageView imgView, final View loadingBar) {
		// set dummy image while loading
		imgView.setImageResource(R.drawable.placeholder);
		// show loading bar while the image is loaded from remote location
		loadingBar.setVisibility(View.VISIBLE);
		// disable like/dislike while image is loading
		flippingEnabled = false;
		
    	AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);
					// TODO: it's better to cache files locally, so there's no need to load them every time
					
					return BitmapFactory.decodeStream(url.openConnection().getInputStream());
				} catch (MalformedURLException e) {
					// TODO: handle exception
					
					return null;
				} catch (IOException e) {
					// TODO: handle exception
					
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(Bitmap bmp) {
				if (bmp != null) {
					// update view
					loadingBar.setVisibility(View.GONE);
					imgView.setImageBitmap(bmp);
					// enable like/dislike
					flippingEnabled = true;
				} else {
					// TODO: handle loading error
				}
			}

		};
		asyncTask.execute(url);
    }
    
    public void onDislikeImage() {
    	// animate swipe out of screen and show next image
    	mViewFlipper.setOutAnimation(flipOutLeftAnimation);
    	mViewFlipper.setInAnimation(flipInAnimation);
    	
    	// load next
    	mViewFlipper.showNext();
    	loadNextImage();
    }
    
    public void onLikeImage() {
    	// animate swipe out of screen and show next image
    	mViewFlipper.setOutAnimation(flipOutRightAnimation);
    	mViewFlipper.setInAnimation(flipInAnimation);
    	
    	// add to liked images list and load next
    	if (mImagesList.size() > 0) {
        	AppModel.getInstance().getLikedImageList().add(mImagesList.get(0));    		
    	}
    	mViewFlipper.showNext();
    	loadNextImage();
    }

	SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
		// threshold for swipe to be detected
		private final float DETECTION_THRESHOLD = 50.0f;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY) {
			float deltaX = e1.getX() - e2.getX();
			
			if ( deltaX > DETECTION_THRESHOLD ) {			// swipe left
				RateImageActivity.this.onDislikeImage();
			} else if ( deltaX < -DETECTION_THRESHOLD ) {	// swipe right
				RateImageActivity.this.onLikeImage();
			}

			return true;
		}
	};

}
