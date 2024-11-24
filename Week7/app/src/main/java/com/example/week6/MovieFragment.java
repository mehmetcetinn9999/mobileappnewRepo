package com.example.week6;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class  MovieFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int nColumnCount = 1;
    OnMovieSelected listener;
    List<Movie> movies = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int mColumnCount = getArguments().getInt("ARG_COLUMN_COUNT");
        }

        movies.add(new Movie("The Shawshank Redemption", "Frank Darabont", 1994,
                Arrays.asList("Tim Robbins", "Morgan Freeman", "Bob Gunton"),
                "Two imprisoned men bond over a number of years, " +
                        "finding solace and eventual redemption through acts of common decency."));
        movies.add(new Movie("The Godfather", "Francis Ford Coppola", 1972,
                Arrays.asList("Marlon Brando", "Al Pacino", "James Caan"),
                "The aging patriarch of an organized crime dynasty transfers control of his " +
                        "clandestine empire to his reluctant son."));
        movies.add(new Movie("Pulp Fiction", "Quentin Tarantino", 1994,
                Arrays.asList("John Travolta", "Uma Thurman", "Samuel L. Jackson"),
                "The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption."));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieSelected) {
            listener = (OnMovieSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieSelected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (nColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, nColumnCount));
            }
            recyclerView.setAdapter(new MyMovieRecyclerViewAdapter(movies, listener));

        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnMovieSelected {
        void movieSelected(Movie movie);
    }

}
