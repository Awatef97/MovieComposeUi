package com.example.moviecomposeui.feature.movieList.entity.pagingDataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviecomposeui.feature.movieList.MovieRepositoryImp
import com.example.moviecomposeui.feature.movieList.entity.MovieView
import com.example.moviecomposeui.feature.movieList.entity.toMovieView
import javax.inject.Inject

class TopRatedPagingDataSource
@Inject constructor(private val movieRepositoryImp: MovieRepositoryImp)
    : PagingSource<Int, MovieView>(){
    override fun getRefreshKey(state: PagingState<Int, MovieView>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieView> {
        return try {
            val nextPage = params.key ?: 1
            val movieListResponse = movieRepositoryImp.getTopRatedList(nextPage)

            LoadResult.Page(
                data = movieListResponse.results.map { it.toMovieView() },
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = movieListResponse.page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}