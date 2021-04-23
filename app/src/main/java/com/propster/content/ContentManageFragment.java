package com.propster.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.landlord.LandlordAddPropertyActivity;
import com.propster.landlord.LandlordPropertyListAdapter;
import com.propster.landlord.LandlordPropertyListItem;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentManageFragment extends Fragment {

    private final ContentTabPageAdapter contentTabPageAdapter;
    private final boolean firstTime;

    // landlord
    private TextView landlordManageFirstTimeLoginLabel;
    private ListView landlordManagePropertyList;
    private Button landlordManageAddPropertyButton;
    private LandlordPropertyListAdapter propertyListAdapter;

    // all
    private RequestQueue requestQueue;

    public ContentManageFragment(ContentTabPageAdapter contentTabPageAdapter, boolean firstTime) {
        this.contentTabPageAdapter = contentTabPageAdapter;
        this.firstTime = firstTime;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.contentTabPageAdapter.getRole() == Constants.ROLE_LANDLORD) {
            return inflater.inflate(R.layout.activity_landlord_content_manage, container, false);
        } else {
            return inflater.inflate(R.layout.activity_tenant_content_manage, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requestQueue = Volley.newRequestQueue(view.getContext());
        if (this.contentTabPageAdapter.getRole() == Constants.ROLE_LANDLORD) {
            this.initializeLandlordManageView(view);
        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY) {
            if (resultCode == Activity.RESULT_OK) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
                this.refreshPropertyListView();
            }
        }
    }

    private void refreshPropertyListView() {
        View view = getView();
        if (view == null) {
            this.landlordManageGetPropertyListFailed("Internet down.");
            return;
        }
        Context context = view.getContext();
        if (context == null) {
            this.landlordManageGetPropertyListFailed("Internet down.");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManageGetPropertyListFailed("Please relogin.");
            return;
        }

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("session_id", sessionId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_LIST, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    landlordManageGetPropertyListSuccess(response.getJSONArray("property_list"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    landlordManageGetPropertyListFailed(e.getLocalizedMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                landlordManageGetPropertyListFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);

//        this.landlordManageGetPropertyListSuccess();

    }

    private void landlordManageGetPropertyListSuccess(JSONArray propertyListJasonArray) {
        this.refreshPropertyList();
        try {
            JSONObject propertyItemJsonObject;
            for (int i = 0; i < propertyListJasonArray.length(); i++) {
                propertyItemJsonObject = propertyListJasonArray.getJSONObject(i);
                this.addPropertyItemIntoList(propertyItemJsonObject.getString("property_name"), propertyItemJsonObject.getInt("tenant_count"), (float) propertyItemJsonObject.getDouble("payment"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void landlordManageGetPropertyListFailed(String landlordGetPropertyListFailed) {

    }

//    private int tmp = 0;

    private void initializeLandlordManageView(View view) {
        this.landlordManageFirstTimeLoginLabel = view.findViewById(R.id.landlordManageFirstTimeLoginLabel);
        this.landlordManagePropertyList = view.findViewById(R.id.landlordManagePropertyList);
        ArrayList<LandlordPropertyListItem> propertyListItemArrayList = new ArrayList<>();
        this.propertyListAdapter = new LandlordPropertyListAdapter(view.getContext(), propertyListItemArrayList);
        this.landlordManagePropertyList.setAdapter(this.propertyListAdapter);
        this.landlordManagePropertyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        this.landlordManageAddPropertyButton = view.findViewById(R.id.landlordManageAddPropertyButton);
        this.landlordManageAddPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent landlordAddPropertyIntent = new Intent(getActivity(), LandlordAddPropertyActivity.class);
                startActivityForResult(landlordAddPropertyIntent, Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY);
//                addPropertyItemIntoList("something new", tmp++, 12);
            }
        });
        this.refreshPropertyListView();
        if (this.propertyListAdapter.getCount() <= 0) {
            if (this.firstTime) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.VISIBLE);
            } else {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
            }
        }
    }

    private void refreshPropertyList() {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.clear();
    }

    private void addPropertyItemIntoList(String propertyName, int tenantCount, float payment) {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, tenantCount, payment));
    }

}
