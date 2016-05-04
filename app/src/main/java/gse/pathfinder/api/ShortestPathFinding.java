package gse.pathfinder.api;

import android.app.Activity;
import android.os.AsyncTask;

import java.security.PublicKey;
import java.util.List;

import gse.pathfinder.MapActivity;
import gse.pathfinder.TowerDialog;
import gse.pathfinder.api.ApplicationController;
import gse.pathfinder.models.PathLines;
import gse.pathfinder.models.Point;
import gse.pathfinder.ui.BaseActivity;

public class ShortestPathFinding extends AsyncTask<Point, Integer, List<PathLines>> {
    private Exception ex;

    public BaseActivity  activity;

    public ShortestPathFinding(BaseActivity baseActivity){
        activity=baseActivity;
    }

    public Activity getActivity(){
        return activity;
    }

    @Override
    protected List<PathLines> doInBackground(Point... params) {
        try {
            String username = ApplicationController.getCurrentUser().getUsername();
            String password = ApplicationController.getCurrentUser().getPassword();
            Point from = params[0];
            Point to = params[1];
            return ApplicationController.shortestPath(getActivity(), username, password, from, to);
        } catch (Exception ex) {
            this.ex = ex;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<PathLines> result) {
        super.onPostExecute(result);
        if(((MapActivity) getActivity()).getWaitDialog()!=null) {
            ((MapActivity) getActivity()).getWaitDialog().dismiss();
        }
        if (result != null) {
            ((MapActivity) getActivity()).clearShortestPathLayer();
            ((MapActivity) getActivity()).setPathLines(result);

            Double length=0.0d;
            for(PathLines pathLine:result) {
                ((MapActivity) getActivity()).displayShortestPath(pathLine.getPoints(),pathLine.getColor() );
                length+=Double.parseDouble(pathLine.getLength());
            }
            ((MapActivity) getActivity()).setRenderedPathLength(length);
        } else {
            ((MapActivity) getActivity()).error(ex);
            ex.printStackTrace();
        }
    }

};