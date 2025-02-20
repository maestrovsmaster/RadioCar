package com.maestrovs.radiocar.ui.app.stations_list

import android.util.Log
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.data.repository.StationRepositoryIml
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import kotlinx.coroutines.flow.first

/**
 * Created by maestromaster$ on 19/02/2025$.
 */

class StationPagingSource(
    private val repository: StationRepositoryIml,
    private val countryCode: String,
    private val searchQuery: String,
    private val tag: String
) : PagingSource<Int, StationGroup>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StationGroup> {
        val page = params.key ?: 0
        return try {
            val stations = repository.getPagedStations(
                country = countryCode,
                searchQuery = searchQuery,
                tag = tag,
                offset = page * params.loadSize,
                limit = params.loadSize
            )

            LoadResult.Page(
                data = stations,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (stations.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StationGroup>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
