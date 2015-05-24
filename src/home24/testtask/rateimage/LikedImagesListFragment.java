package home24.testtask.rateimage;

import home24.testtask.rateimage.datamodel.AppModel;
import home24.testtask.rateimage.datamodel.RatableImage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LikedImagesListFragment extends ListFragment {
	
	private LikedImagesAdapter mAdapter = null;
    static final int INTERNAL_LIST_CONTAINER_ID = 16711683;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.liked_images_screen, container, false);
        view.findViewById(R.id.list_container_id).setId(INTERNAL_LIST_CONTAINER_ID);
	    
	    return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new LikedImagesAdapter(getActivity(), AppModel.getInstance().getLikedImageList());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		setListAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mAdapter.clear();
		mAdapter = null;
	}
	

	public class LikedImagesAdapter extends ArrayAdapter<RatableImage> {

		private class ViewHolder {
			TextView tvSKU;
			ImageView ivIMG;
		}

		public LikedImagesAdapter(Context context, ArrayList<RatableImage> users) {
			super(context, R.layout.liked_image_row, users);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RatableImage ratedImg = getItem(position);

			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.liked_image_row, parent, false);
				viewHolder.ivIMG = (ImageView) convertView.findViewById(R.id.ivIMG);
				viewHolder.tvSKU = (TextView) convertView.findViewById(R.id.tvSKU);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tvSKU.setText(ratedImg.getSku());
			viewHolder.ivIMG.setImageResource(R.drawable.placeholder);
			// load remote image
	    	AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(String... params) {
					try {
						URL url = new URL(params[0]);
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
						viewHolder.ivIMG.setImageBitmap(bmp);
					} else {
						// TODO: handle loading error
					}
				}

			};
			asyncTask.execute(ratedImg.getPath());

			return convertView;
		}
	}
	
}
