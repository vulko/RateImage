package home24.testtask.rateimage.backend;

import home24.testtask.rateimage.datamodel.RatableImage;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class RemoteDataLoader extends AsyncTaskLoader< List<RatableImage> > {
	
	protected List<RatableImage> mLastDataList = null;
	protected BackendLoaderAdapter mBackendLoaderAdapter = null;

	public RemoteDataLoader(Context context) {
		super(context);
		// init adapter that loads data
		mBackendLoaderAdapter = new BackendLoaderAdapter();
	}
	
	protected List<RatableImage> buildList() {
		return mBackendLoaderAdapter.loadRemoteData();
	}

	@Override
	public List<RatableImage> loadInBackground() {
		return buildList();
	}

	@Override
	public void deliverResult(List<RatableImage> dataList) {
		// if loader is stopped, release data
		if (isReset()) {
			emptyDataList(dataList);
			
			return;
		}
		
		List<RatableImage> oldDataList = mLastDataList;
		mLastDataList = dataList;
		
		// deliver loaded data
		if (isStarted()) {
			super.deliverResult(dataList);
		}
		
		// release data from previous load
		if (oldDataList != dataList) {
			emptyDataList(oldDataList);
		}
	}
	
	@Override
	protected void onStartLoading() {
		// if data is already loaded deliver it
		if (mLastDataList != null) {
			deliverResult(mLastDataList);
		}
		
		// reload if data was changed (after invoking onContentChanged())
		//        or if data was released
		if (takeContentChanged()
				|| mLastDataList == null
				|| mLastDataList.size() == 0) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		// try to cancel loading
		cancelLoad();
	}
	
	@Override
	public void onCanceled(List<RatableImage> dataList) {
		// release data
		emptyDataList(dataList);
	}
	
	protected void emptyDataList(List<RatableImage> dataList) {
		if (dataList != null && dataList.size() > 0) {
			dataList.clear();
		}
	}
	
}
