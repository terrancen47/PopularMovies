package regulo.udacity.popularmovies.fragments.movies_popular;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import regulo.udacity.popularmovies.R;
import regulo.udacity.popularmovies.adapters.GridMovieAdapter;
import regulo.udacity.popularmovies.interfaces.IOnClickListener;
import regulo.udacity.popularmovies.listeners.RecyclerOnItemClickListener;
import regulo.udacity.popularmovies.listeners.RecyclerViewScrollListener;
import regulo.udacity.popularmovies.models.Movie;
import regulo.udacity.popularmovies.restclient.MovieRepositories;
import regulo.udacity.popularmovies.restclient.RestUtils;
import regulo.udacity.popularmovies.utilities.DeviceHelper;
import regulo.udacity.popularmovies.utilities.NetworkHelper;


public class PopularMovieFragment extends Fragment implements IPopularMovieContract.View, RecyclerOnItemClickListener{

    @BindView(R.id.recyclerview_movies_popular)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_progress)
    ProgressBar mProgress;

    private IOnClickListener mCallback;
    private PopularMoviePresenter mPresenter;
    private GridMovieAdapter mGridMovieAdapter;
    private GridLayoutManager mGridLayoutManager;

    public PopularMovieFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (IOnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + getString(R.string.must_implement));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PopularMoviePresenter(
                     this,
                     MovieRepositories.getInMemoryRepoInstance(RestUtils.createRestClient())
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_movie, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getPopularMovies(RestUtils.FIRST_PAGE, getContext());
    }

    private void setUI() {
        mGridLayoutManager = new GridLayoutManager(getContext(), DeviceHelper.calculateNoOfColumns(getContext()));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        NetworkHelper.addNoInternetView(mRecyclerView, getContext());
    }

    @Override
    public void onLoadedSuccess(List<Movie> movies) {
        mProgress.setVisibility(View.GONE);
        if(mGridMovieAdapter == null) {
            mGridMovieAdapter = new GridMovieAdapter(movies, this , getActivity());
        }else{
            mGridMovieAdapter.addNewItems(movies);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mGridMovieAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(mGridLayoutManager, mGridMovieAdapter));
    }

    @Override
    public void onLoadedFailure() {
        //Todo : Create a customize message.
    }

    @Override
    public void onItemClick(Movie movie) {
        mCallback.onItemSelected(movie);
    }
}
