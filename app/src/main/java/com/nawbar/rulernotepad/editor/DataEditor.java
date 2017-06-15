package com.nawbar.rulernotepad.editor;

import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;

import com.nawbar.rulernotepad.fragments.DetailsFragment;
import com.nawbar.rulernotepad.fragments.ProjectsFragment;

import java.util.List;

/**
 * Created by Bartosz Nawrot on 2017-06-15.
 */

public class DataEditor implements
        ProjectsFragment.ProjectsFragmentCommandsListener,
        DetailsFragment.DetailsFragmentCommandsListener {

    @Override
    public void onProjectAdd(String name) {

    }

    @Override
    public void onProjectRemove(String name) {

    }

    @Override
    public void onProjectSend(String name) {

    }

    @Override
    public List<String> getProjects() {
        return null;
    }

    @Override
    public void onPhotoAdd(String item) {

    }

    @Override
    public void onPhotoRemove(String item) {

    }

    @Override
    public void onPhotoEdit(String item) {

    }

    @Override
    public void onPhotoRename(String item) {

    }

    @Override
    public List<Pair<String, Drawable>> getItems() {
        return null;
    }
}
