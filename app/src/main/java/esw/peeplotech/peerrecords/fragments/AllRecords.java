package esw.peeplotech.peerrecords.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.adapters.AllRecordAdapter;
import esw.peeplotech.peerrecords.databases.Database;
import esw.peeplotech.peerrecords.models.Record;

public class AllRecords extends Fragment {

    //widget
    private LinearLayout emptyLayout;
    private RecyclerView recordRecycler;
    private CardView refreshFab;
    private RelativeLayout connectLayout;
    private EditText searchEdt;

    //data
    private AllRecordAdapter adapter;
    private List<Record> recordList = new ArrayList<>();

    //timer
    private Timer timer;

    public AllRecords() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_records, container, false);

        //widgets
        emptyLayout = v.findViewById(R.id.emptyLayout);
        recordRecycler = v.findViewById(R.id.recordRecycler);
        refreshFab = v.findViewById(R.id.refreshFab);
        connectLayout = v.findViewById(R.id.connectLayout);
        searchEdt = v.findViewById(R.id.searchEdt);

        //init
        initialize();

        return v;
    }

    private void initialize() {

        //load stuff
        loadRecords();

        //attach listener to text
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {

                                searchQueriedRecord(s.toString());

                            });
                        }
                    }, 1000);
                } else {
                    loadRecords();
                }
            }
        });

        //refresh
        refreshFab.setOnClickListener(v -> {
            loadRecords();
        });

    }

    private void searchQueriedRecord(String query) {

        //clear list
        recordList.clear();

        //init recycler
        recordRecycler.setHasFixedSize(true);
        recordRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //init list
        recordList = new Database(getContext()).getQueriedRecords(query);

        //adapter
        adapter = new AllRecordAdapter(getActivity(), getContext(), recordList);
        recordRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void loadRecords() {

        recordList.clear();

        recordRecycler.setHasFixedSize(true);
        recordRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recordList = new Database(getContext()).getAllRecords();

        adapter = new AllRecordAdapter(getActivity(), getContext(), recordList);
        recordRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}