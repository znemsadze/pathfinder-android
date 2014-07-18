package gse.pathfinder;

import gse.pathfinder.api.NetworkUtils;
import gse.pathfinder.models.Tower;
import gse.pathfinder.ui.UiUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TowerDialog extends DialogFragment {
	private Tower tower;
	private TextView txtRegion;
	private TextView txtLinename;
	private TextView txtName;
	private TextView txtCategory;
	private TextView txtNote;
	private ProgressBar prgDownload;
	private ImageView imgTower;
	private TextView imgCount;
	private List<String> files;
	private int currImage;
	private View imageLayout;

	public TowerDialog(Tower tower) {
		this.tower = tower;
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.fragment_tower, null);

		txtName = (TextView) view.findViewById(R.id.name_tower_fragment);
		txtLinename = (TextView) view.findViewById(R.id.linename_tower_fragment);
		txtRegion = (TextView) view.findViewById(R.id.region_tower_fragment);
		txtCategory = (TextView) view.findViewById(R.id.category_tower_fragment);
		txtNote = (TextView) view.findViewById(R.id.note_tower_fragment);
		prgDownload = (ProgressBar) view.findViewById(R.id.progress_tower_fragment);
		imgTower = (ImageView) view.findViewById(R.id.image_view_tower_fragment);
		imgCount = (TextView) view.findViewById(R.id.image_count_tower_fragment);
		imageLayout = view.findViewById(R.id.image_layout_tower_fragment);

		builder.setView(view);
		builder.setTitle("ანძის თვისებები");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				TowerDialog.this.dismiss();
			}
		});

		final GestureDetector gdt = new GestureDetector(getActivity(), new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if (velocityX > 500) { // prev
					if (currImage > 0) showImage(currImage - 1);
				} else if (velocityX < -500) { // next
					if (currImage < files.size() - 1) showImage(currImage + 1);
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});

		imgTower.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gdt.onTouchEvent(event);
				return true;
			}
		});

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();

		txtName.setText("#" + tower.getName());
		UiUtils.showText(txtLinename, tower.getLinename());
		UiUtils.showText(txtRegion, tower.getRegion());
		UiUtils.showText(txtCategory, tower.getCategory());
		UiUtils.showText(txtNote, tower.getDescription());

		if (tower.getImages().isEmpty()) {
			displayImages(null);
		} else {
			if (null == files || files.isEmpty()) {
				imageLayout.setVisibility(View.GONE);
				prgDownload.setVisibility(View.VISIBLE);
				new ImageDownload().execute(tower.getImages().toArray(new String[] {}));
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (files != null) {
			for (String f : files) {
				new File(f).delete();
			}
			files.clear();
		}
	}

	private void displayImages(List<String> images) {
		this.files = images;
		prgDownload.setVisibility(View.GONE);
		if (images != null && !images.isEmpty()) {
			imageLayout.setVisibility(View.VISIBLE);
			showImage(0);
		} else {
			imageLayout.setVisibility(View.GONE);
		}
	}

	private void showImage(int index) {
		this.currImage = index;
		Bitmap myBitmap = BitmapFactory.decodeFile(files.get(index));
		imgTower.setImageBitmap(myBitmap);
		imgCount.setText((currImage + 1) + " / " + files.size());
	}

	private class ImageDownload extends AsyncTask<String, Integer, List<String>> {
		@Override
		protected List<String> doInBackground(String... params) {
			List<String> files = new ArrayList<String>();
			for (String url : params) {
				try {
					files.add(NetworkUtils.downloadFile(TowerDialog.this.getActivity(), url));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return files;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			displayImages(result);
		}
	};
}
