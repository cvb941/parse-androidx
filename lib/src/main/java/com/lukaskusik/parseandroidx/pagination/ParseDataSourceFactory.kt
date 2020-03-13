package com.lukaskusik.parseandroidx.pagination

import androidx.paging.DataSource
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.ParseLiveQueryClient

class ParseDataSourceFactory<T : ParseObject>(
    private val query: ParseQuery<T>,
    private val parseClient: ParseLiveQueryClient = ParseLiveQueryClient.Factory.getClient()
) :
    DataSource.Factory<Int, T>() {

    override fun create(): DataSource<Int, T> {
        return ParsePositionalDataSource(query, parseClient)
    }
}