package gse.pathfinder;

import gse.pathfinder.models.Tower;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class TowerDialog extends DialogFragment {
	private Tower tower;
	private TextView txtRegion;
	private TextView txtLinename;
	private TextView txtName;

	public TowerDialog(Tower tower) {
		this.tower = tower;
	}

	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.fragment_tower, null);

		txtName = (TextView) view.findViewById(R.id.name_tower_fragment);
		txtLinename = (TextView) view.findViewById(R.id.linename_tower_fragment);
		txtRegion = (TextView) view.findViewById(R.id.region_tower_fragment);

		builder.setView(view);
		builder.setTitle("ანძის თვისებები");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				TowerDialog.this.dismiss();
			}
		});

		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		txtName.setText("#" + tower.getName());
		txtLinename.setText(tower.getLinename());
		txtRegion.setText(tower.getRegion());
	}

}
