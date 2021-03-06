package com.dian.diabetes.activity.set;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dian.diabetes.R;
import com.dian.diabetes.activity.BasicActivity;
import com.dian.diabetes.activity.set.adapter.ItemNewsAdapter;
import com.dian.diabetes.widget.anotation.ViewInject;

public class HelpActivity extends BasicActivity implements OnClickListener{

	@ViewInject(id = R.id.back_btn)
	private Button backBtn;
	@ViewInject(id = R.id.list)
	private ListView listview;

	private List<List<QuestionDto>> data;
	private ItemNewsAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_layout);
		initActivity();
	}

	private void initActivity() {
		backBtn.setOnClickListener(this);
		data = new ArrayList<List<QuestionDto>>();
		data.addAll(readFile().values());
		adapter = new ItemNewsAdapter(context, data);
		listview.setAdapter(adapter);
	}

	private Map<String, List<QuestionDto>> readFile() {
		Map<String, List<QuestionDto>> maps = new LinkedHashMap<String, List<QuestionDto>>();
		InputStream in = null;
		BufferedReader bufferReader = null;
		String str = "";
		String[] supstr;
		int index = 1;
		try {
			in = context.getAssets().open("help.txt");
			bufferReader = new BufferedReader(
					new InputStreamReader(in, "UTF-8"));
			while ((str = bufferReader.readLine()) != null) {
				supstr = str.split("_");
				List<QuestionDto> dtolist = maps.get(supstr[0]);
				if (dtolist == null) {
					dtolist = new ArrayList<QuestionDto>();
					maps.put(supstr[0], dtolist);
				}
				QuestionDto dto = new QuestionDto();
				dto.index = index;
				dto.name = supstr[1];
				dto.parent = supstr[0];
				dtolist.add(dto);
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return maps;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.back_btn:
			finish();
			break;
		}
	}

}
