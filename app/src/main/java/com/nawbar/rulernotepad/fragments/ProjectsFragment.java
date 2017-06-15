package com.nawbar.rulernotepad.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nawbar.rulernotepad.R;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class ProjectsFragment extends ListFragment {

    public interface ProjectsFragmentListener {
        void onDetailsSelect(String name);
        ProjectsFragmentCommandsListener getProjectsCommandsListener();
    }

    public interface ProjectsFragmentCommandsListener {
        void onProjectAdd(String name);
        void onProjectRemove(String name);
        void onProjectSend(String name);
        List<String> getProjects();
    }

    private ProjectsFragmentListener listener;
    private ProjectsFragmentCommandsListener commandsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(ProjectsFragment.class.getSimpleName(), "onCreateView");

        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        setupButtons(view);

        return view;
    }

    void setupButtons(View view) {
        FloatingActionButton fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDetailsSelect("aaa");
                Snackbar.make(view, "Replace with your own action: fab_add", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_remove = (FloatingActionButton) view.findViewById(R.id.fab_remove);
        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action: fab_remove", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab_email = (FloatingActionButton) view.findViewById(R.id.fab_email);
        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action: fab_email", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProjectsFragmentListener) {
            listener = (ProjectsFragmentListener) context;
            commandsListener = listener.getProjectsCommandsListener();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProjectsFragment.ProjectsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
