package com.cspi.ezams.common.dao.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Repository;
import com.cspi.ezams.common.dao.CommonDAO;

@Repository
public class CommonDAOImpl implements CommonDAO {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "userinfo")
	private CompositeConfiguration configuration;

	@Resource(name = "serverinfo")
	private CompositeConfiguration configuration2;

	@Override
	public boolean userCheck(String userId, String userPw) {
		if (userPw.equals(configuration.getString("user.password"))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public JSONArray selectFileDb(String folderPath, String fileName, String searchColumn, String searchKeyword)
			throws Exception {

		JSONParser parser = new JSONParser();

		JSONArray json = null;
		Object obj;
		obj = (Object) parser.parse(new BufferedReader(new FileReader(folderPath + "\\" + fileName)));

		String sortColumn = "no";
		String sortType = "int";
		json = SelectSortJson(sortColumn, sortType, obj);
		String selectColumn = searchColumn;
		String searchString = searchKeyword;

		if (!selectColumn.isEmpty() && !searchString.isEmpty() && selectColumn.length() > 0
				&& searchString.length() > 0) {
			json = SearchColumnJson(selectColumn, searchString, json);
		}
		json = removeDuplicationJson(json);

		return json;
	}

	private JSONArray removeDuplicationJson(JSONArray json) {
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < json.size() && json != null; i++) {
			if (json.size() - 1 != i
					&& json.getJSONObject(i).get("no").toString().trim()
							.equals(json.getJSONObject(i + 1).get("no").toString().trim())
					&& json.getJSONObject(i).get("occuredTime").toString().trim()
							.equals(json.getJSONObject(i + 1).get("occuredTime").toString().trim())) {

			} else {
				jsonValues.add(json.getJSONObject(i));
			}
		}

		JSONArray sortedJsonArray = new JSONArray();
		for (int i = 0; i < jsonValues.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}
		return sortedJsonArray;
	}

	private JSONArray SearchColumnJson(String selectColumn, String searchString, JSONArray obj) throws Exception {

		String jsonArrStr = obj.toString();
		JSONArray jsonArr = new JSONArray();
		jsonArr = JSONArray.fromObject(jsonArrStr);

		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArr.size() && jsonArr != null; i++) {
			if (jsonArr.getJSONObject(i).get(selectColumn).toString().trim().equals(searchString)) {
				if (jsonArr.size() - 1 != i && jsonArr.getJSONObject(i).get("no").toString().trim()
						.equals(jsonArr.getJSONObject(i + 1).get("no").toString().trim())) {

				} else {
					jsonValues.add(jsonArr.getJSONObject(i));
				}
			}
		}
		JSONArray sortedJsonArray = new JSONArray();
		for (int i = 0; i < jsonValues.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}
		return sortedJsonArray;
	}

	protected JSONArray SelectSortJson(final String sortColumn, final String sortType, Object obj) throws Exception {

		String jsonArrStr = obj.toString();
		JSONArray jsonArr = new JSONArray();
		jsonArr = JSONArray.fromObject(jsonArrStr);

		List<JSONObject> jsonValues = new ArrayList<JSONObject>();

		for (int i = 0; i < jsonArr.size(); i++) {
			jsonValues.add(jsonArr.getJSONObject(i));
		}

		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			private final String KEY_NAME = sortColumn;

			@Override
			public int compare(JSONObject a, JSONObject b) {
				int rtn = 0;

				if (sortType.equals("int")) {
					int intValA = 0;
					int intValB = 0;
					try {
						intValA = (int) a.get(KEY_NAME);
						intValB = (int) b.get(KEY_NAME);
					} catch (JSONException e) {
						log.debug(e);
					}
					rtn = (intValA - intValB);
				} else {
					Integer intValA = (int) a.get(KEY_NAME);
					Integer intValB = (int) b.get(KEY_NAME);
					return intValA.compareTo(intValB);
				}
				return rtn;
			}
		});

		JSONArray sortedJsonArray = new JSONArray();
		for (int i = 0; i < jsonArr.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}
		return sortedJsonArray;
	}
}