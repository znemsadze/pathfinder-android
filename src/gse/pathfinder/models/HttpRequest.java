package gse.pathfinder.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequest implements Serializable {
	private static final long serialVersionUID = 3116528073764034824L;

	private int id;
	private String url;
	private List<HttpRequestParam> parameters = new ArrayList<HttpRequestParam>();

	public static HttpRequest newRequest(String url, List<NameValuePair> params) {
		HttpRequest request = new HttpRequest();
		request.setUrl(url);
		for (NameValuePair pair : params) {
			HttpRequestParam param = new HttpRequestParam();
			param.setName(pair.getName());
			param.setValue(pair.getValue());
			request.getParameters().add(param);
		}
		return request;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<HttpRequestParam> getParameters() {
		return parameters;
	}

	public List<NameValuePair> getParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (HttpRequestParam param : getParameters()) {
			params.add(new BasicNameValuePair(param.getName(), param.getValue()));
		}
		return params;
	}
}
