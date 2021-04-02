package esw.peeplotech.peerrecords.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import esw.peeplotech.peerrecords.R;
import esw.peeplotech.peerrecords.adapters.StudentAdapter;
import esw.peeplotech.peerrecords.models.Student;
import esw.peeplotech.peerrecords.util.Common;
import io.paperdb.Paper;

public class MyRecords extends Fragment {

    //widget
    private RecyclerView userRecycler;

    //data
    private StudentAdapter adapter;
    private List<Student> studentList;

    public MyRecords() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_records, container, false);

        //widget
        userRecycler = v.findViewById(R.id.userRecycler);

        //init
        initialize();

        return v;
    }

    private void initialize() {

        //load students
        loadStudents();

    }

    private void loadStudents() {

        //init recycler
        userRecycler.setHasFixedSize(true);
        userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //populate
        studentList = Paper.book().read(Common.STUDENTS_LIST);

        //adapter
        adapter = new StudentAdapter(getActivity(), getContext(), studentList);
        userRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}