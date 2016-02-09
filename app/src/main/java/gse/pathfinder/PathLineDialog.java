package gse.pathfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.api.NetworkUtils;
import gse.pathfinder.models.PathLines;
import gse.pathfinder.models.Point;
import gse.pathfinder.models.Tower;
import gse.pathfinder.sql.TowerUtils;
import gse.pathfinder.sql.TrackUtils;
import gse.pathfinder.ui.UiUtils;

@SuppressLint("ValidFragment")
public class PathLineDialog extends DialogFragment {
	static final int REQUEST_IMAGE_CAPTURE = 100;

	private TextView txtLinename;
	private TextView txtLineTypeName;
	private TextView txtSurfaceName;
	private TextView txtLength;
	private TextView txtDdescription;


	private PathLines pathLines;

	@SuppressLint("ValidFragment")
	public PathLineDialog(PathLines pathLines) {
		this.pathLines = pathLines;
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_pathline, null);
		txtLinename=(TextView) view.findViewById(R.id.line_name_pathline_fragment);
		txtLineTypeName = (TextView) view.findViewById(R.id.line_type_pathline_fragment);
		txtSurfaceName = (TextView) view.findViewById(R.id.surface_name_pathline_fragment);
		txtLength=(TextView) view.findViewById(R.id.length_pathline_fragment);
		txtDdescription=(TextView) view.findViewById(R.id.description_pathline_fragment);
		builder.setView(view);
		builder.setTitle("გზის თვისებები");
		builder.setPositiveButton("OK", null);
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		UiUtils.showTextWithHeder(txtLineTypeName, "გზის სახეობა", pathLines.getLineTypeName());
		UiUtils.showTextWithHeder(txtSurfaceName, "ზედაპირი", pathLines.getSurficeName());
		UiUtils.showText(txtDdescription, pathLines.getDescription());
		UiUtils.showTextWithHeder(txtLength,"მანძილი", pathLines.getLength());
		UiUtils.showTextWithHeder(txtLinename, "დასახელება",pathLines.getLineName());
		// button listeners

		AlertDialog d = (AlertDialog) getDialog();

		Button ok_button = d.getButton(AlertDialog.BUTTON_POSITIVE);
		ok_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PathLineDialog.this.dismiss();
			}
		});

	}

}
