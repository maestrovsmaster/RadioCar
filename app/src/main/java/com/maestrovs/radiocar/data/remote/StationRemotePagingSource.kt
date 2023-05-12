package com.maestrovs.radiocar.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maestrovs.radiocar.data.entities.Station
import javax.inject.Inject

class StationRemotePagingSource  @Inject constructor(
//private val stationRemoteDataSource: StationRemoteDataSource
private val stationService: StationService
):  PagingSource<Int, Station>() {




    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Station> {
        Log.d("Paging","load")

        val offset = params.key ?: 0 // Start from offset 0 if params.key is null
        val limit = params.loadSize // Use the load size provided by the PagingConfig

        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1

            Log.d("Paging","load.. offset = ${offset}  ,  limit = ${limit}")
            val response = stationService.getStations(offset = offset, limit = limit)


            val stations = response.body() ?: emptyList()

            Log.d("Paging","stations? = ${stations.size}")
            // Calculate the next key value
            val nextKey = if (stations.isEmpty()) null else offset + limit


            return LoadResult.Page(
                data = stations,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = nextKey
               // prevKey = null, // Only paging forward.
               // nextKey =  3//response.nextPageNumber
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            Log.d("Paging","load err ${e.localizedMessage}")
            return LoadResult.Error(e)
        }
    }



    override fun getRefreshKey(state: PagingState<Int, Station>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}
